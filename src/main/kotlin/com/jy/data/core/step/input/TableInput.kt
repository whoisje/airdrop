package com.jy.data.core.step.input

import cn.hutool.db.sql.NamedSql
import cn.hutool.db.sql.SqlExecutor
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import com.jy.data.core.db.AirDSFactory
import com.jy.data.core.row.Row
import com.jy.data.core.step.Step
import com.jy.data.core.step.StepInfo
import java.sql.ResultSet


/**
 * @Author Je.Wang
 * @Date 2021/2/4 11:26
 */
class TableInput(
    info: StepInfo,
) : Step(info) {
    private var state: TableInputState = Gson().fromJson(info.state)
    private var prop: TableInputProp = Gson().fromJson(info.option)

    init {
        val ds = AirDSFactory.get(prop.dbId)
        val connection = ds.connection
        val namedSql = NamedSql(prop.sql)
    }

    override suspend fun processRow(inputRow: Row) {
        val ds = AirDSFactory.get(prop.dbId)
        val connection = ds.connection
        val sql = "select team_name as teamName from team where team_name = :team_name"
        val namedSql = NamedSql(sql, mapOf("team_name" to "项目一组"))
        val prepareStatement = connection.prepareStatement(
            namedSql.sql,
            ResultSet.TYPE_FORWARD_ONLY,
            ResultSet.CONCUR_READ_ONLY
        )
        prepareStatement.fetchSize = Int.MIN_VALUE
        SqlExecutor.query(prepareStatement, {
            println(it.metaData)
        }, *namedSql.params)
    }
}

data class TableInputProp(
    val sql: String,
    val querySize: Long = 1000,
    val queryAll: Boolean = false,
    val dbId: String,
)

data class TableInputState(
    val count: Long,
)