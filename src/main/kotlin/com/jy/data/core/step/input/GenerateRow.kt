package com.jy.data.core.step.input

import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import com.jy.data.core.row.Row
import com.jy.data.core.row.RowMeta
import com.jy.data.core.step.Step
import com.jy.data.core.step.StepInfo

/**
 * @Author Je.Wang
 * @Date 2021/2/1 20:18
 */
class GenerateRow(
    info: StepInfo,
) : Step(info) {
    private var state: GenerateRowState = Gson().fromJson(info.state)
    private var prop: GenerateRowProp = Gson().fromJson(info.option)

    private fun generateRow(inputRow: Row): Row {
        val rowMetas = this.prop.rowInfos
        rowMetas.forEach { info ->
            inputRow.put(info.rowMeta, info.value)
        }
        return inputRow
    }


    override suspend fun processRow(inputRow: Row) {
        for (i in 0 until prop.count) {
            this.state.count++
            putRow(generateRow(inputRow))
        }
    }
}

data class GenerateRowState(
    var count: Int = 0,
)

data class GenerateRowProp(
    var count: Int = 0,
    val rowInfos: List<GenerateRowInfo>,
)

data class GenerateRowInfo(
    val rowMeta: RowMeta,
    val value: Any,
)