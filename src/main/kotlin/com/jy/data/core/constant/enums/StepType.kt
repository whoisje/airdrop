package com.jy.data.core.constant.enums

enum class StepType(val type: Int) {
    NORMAL(0), ERROR(-1);

    companion object {
        fun typeOf(type: Int): StepType {
            return values().firstOrNull { it.type == type } ?: throw IllegalArgumentException("任务类型${type}不存在")
        }
    }
}