package com.jy.data.core.step

import com.jy.data.core.constant.enums.Status
import com.jy.data.core.row.EOF_ROW
import com.jy.data.core.row.Row
import kotlinx.coroutines.channels.Channel

/**
 * @Author Je.Wang
 * @Date 2021/2/2 12:30
 */
/**
 * TODO 顺序执行优先级
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
    var currentRow: Row? = null;

    /**
     * 执行步骤，返回值为步骤停止时的回调函数
     */
    abstract suspend fun processRow(inputRow: Row)

    /**
     * 保证current row是当前输入row
     * 保证第一步getRow
     */
    suspend fun process() {
        val inputRow = getRow() ?: return
        processRow(inputRow)
    }

    open fun onStop() {

    }

    fun status(): Status {
        return Status.statusOf(this.info.status)
    }

    fun status(status: Status) {
        this.info.status = status.status
    }

    /**
     * 任何步骤必须getRow
     */
    suspend fun getRow(): Row? {
        if (receiveChannel == null) {
            currentRow = Row()
            return currentRow
        }
        val row = receiveChannel!!.receive()
        currentRow = row
        println("get $row")
        //标记作用，channel为空的时候会堵塞协程，用EOF标记判断而不堵塞
        if (row.isEOFRow()) {
            return null
        }
        info.inCount++
        return row
    }

    open fun hasNext(): Boolean {
        return currentRow?.run {
            return@run !isEOFRow()
        } ?: true
    }

    suspend fun putRow(row: Row) {
        for (sendChannel in sendChannels) {
            if (!row.isEOFRow()) {
                info.outCount++
            }
            println("put row")
            sendChannel.value.send(row)
        }
    }

    suspend fun complete() {
        currentRow = EOF_ROW
        putRow(EOF_ROW)
        putErrorRow(EOF_ROW)
        onStop()
        status(Status.IDLE)
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