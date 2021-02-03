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
    private var state: GenerateRowState = GenerateRowState()
    private var prop: GenerateRowProp = Gson().fromJson(info.option);

    init {
    }

    private fun generateRow(inputRow: Row): Row {
        val rowMetas = this.prop.rowInfos
        rowMetas.forEach { info ->
            inputRow.put(info.rowMeta, info.value)
        }
        state.count++
        if (state.count == this.prop.count) {
            this.markNoMore()
        }
        return inputRow
    }

    override suspend fun process() {
        val inputRow = getRow() ?: return
        if (info.id=="1235")throw Exception()
        println("input row $inputRow")
        putRow(generateRow(inputRow))
    }

    override fun onStop() {
        //TODO 销毁操作，保存状态state
        println("unload " + info)
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