package com.jy.data.core.db

import cn.hutool.db.ds.DSFactory
import cn.hutool.db.ds.DataSourceWrapper
import cn.hutool.setting.Setting
import javax.sql.DataSource

/**
 * @author Je.Wang
 * @date 2021/2/4 16:41
 */
object AirDSFactory {
    private val hikariDSFactory = HikariDSFactory(Setting.create())

    init {
        DSFactory.setCurrentDSFactory(
            hikariDSFactory
        )
    }

    fun get(id: String): DataSource {
        return DSFactory.get(id)
    }

    fun add(id: String, setting: Map<String, String>) {
        hikariDSFactory.setting.putAll(id, setting)
    }

    fun delete(id: String) {
        (get(id) as DataSourceWrapper).close()
        hikariDSFactory.setting.remove(id)
    }
}