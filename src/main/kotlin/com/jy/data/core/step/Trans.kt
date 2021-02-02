package com.jy.data.core.step

import com.jy.data.core.row.Row
import io.reactivex.rxjava3.core.Flowable

abstract class Trans<T>(
    val flowable: Flowable<Row>,
    var info: StepInfo
) {
    /**
     * 筛选函数
     */
    abstract fun keySelector(): (Row) -> T

    /**
     * 执行转换
     */
    abstract fun trans(value: Row): Flowable<Row>
}