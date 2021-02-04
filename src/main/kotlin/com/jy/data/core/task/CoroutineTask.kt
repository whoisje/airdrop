package com.jy.data.core.task

import com.jy.data.core.constant.DEFAULT_CHANNEL_SIZE
import com.jy.data.core.constant.enums.Status
import com.jy.data.core.constant.enums.StepType
import com.jy.data.core.event.RxBus
import com.jy.data.core.event.StepProcessExceptionEvent
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.slf4j.LoggerFactory

class CoroutineTask constructor(info: TaskInfo) {
    var steps = info.steps.map { it.info.id to it }.toMap()
    private var stepJobs = mutableMapOf<String, Job>()

    private val logger = LoggerFactory.getLogger(CoroutineTask::class.java)
    private val subscribes = mutableListOf<Disposable>()

    /**
     * 用channel串联步骤
     */
    fun buildStepClainAndChannel() {
        steps.forEach { (_, step) ->
            step.info.targets.forEach { target ->
                steps[target]?.let { targetStep ->
                    val receiveChannel = targetStep.receiveChannel
                    if (receiveChannel == null) {
                        targetStep.receiveChannel = Channel(DEFAULT_CHANNEL_SIZE)
                    }
                    when (StepType.typeOf(targetStep.info.stepType)) {
                        StepType.NORMAL -> step.sendChannels[target] = targetStep.receiveChannel!!
                        StepType.ERROR -> step.errorChannels[target] = targetStep.receiveChannel!!
                    }
                }
            }
        }
    }

    suspend fun run() {
        coroutineScope {
            steps.forEach { (key, step) ->
                stepJobs[key] = launch {
                    while (isActive) {
                        if (step.hasNext()) {
                            try {
                                step.process()
                            } catch (ignore: CancellationException) {
                            } catch (e: Exception) {
                                //没有异常处理步骤，停任务
                                if (step.errorChannels.isEmpty()) {
                                    step.info.errorCount++
                                    this@CoroutineTask.cancel()
                                } else {
                                    step.putErrorRow(step.currentRow!!)
                                }
                                //广播异常消息
                                RxBus.instance
                                    .post(StepProcessExceptionEvent(step.info, e))
                            }
                        } else {
                            step.complete()
                            step.onStop()
                            step.status(Status.IDLE)
                            break
                        }
                    }

                }
            }
        }
    }


    suspend fun cancel() {
        stepJobs.forEach { (key, job) ->
            steps[key]?.let { step ->
                if (step.status() != Status.IDLE) {
                    step.onStop()
                    step.status(Status.IDLE)
                }
            }
            if (!job.isCancelled) {
                job.cancelAndJoin()
            }
        }
        unSubscribe()
    }

    private fun unSubscribe() {
        subscribes.forEach {
            if (!it.isDisposed) {
                it.dispose()
            }
        }
    }

    private fun registerStepErrorHandler() {
        val subscribe = RxBus.instance.toObservable(StepProcessExceptionEvent::class.java)
            .subscribe {
                println(it)
            }
        subscribes.add(subscribe)
    }

}