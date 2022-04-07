package ru.get.better.util.stickyheader

import ru.get.better.model.DiaryNote

interface Section {
    fun type(): Int
    fun sectionPosition(): Int
    fun sectionName(): String
    fun reduceSection()

    var diaryNote: DiaryNote?

    companion object {
        const val HEADER = 0
        const val ITEM = 1
        const val CUSTOM_HEADER = 2
    }
}