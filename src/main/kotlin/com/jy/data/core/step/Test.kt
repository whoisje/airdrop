package com.jy.data.core.step

import com.google.gson.JsonObject
import com.jy.data.core.step.filter.FlowFilter
import com.jy.data.core.step.input.GenerateRow
import com.jy.data.core.task.CoroutineTask
import com.jy.data.core.task.TaskInfo


fun main() {
    val generateRow = GenerateRow(StepInfo("generate_row", "1", JsonObject(), targets = listOf("2", "3")))
    val filter = FlowFilter(StepInfo("filter", "2", JsonObject()))
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
    coroutineTask.cancel()
}
