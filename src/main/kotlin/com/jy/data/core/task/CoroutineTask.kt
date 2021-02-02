package com.jy.data.core.task

import com.jy.data.core.constant.DEFAULT_CHANNEL_SIZE
import com.jy.data.core.constant.enums.StepType
import com.jy.data.core.event.RxBus
import com.jy.data.core.event.StepExceptionEvent
import com.jy.data.core.step.Step
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.slf4j.LoggerFactory

class CoroutineTask constructor(info: TaskInfo, steps: MutableMap<String, Step>) {
    //串行or并行
    private var steps = mutableMapOf<String, Step>()
    private var stepJobs = mutableMapOf<String, Job>()
    private var unloadFunc = mutableMapOf<String, () -> Unit>()
    private val logger = LoggerFactory.getLogger(CoroutineTask::class.java)

    init {
        this.steps = steps;
    }

    private var cacheSize = DEFAULT_CHANNEL_SIZE;

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
                        targetStep.receiveChannel = Channel(cacheSize)
                    }
                    when (StepType.typeOf(targetStep.info.stepType)) {
                        StepType.NORMAL -> step.sendChannels[target] = targetStep.receiveChannel!!
                        StepType.ERROR -> step.errorChannels[target] = targetStep.receiveChannel!!
                    }
                }
            }
        }
    }

    fun start() {
        steps.forEach { (key, step) ->
            stepJobs[key] = GlobalScope.launch {
                while (isActive) {
                    try {
                        val unload = step.process()
                        unloadFunc.putIfAbsent(key, unload)
                    } catch (e: Exception) {
                        RxBus.instance.post(StepExceptionEvent(step.info, step.currentRows, e))
                    }
                }
            }
        }
    }

    fun cancel(target: String) {
        stepJobs[target]?.let {
            runBlocking {
                unloadFunc[target]?.invoke()
                it.cancelAndJoin()
            }
        }
    }

    fun cancel() {
        stepJobs.forEach { (key, job) ->
            runBlocking {
                job.cancelAndJoin()
                //调用销毁回调
                unloadFunc[key]?.invoke()
            }
        }
    }

    fun handleStepError() {
        RxBus.instance.toObservable(StepExceptionEvent::class.java)
            .subscribe {
                GlobalScope.launch {
                    steps[it.info.id]?.putErrorRows(it.rows)
                }
            }
    }

}