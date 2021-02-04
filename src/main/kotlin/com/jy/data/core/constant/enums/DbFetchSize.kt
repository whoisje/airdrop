package com.jy.data.core.constant.enums

enum class DbFetchSize(val fetchSize: Int) {
    ORACLE(100),
    MYSQL(Int.MIN_VALUE),
    SQLSERVER(100)
}