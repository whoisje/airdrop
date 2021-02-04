package com.jy.data.core.db

import cn.hutool.core.map.MapUtil
import cn.hutool.core.text.StrBuilder
import cn.hutool.core.util.ArrayUtil
import cn.hutool.core.util.StrUtil
import cn.hutool.db.sql.NamedSql
import java.util.*

/**
 * @author Je.Wang
 * @date 2021/2/4 17:33
 */
class NamedSql(namedSql: String, paramMap: Map<String?, Any?>) {
    /**
     * 获取SQL
     *
     * @return SQL
     */
    var sql: String? = null
        private set
    private val params: MutableList<Any?>

    /**
     * 获取参数列表，按照占位符顺序
     *
     * @return 参数数组
     */
    fun getParams(): Array<Any?> {
        return params.toTypedArray()
    }

    /**
     * 获取参数列表，按照占位符顺序
     *
     * @return 参数列表
     */
    val paramList: List<Any?>
        get() = params

    /**
     * 解析命名占位符的SQL
     *
     * @param namedSql 命名占位符的SQL
     * @param paramMap 名和参数的对应Map
     */
    private fun parse(namedSql: String, paramMap: Map<String?, Any?>) {
        if (MapUtil.isEmpty(paramMap)) {
            sql = namedSql
            return
        }
        val len = namedSql.length
        val name = StrUtil.strBuilder()
        val sqlBuilder = StrUtil.strBuilder()
        var c: Char
        var nameStartChar: Char? = null
        for (i in 0 until len) {
            c = namedSql[i]
            if (ArrayUtil.contains(NamedSql.Companion.NAME_START_CHARS, c)) {
                // 新的变量开始符出现，要处理之前的变量
                replaceVar(nameStartChar, name, sqlBuilder, paramMap)
                nameStartChar = c
            } else if (null != nameStartChar) {
                // 变量状态
                if (NamedSql.Companion.isGenerateChar(c)) {
                    // 变量名
                    name.append(c)
                } else {
                    // 非标准字符也非变量开始的字符出现表示变量名结束，开始替换
                    replaceVar(nameStartChar, name, sqlBuilder, paramMap)
                    nameStartChar = null
                    sqlBuilder.append(c)
                }
            } else {
                // 变量以外的字符原样输出
                sqlBuilder.append(c)
            }
        }

        // 收尾，如果SQL末尾存在变量，处理之
        if (false == name.isEmpty) {
            replaceVar(nameStartChar, name, sqlBuilder, paramMap)
        }
        sql = sqlBuilder.toString()
    }

    /**
     * 替换变量，如果无变量，原样输出到SQL中去
     *
     * @param nameStartChar 变量开始字符
     * @param name 变量名
     * @param sqlBuilder 结果SQL缓存
     * @param paramMap 变量map（非空）
     */
    private fun replaceVar(
        nameStartChar: Char?,
        name: StrBuilder,
        sqlBuilder: StrBuilder,
        paramMap: Map<String?, Any?>
    ) {
        if (name.isEmpty) {
            if (null != nameStartChar) {
                // 类似于:的情况，需要补上:
                sqlBuilder.append(nameStartChar)
            }
            // 无变量，按照普通字符处理
            return
        }

        // 变量结束
        val nameStr = name.toString()
        if (paramMap.containsKey(nameStr)) {
            // 有变量对应值（值可以为null），替换占位符为?，变量值放入相应index位置
            val paramValue = paramMap[nameStr]
            sqlBuilder.append('?')
            params.add(paramValue)
        } else {
            // 无变量对应值，原样输出
            sqlBuilder.append(nameStartChar).append(name)
        }

        //清空变量，表示此变量处理结束
        name.clear()
    }

    companion object {
        private val NAME_START_CHARS = charArrayOf(':', '@', '?')

        /**
         * 是否为标准的字符，包括大小写字母、下划线和数字
         *
         * @param c 字符
         * @return 是否标准字符
         */
        private fun isGenerateChar(c: Char): Boolean {
            return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '_' || c >= '0' && c <= '9'
        }
    }

    /**
     * 构造
     *
     * @param namedSql 命名占位符的SQL
     * @param paramMap 名和参数的对应Map
     */
    init {
        params = LinkedList()
        parse(namedSql, paramMap)
    }
}