package com.jy.data.core.task

import com.jy.data.core.constant.DEFAULT_CHANNEL_SIZE
import com.jy.data.core.constant.enums.StepType
import com.jy.data.core.event.RxBus
import com.jy.data.core.event.StepProcessExceptionEvent
import com.jy.data.core.step.Step
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.slf4j.LoggerFactory

class CoroutineTask constructor(info: TaskInfo, steps: MutableMap<String, Step>) {
    //串行or并行
    private var steps = mutableMapOf<String, Step>()
    private var stepJobs = mutableMapOf<String, Job>()
    private var unloadFunc = mutableMapOf<String, () -> Unit>()
    private val logger = LoggerFactory.getLogger(CoroutineTask::class.java)
    private val subscribes = mutableListOf<Disposable>()

    init {
        registerStepErrorHandler()
        //TODO 步骤应该由task info转换而来，而不是直接传进来
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
                while (isActive && step.hasMore) {
                    try {
                        val unload = step.process()
                        unloadFunc.putIfAbsent(key, unload)
                    } catch (e: Exception) {
                        RxBus.instance.post(StepProcessExceptionEvent(step.info, e))
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
                //TODO 处理销毁回调异常？
            }
        }
        unSubscribe()
    }

    private fun unSubscribe() {
        subscribes.forEach {
            it.dispose()
        }
    }

    private fun registerStepErrorHandler() {
        val subscribe = RxBus.instance.toObservable(StepProcessExceptionEvent::class.java)
            .subscribe {
                //TODO 这里需要同步操作
                val step = steps[it.info.id]!!
                if (step.errorChannels.isEmpty()) {
                    this.cancel()
                } else {
                    GlobalScope.launch {
                        step.putErrorRows(step.currentRows)
                    }
                }
            }
        subscribes.add(subscribe)
    }

}