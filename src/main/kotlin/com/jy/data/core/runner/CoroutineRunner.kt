package com.jy.data.core.runner

import kotlinx.coroutines.*

/**
 * @Author Je.Wang
 * @Date 2021/2/1 19:43
 */
class CoroutineRunner {
    private lateinit var job: Job
    fun start() {
        GlobalScope.launch(Dispatchers.Default) {

        }
    }

    suspend fun cancel() {
        job.cancelAndJoin()
    }
}