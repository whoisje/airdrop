package com.jy.data.core.constant.enums

enum class DbType(val product: String) {
    ORACLE("ORACLE"),
    MYSQL("MYSQL"),
    SQLSERVER("Microsoft SQL Server");

    companion object {
        fun productOf(product: String): DbType {
            return values().firstOrNull { it.product == product }
                ?: throw IllegalArgumentException("数据库类型${product}不存在")
        }
    }
}