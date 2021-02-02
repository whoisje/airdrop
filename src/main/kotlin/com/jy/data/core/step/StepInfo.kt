package com.jy.data.core.step

import com.google.gson.JsonObject
import com.jy.data.core.constant.enums.Status
import com.jy.data.core.constant.enums.StepType

/**
 * @Author Je.Wang
 * @Date 2021/2/2 9:52
 */
data class StepInfo(
    val name: String,
    val id: String,
    var option: JsonObject,
    var inCount: Long = 0,
    var outCount: Long = 0,
    var errorCount: Long = 0,
    var status: Int = Status.IDLE.status,
    var stepType: Int = StepType.NORMAL.type,
    //目标步骤
    val targets: List<String> = listOf(),
    val from: List<String> = listOf(),
)
