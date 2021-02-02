package com.jy.data.core.constant.enums

enum class Status(var status: Int) {
    IDLE(0), RUNNING(1), ERROR(2);

    companion object {
        fun statusOf(status: Int): Status {
            return values().firstOrNull { it.status == status } ?: throw IllegalArgumentException("状态${status}不存在")
        }
    }
}