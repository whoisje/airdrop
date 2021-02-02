package com.jy.data.core.step

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
    var receiveChannel: Channel<List<Row>>? = null
    var sendChannels: MutableMap<String, Channel<List<Row>>> = mutableMapOf()
    abstract suspend fun process()
    suspend fun getRows(): List<Row> {
        return receiveChannel?.receive() ?: listOf()
    }

    suspend fun putRows(rows: List<Row>) {
        for (sendChannel in sendChannels) {
            sendChannel.value.send(rows)
        }
    }

    suspend fun putRowsTo(target: String, rows: List<Row>) {
        sendChannels[target]?.send(rows)
    }
}