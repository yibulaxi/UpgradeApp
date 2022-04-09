package ru.get.better.model

import android.content.Context

data class Task(
    val taskId: String,
    val datetime: String? = null,
    val isCompleted: Boolean = false,
    val title: String
)

enum class TaskId(val id: String)

fun Context.getTasks() =
    arrayListOf<Task>(

    )