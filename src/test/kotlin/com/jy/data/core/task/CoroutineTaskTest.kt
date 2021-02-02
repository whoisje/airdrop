package com.jy.data.core.task

import com.google.gson.JsonObject
import com.jy.data.core.constant.enums.StepType
import com.jy.data.core.step.StepInfo
import com.jy.data.core.step.filter.FlowFilter
import com.jy.data.core.step.input.GenerateRow
import org.junit.jupiter.api.Test

internal class CoroutineTaskTest {

    @Test
    fun start() {
        val generateRow = GenerateRow(StepInfo("generate_row", "1", JsonObject(), targets = listOf("2", "3")))
        val filter = FlowFilter(StepInfo("filter", "2", JsonObject(), stepType = StepType.ERROR.type))
        val filter2 = FlowFilter(StepInfo("filter", "3", JsonObject()))
        val coroutineTask = CoroutineTask(
            TaskInfo("test", "1", JsonObject()),
            mutableMapOf(
                "1" to generateRow,
                "2" to filter,
                "3" to filter2
            )
        )
        coroutineTask.buildStepClainAndChannel()
        coroutineTask.start()
        Thread.sleep(4000)
        coroutineTask.cancel()
    }
}