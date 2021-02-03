package com.jy.data.core.step

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
    var hasMore = true

    /**
     * 记录当前正在处理的row
     */
    var currentRow: Row? = null;

    /**
     * 执行步骤，返回值为步骤停止时的回调函数
     */
    abstract suspend fun process()
    open fun onStop() {

    }

    //标记作用，channel为空的时候会堵塞协程，用标记判断而不堵塞
    suspend fun getRow(): Row? {
        if (receiveChannel == null) {
            return Row()
        }
        val row = receiveChannel!!.receive()
        if (row.isEOFRow()) {
            this.markNoMore()
            return null
        }
        info.inCount++
        currentRow = row
        return row
    }

    suspend fun putRow(row: Row) {
        for (sendChannel in sendChannels) {
            if (!row.isEOFRow()) {
                info.outCount++
            }
            sendChannel.value.send(row)
        }
    }

    suspend fun finish() {
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

    fun markNoMore() {
        this.hasMore = false
    }

    suspend fun putRowTo(target: String, row: Row) {
        if (!row.isEOFRow()) {
            info.outCount++
        }
        sendChannels[target]?.send(row)
    }
}