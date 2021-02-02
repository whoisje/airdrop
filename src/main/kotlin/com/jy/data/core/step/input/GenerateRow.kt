package com.jy.data.core.step.input

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
    override suspend fun process() {
        putRows(listOf(Row(data = mutableMapOf("name" to "name"))))
    }

}