package com.jy.data.core.task

import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.jy.data.core.constant.enums.StepType
import com.jy.data.core.row.RowMeta
import com.jy.data.core.step.StepInfo
import com.jy.data.core.step.input.GenerateRow
import com.jy.data.core.step.input.GenerateRowInfo
import com.jy.data.core.step.input.GenerateRowProp
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

@Test
fun main() {
    val rowProp = GenerateRowProp(
        count = 2,
        rowInfos = listOf(
            GenerateRowInfo(
                rowMeta = RowMeta(
                    name = "test",
                ),
                value = "test"
            )
        )
    )
    val gRowProp = Gson().toJson(rowProp)

    val gRowInfo = StepInfo(
        "生成记录",
        "1234",
        Gson().fromJson(gRowProp),
        targets = listOf("1235", "1236"),
    )
    val gRowInfo1 = StepInfo(
        "生成记录",
        "1235",
        Gson().fromJson(gRowProp),
        targets = listOf("1237"),
    )
    val gRowInfo2 = StepInfo(
        "生成记录",
        "1236",
        Gson().fromJson(gRowProp),
        stepType = StepType.ERROR.type,

        )
    val gRowInfo3 = StepInfo(
        "生成记录",
        "1237",
        Gson().fromJson(gRowProp),
    )
    val generateRow = GenerateRow(gRowInfo)
    val generateRow1 = GenerateRow(gRowInfo1)
    val generateRow2 = GenerateRow(gRowInfo2)
    val generateRow3 = GenerateRow(gRowInfo3)
    val coroutineTask = CoroutineTask(
        TaskInfo("test", "1", JsonObject()),
    )
    coroutineTask.steps = mutableMapOf(
        generateRow.info.id to generateRow,
        generateRow1.info.id to generateRow1,
        generateRow2.info.id to generateRow2,
        generateRow3.info.id to generateRow3,
    )
    coroutineTask.buildStepClainAndChannel()
    runBlocking {
         coroutineTask.run()
        coroutineTask.cancel()
        println("run over")
    }

    println("over")

}