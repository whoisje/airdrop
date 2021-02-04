package com.jy.data.core.step.input

import cn.hutool.db.sql.NamedSql
import cn.hutool.db.sql.SqlExecutor
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import com.jy.data.core.constant.enums.DbFetchSize
import com.jy.data.core.constant.enums.DbType
import com.jy.data.core.db.AirDSFactory
import com.jy.data.core.expand.toRow
import com.jy.data.core.row.Row
import com.jy.data.core.step.Step
import com.jy.data.core.step.StepInfo
import kotlinx.coroutines.runBlocking
import java.sql.PreparedStatement


/**
 * @Author Je.Wang
 * @Date 2021/2/4 11:26
 */
class TableInput(
    info: StepInfo,
) : Step(info) {
    private var state: TableInputState = Gson().fromJson(info.state)
    private var prop: TableInputProp = Gson().fromJson(info.option)
    private var prepareStatement: PreparedStatement;

    init {
        val ds = AirDSFactory.get(prop.dbId)
        val connection = ds.connection
        val fetchSize = DbFetchSize.valueOf(
            DbType.productOf(connection.metaData.databaseProductName).name
        ).fetchSize
        prepareStatement = connection.prepareStatement(prop.sql)
        prepareStatement.fetchSize = fetchSize
    }

    override suspend fun processRow(inputRow: Row) {
        val namedSql = NamedSql(prop.sql, inputRow.data)
        SqlExecutor.queryAndClosePs(
            prepareStatement, {
                runBlocking {
                    val metaData = it.metaData
                    var index = 0;
                    while (it.next() && (index < prop.querySize || prop.queryAll)) {
                        if (index < state.count) continue//跳过上次已经取过的数据
                        state.count++
                        index++
                        putRow(it.toRow(metaData))
                    }
                }
            },
            *namedSql.params
        )
    }
}

data class TableInputProp(
    val sql: String,
    val querySize: Long = 1000,
    val queryAll: Boolean = false,
    val dbId: String,
)

data class TableInputState(
    var count: Long,
)