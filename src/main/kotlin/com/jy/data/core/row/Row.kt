package com.jy.data.core.row

data class Row(
    var meta: MutableMap<String, RowInfo> = mutableMapOf(),
    var header: MutableMap<String, String> = mutableMapOf(),
    var data: MutableMap<String, Any> = mutableMapOf(),
) {
    operator fun get(key: String): Any? {
        return data[key]
    }

    fun put(key: String, rowInfo: RowInfo, value: Any) {
        data[key] = value
        meta[key] = rowInfo
    }
}

data class RowInfo(
    val type: Int,
    val name: String,
    val scale: Int,
    val precision: Int,
    val primary: Boolean
)