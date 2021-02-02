package com.jy.data.core.event

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject

class RxBus private constructor() {
    private val bus: Subject<Any> = PublishSubject.create<Any>().toSerialized()

    /**
     * 发送事件
     * @param data
     */
    fun post(data: Any) {
        bus.onNext(data)
    }

    /**
     * 根据类型接收相应类型事件
     * @param eventType
     * @param <T>
     * @return
    </T> */
    fun <T> toObservable(eventType: Class<T>): Observable<T> {
        return bus.ofType(eventType)
    }

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { RxBus() }
    }

}