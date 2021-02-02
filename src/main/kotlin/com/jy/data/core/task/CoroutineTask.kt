package com.jy.data.core.task

import com.jy.data.core.constant.DEFAULT_CHANNEL_SIZE
import com.jy.data.core.row.Row
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
     * 用channel串联步骤
     */
    fun buildStepClainAndChannel() {
        steps.forEach { (_, step) ->
            step.info.targets.forEach { target ->
                steps[target]?.let { targetStep ->
                    val receiveChannel = targetStep.receiveChannel
                    if (receiveChannel == null) {
                        val channel = Channel<List<Row>>(cacheSize)
                        targetStep.receiveChannel = channel
                        step.sendChannels[target] = channel
                    } else {
                        val channel: Channel<List<Row>> = receiveChannel;
                        step.sendChannels[target] = channel
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
                        logger.error(e.message, e)
                        step.info.errorCount++
                        this@CoroutineTask.cancel()
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

}