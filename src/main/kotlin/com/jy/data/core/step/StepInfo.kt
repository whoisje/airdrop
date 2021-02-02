package com.jy.data.core.step

import com.google.gson.JsonObject

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
    //目标步骤
    val targets: List<String> = listOf(),
    val from: List<String> = listOf(),
)
