package com.jy.data.core.task

import com.jy.data.core.constant.DEFAULT_CHANNEL_SIZE
import com.jy.data.core.row.Row
import com.jy.data.core.step.Step
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

class CoroutineTask constructor(info: TaskInfo, steps: MutableMap<String, Step>) {
    //串行or并行
    private var steps = mutableMapOf<String, Step>()
    private var stepJobs = mutableMapOf<String, Job>()

    init {
        this.steps = steps;
    }

    private var cacheSize = DEFAULT_CHANNEL_SIZE;


    fun buildStepClainAndChannel() {
        steps.forEach { (_, step) ->
            step.info.targets.forEach { target ->
                steps[target]?.let { tStep ->
                    val receiveChannel = tStep.receiveChannel
                    if (receiveChannel == null) {
                        val channel = Channel<List<Row>>(cacheSize)
                        tStep.receiveChannel = channel
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
        steps.forEach { (t, u) ->
            stepJobs[t] = GlobalScope.launch {
                if (isActive) {
                    u.process()
                }
            }
        }
    }

    fun cancel(target: String) {
        stepJobs[target]?.let {
            runBlocking {
                it.cancelAndJoin()
            }
        }
    }

    fun cancel() {
        stepJobs.forEach { (_, u) ->
            runBlocking {
                u.cancelAndJoin()
            }
        }
    }

}