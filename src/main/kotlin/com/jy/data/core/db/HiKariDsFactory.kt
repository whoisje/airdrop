package com.jy.data.core.db

import cn.hutool.core.util.StrUtil
import cn.hutool.db.ds.AbstractDSFactory
import cn.hutool.setting.Setting
import cn.hutool.setting.dialect.Props
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource

/**
 * @author Je.Wang
 *
 */
class HikariDSFactory @JvmOverloads constructor(setting: Setting? = null) :
    AbstractDSFactory(DS_NAME, HikariDataSource::class.java, setting) {
    override fun createDataSource(
        jdbcUrl: String,
        driver: String,
        user: String,
        pass: String,
        poolSetting: Setting
    ): DataSource {
        // remarks等特殊配置，since 5.3.8
        val connProps = Props()
        var connValue: String?
        for (key in KEY_CONN_PROPS) {
            connValue = poolSetting.getAndRemoveStr(key)
            if (StrUtil.isNotBlank(connValue)) {
                connProps.setProperty(key, connValue)
            }
        }
        val config = Props()
        config.putAll(poolSetting)
        config["jdbcUrl"] = jdbcUrl
        config["driverClassName"] = driver
        config["username"] = user
        config["password"] = pass
        val hikariConfig = HikariConfig()
        config.forEach { key, value ->
            hikariConfig[key as String] = value
        }
        hikariConfig.dataSourceProperties = connProps
        return HikariDataSource(hikariConfig)
    }

    companion object {
        private const val serialVersionUID = -8834744983614749401L
        const val DS_NAME = "HikariCP"
    }
}

private operator fun HikariConfig.set(name: String, value: Any) {
    this::class.java.declaredFields.filter { it.name == name }
        .forEach {
            it.isAccessible = true
            it.trySetAccessible()
            when (it.type) {
                String::class.java -> it.set(this, value.toString())
                Long::class.java -> it.set(this, value.toString().toLong())
                Int::class.java -> it.set(this, value.toString().toInt())
                Boolean::class.java -> it.set(this, value.toString().toBoolean())
                Float::class.java -> it.set(this, value.toString().toFloat())
            }


        }
}
