package com.jy.data.core.step.filter

import com.jy.data.core.row.Row
import com.jy.data.core.step.Step
import com.jy.data.core.step.StepInfo

/**
 * @Author Je.Wang
 * @Date 2021/2/2 11:29
 */
class FlowFilter(
    info: StepInfo,
) : Step(info) {
    override suspend fun processRow(inputRow: Row) {
    }

}