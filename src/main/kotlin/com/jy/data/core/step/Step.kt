package com.jy.data.core.step

import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.kotlin.Flowables
import io.reactivex.rxjava3.kotlin.toFlowable
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
    }.groupBy {
            it.name
        }
        .delay(1000,TimeUnit.DAYS)
        .subscribe { flow ->
            flow
                .groupBy { it.age }
                .subscribe { newFlow ->
                    newFlow
                        .filter { it.age == 2 }
                        .doOnEach(subscriber)
                        .subscribe()
                }
        }
}

val subscriber = object : Subscriber<Item> {


    override fun onNext(item: Item?) {
        println(item)
        println(Thread.currentThread())
    }

    override fun onError(throwable: Throwable?) {
    }

    override fun onComplete() {
    }

    override fun onSubscribe(s: Subscription?) {
    }

}