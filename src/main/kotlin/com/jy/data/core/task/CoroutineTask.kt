package com.jy.data.core.task

import com.jy.data.core.constant.DEFAULT_CHANNEL_SIZE
import com.jy.data.core.constant.enums.StepType
import com.jy.data.core.event.RxBus
import com.jy.data.core.event.StepProcessExceptionEvent
import com.jy.data.core.step.Step
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

class CoroutineTask constructor(info: TaskInfo) {
    //串行or并行
    public var steps = mutableMapOf<String, Step>()
    private var stepJobs = mutableMapOf<String, Job>()

    //    private val logger = LoggerFactory.getLogger(CoroutineTask::class.java)
    private val subscribes = mutableListOf<Disposable>()

    init {
        registerStepErrorHandler()
        //TODO 步骤应该由task info转换而来，而不是直接传进来
//        this.steps = Gson().fromJson(info.option);
    }

    /**
     * TODO 把属性转换为步骤对象
     */
    fun convertOptionToStep() {

    }

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
                    try {
                        while (isActive) {
                            if (step.hasMore) {
                                step.process()
                            } else {
                                step.finish()
                                break
                            }
                        }
                    } catch (ignore: CancellationException) {
                    } catch (e: Exception) {
                        RxBus.instance.post(StepProcessExceptionEvent(step.info, e))
                    } finally {
                        //TODO 处理销毁回调异常？
                        step.onStop()
                    }
                }
            }
        }
    }


    suspend fun cancel() {
        stepJobs.forEach { (_, job) ->
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
                val step = steps[it.info.id]!!
                //没有异常处理步骤，停任务
                if (step.errorChannels.isEmpty()) {
                    step.info.errorCount++
                    GlobalScope.launch {
                        this@CoroutineTask.cancel()
                    }
                } else {
                    runBlocking {
                        step.putErrorRow(step.currentRow!!)
                    }
                }
            }
        subscribes.add(subscribe)
    }

}