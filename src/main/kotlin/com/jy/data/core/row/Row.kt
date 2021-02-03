package com.jy.data.core.row

val EOF_ROW = Row(
    mutableMapOf(),
    mutableMapOf(
        RowHeaderKey.IS_LAST to true
    ),
    mutableMapOf()
)

data class Row(
    var meta: MutableMap<String, RowMeta>,
    var header: MutableMap<RowHeaderKey, Any>,
    var data: MutableMap<String, Any>,
) {
    constructor() : this(mutableMapOf(), mutableMapOf(), mutableMapOf()) {
        header[RowHeaderKey.TIMESTAMP] = System.currentTimeMillis();
    }

    operator fun get(key: String): Any? {
        return data[key]
    }

    /**
     * 是否是结束标志row
     */
    fun isEOFRow(): Boolean {
        val isLast = getHeader<Any?>(RowHeaderKey.IS_LAST)
        return isLast != null && isLast as Boolean
    }

    inline fun <reified T> getHeader(key: RowHeaderKey): T {
        return header[key] as T
    }

    fun put(rowMeta: RowMeta, value: Any) {
        data[rowMeta.name] = value
        meta[rowMeta.name] = rowMeta
    }
}

enum class RowHeaderKey {
    TIMESTAMP,
    TYPE,
    IS_LAST
}

enum class ValueType(val type: Int) {
    NUMBER(0), STRING(1), ARRAY(1), BOOLEAN(3);

    companion object {
        fun typeOf(type: Int): ValueType {
            return values().firstOrNull { it.type == type } ?: throw IllegalArgumentException("数据类型${type}不存在")
        }
    }
}

data class RowMeta(
    val name: String,
    val type: Int = 1,
    val length: Int = 0,
    val scale: Int = 0,
    val precision: Int = 0,
    val primary: Boolean = false
)