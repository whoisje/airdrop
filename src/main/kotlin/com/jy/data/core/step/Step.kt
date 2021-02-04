package com.jy.data.core.step

import com.jy.data.core.constant.enums.Status
import com.jy.data.core.row.EOF_ROW
import com.jy.data.core.row.Row
import kotlinx.coroutines.channels.Channel

/**
 * @Author Je.Wang
 * @Date 2021/2/2 12:30
 */
abstract class Step(
    var info: StepInfo,
) {
    var receiveChannel: Channel<Row>? = null
    var sendChannels: MutableMap<String, Channel<Row>> = mutableMapOf()
    var errorChannels: MutableMap<String, Channel<Row>> = mutableMapOf()

    /**
     * 记录当前正在处理的row
     */
    var currentRow: Row? = null

    /**
     * 执行步骤，返回值为步骤停止时的回调函数
     */
    abstract suspend fun processRow(inputRow: Row)

    /**
     * 保证current row是当前输入row
     */
    suspend fun process() {
        val channel = receiveChannel
        if (channel == null) {
            val row = Row()
            currentRow = row
            processRow(row)
            complete()
            return
        }
        for (row in channel) {
            if (row.isEOFRow()) {
                complete()
                return
            }
            currentRow = row
            info.inCount++
            processRow(row)
        }
    }

    open fun onStop() {

    }

    fun status(): Status {
        return Status.statusOf(this.info.status)
    }

    fun status(status: Status) {
        this.info.status = status.status
    }

    fun hasNext(): Boolean {
        return currentRow?.run {
            return@run !isEOFRow()
        } ?: true
    }

    suspend fun putRow(row: Row) {
        for (sendChannel in sendChannels) {
            if (!row.isEOFRow()) {
                info.outCount++
            }
            sendChannel.value.send(row)
        }
    }

    suspend fun complete() {
        currentRow = EOF_ROW
        putRow(EOF_ROW)
        putErrorRow(EOF_ROW)
    }

    suspend fun putErrorRow(row: Row) {
        for (channel in errorChannels) {
            if (!row.isEOFRow()) {
                info.errorCount++
            }
            channel.value.send(row)
            //TODO 这里是send是拷贝还是引用？引用可能会有问题。
        }
    }


    suspend fun putRowTo(target: String, row: Row) {
        if (!row.isEOFRow()) {
            info.outCount++
        }
        sendChannels[target]?.send(row)
    }
}