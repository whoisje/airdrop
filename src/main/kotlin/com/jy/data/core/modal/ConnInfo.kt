package com.jy.data.core.modal

/**
 * @author Je.Wang
 * @date 2021/2/4 16:55
 */
data class ConnInfo(
    val id: String,
    val name: String,
    val username: String,
    val password: String,
    val setting: Map<String, String>
)
