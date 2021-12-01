package com.velkonost.upgrade.util.stickyheader

import com.velkonost.upgrade.model.DiaryNote

interface Section {
    fun type(): Int
    fun sectionPosition(): Int
    fun sectionName(): String

    var diaryNote: DiaryNote?

    companion object {
        const val HEADER = 0
        const val ITEM = 1
        const val CUSTOM_HEADER = 2
    }
}