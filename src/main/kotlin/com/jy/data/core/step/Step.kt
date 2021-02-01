package com.jy.data.core.step

import io.reactivex.rxjava3.kotlin.toFlowable
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription

interface Step {
    fun processRow() {
        listOf(1, 2, 3)
                .toFlowable()
                .map { it.toString() }
                .doOnEach(subscriber)
                .subscribe {
                    println(it)
                }
    }
}

val subscriber = object : Subscriber<String> {

    override fun onNext(item: String?) {
        println(item)
    }

    override fun onError(throwable: Throwable?) {
        TODO("Not yet implemented")
    }

    override fun onComplete() {
        TODO("Not yet implemented")
    }

    override fun onSubscribe(s: Subscription?) {
        TODO("Not yet implemented")
    }

}