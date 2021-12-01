package com.velkonost.upgrade.util.stickyheader

import com.velkonost.upgrade.model.DiaryNote

class SectionItem(
    private val section: Int,
    private val sectionName: String,
    override var diaryNote: DiaryNote?
    ) : Section {
    override fun type(): Int {
        return Section.ITEM
    }


    override fun sectionPosition(): Int {
        return section
    }

    override fun sectionName(): String = sectionName
}