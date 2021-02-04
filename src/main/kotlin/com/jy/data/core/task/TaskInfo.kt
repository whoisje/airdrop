package com.jy.data.core.task

import com.google.gson.JsonObject
import com.jy.data.core.constant.enums.Status
import com.jy.data.core.step.Step

/**
 * @Author Je.Wang
 * @Date 2021/2/2 16:19
 */
data class TaskInfo(
    val name: String,
    val id: String,
    val steps: List<Step>,
    var option: JsonObject,
    var status: Int = Status.IDLE.status,
)
