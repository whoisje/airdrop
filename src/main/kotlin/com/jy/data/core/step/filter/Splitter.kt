package com.jy.data.core.step.filter

import com.jy.data.core.step.Step
import com.jy.data.core.step.StepInfo

/**
 * @Author Je.Wang
 * @Date 2021/2/2 10:34
 */
class Splitter(
    info: StepInfo,
) : Step(info) {
    override suspend fun process(): () -> Unit {
        return {

        }
    }

}