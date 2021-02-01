package com.jy.data.core.operator

import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.kotlin.Flowables

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
    }.groupBy { it.age }.subscribe { flow ->
        Flowables.create<Item>(BackpressureStrategy.BUFFER) { emit ->
            println("create flow")
            flow.subscribe { item ->
                emit.onNext(item)
            }
        }.subscribe { println(it) }
    }

}
