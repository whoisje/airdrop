package com.jy.data.core.step.input

import com.jy.data.core.row.Row
import com.jy.data.core.step.Step
import com.jy.data.core.step.StepInfo

/**
 * @Author Je.Wang
 * @Date 2021/2/4 11:26
 */
class TableInput(
    info: StepInfo,
) : Step(info) {
    override suspend fun processRow(inputRow: Row) {
    }
}

fun main() {

}