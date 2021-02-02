package com.jy.data.core.step.input

import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import com.jy.data.core.row.Row
import com.jy.data.core.step.Step
import com.jy.data.core.step.StepInfo

/**
 * @Author Je.Wang
 * @Date 2021/2/1 20:18
 */
class GenerateRow(
    info: StepInfo,
) : Step(info) {
    private lateinit var state: GenerateRowState;
    private var prop: GenerateRowProp = Gson().fromJson(info.option);
    init {

    }

    override suspend fun process() {
        putRows(listOf(Row(data = mutableMapOf("name" to "name"))))
    }

}

data class GenerateRowState(
    var index: Int = 0,
)

data class GenerateRowProp(
    var count: Int = 0,
)