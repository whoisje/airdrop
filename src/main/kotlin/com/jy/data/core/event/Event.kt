package com.jy.data.core.event

import com.jy.data.core.row.Row
import com.jy.data.core.step.StepInfo
import com.jy.data.core.task.TaskInfo


data class StepEvent(
    val info: StepInfo,
    val timestamp: Long = System.currentTimeMillis()
)

data class TaskEvent(
    val task: TaskInfo,
    val timestamp: Long = System.currentTimeMillis()
)

data class ExceptionEvent(
    val exception: Exception,
    val timestamp: Long = System.currentTimeMillis()
)

data class StepExceptionEvent(
    val info: StepInfo,
    val rows: List<Row>,
    val exception: Exception,
    val timestamp: Long = System.currentTimeMillis()
)

data class TaskExceptionEvent(
    val task: TaskInfo,
    val exception: Exception,
    val timestamp: Long = System.currentTimeMillis()
)
