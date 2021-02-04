package com.jy.data.core.expand

import com.jy.data.core.row.Row
import com.jy.data.core.row.RowMeta
import java.sql.ResultSet
import java.sql.ResultSetMetaData

fun ResultSet.toRow(
    meta: ResultSetMetaData
): Row {
    val row = Row()
    for (i in 0 until meta.columnCount) {
        val rowMeta = RowMeta(
            name = meta.getColumnLabel(i),
            type = meta.getColumnType(i),
            scale = meta.getScale(i),
            precision = meta.getPrecision(i),
        )
        row.put(rowMeta, getObject(rowMeta.name))
    }
    return row
}