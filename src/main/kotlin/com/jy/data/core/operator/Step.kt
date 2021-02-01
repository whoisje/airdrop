package com.jy.data.core.operator

import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.kotlin.Flowables
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import java.util.concurrent.TimeUnit

interface Step {
    fun processRow() {
    }
}

data class Item(val name: String, val age: Int)

fun main() {
    Flowables.create<Item>(BackpressureStrategy.BUFFER) { emitter ->
        listOf(Item("a", 2), Item("a", 2), Item("b", 1))
            .forEach { item ->
                emitter.onNext(item);
            }
    }.subscribe(subscriber)
    subscriber
}

val subscriber = object : Subscriber<Item> {
    lateinit var subscription: Subscription;
    override fun onSubscribe(sub: Subscription) {
        this.subscription = sub
        sub.request(4)
    }

    override fun onNext(item: Item?) {
        println(item)
    }

    override fun onError(throwable: Throwable?) {
    }

    override fun onComplete() {
    }

}