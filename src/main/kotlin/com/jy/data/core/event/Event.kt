package com.jy.data.core.event

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

data class StepProcessExceptionEvent(
    val info: StepInfo,
    val exception: Exception,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * 没有异常处理步骤时的异常事件
 */
data class StepExceptionEvent(
    val info: StepInfo,
    val exception: Exception,
    val timestamp: Long = System.currentTimeMillis()
)

data class TaskExceptionEvent(
    val task: TaskInfo,
    val exception: Exception,
    val timestamp: Long = System.currentTimeMillis()
)
