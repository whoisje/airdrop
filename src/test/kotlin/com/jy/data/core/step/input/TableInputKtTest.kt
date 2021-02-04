package com.jy.data.core.step.input

import cn.hutool.setting.Setting
import org.junit.jupiter.api.BeforeEach

/**
 * @author Je.Wang
 * @date 2021/2/4 17:18
 */
internal class TableInputKtTest {

    @BeforeEach
    fun setUp() {
        val setting = Setting.create()
        setting.putAll(
            "test", mapOf(
                "url" to " jdbc:mysql://localhost:3306/test?serverTimezone=Asia/Shanghai",
                "username" to "root",
                "password" to "wangwenjie",
                "driver" to "com.mysql.jdbc.Driver",
                "idleTimeout" to "600000",
            )
        )
    }
}