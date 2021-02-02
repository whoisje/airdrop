package com.jy.data.core.task

import com.google.gson.JsonObject

/**
 * @Author Je.Wang
 * @Date 2021/2/2 16:19
 */
data class TaskInfo(val name: String,
                    val id: String,
                    var option: JsonObject)
