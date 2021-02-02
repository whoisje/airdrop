package com.jy.data.core.expand

import com.jy.data.core.row.Row
import com.jy.data.core.step.StepInfo
import io.reactivex.rxjava3.core.FlowableEmitter

/**
 * @Author Je.Wang
 * @Date 2021/2/2 10:14
 */
fun FlowableEmitter<Row>.onNext(value: Row, info: StepInfo) {
    info.outCount++
    this.onNext(value)
}

fun FlowableEmitter<Row>.onMore(values: List<Row>, info: StepInfo) {
    values.forEach { this.onNext(it);info.outCount++ }
}