package com.velkonost.upgrade.util.stickyheader

import com.velkonost.upgrade.model.DiaryNote

class SectionHeader(
    private val section: Int,
    private val sectionName: String
    ) : Section {
    override fun type(): Int {
        return Section.HEADER
    }

    override var diaryNote: DiaryNote? = null

    override fun sectionPosition(): Int {
        return section
    }

    override fun sectionName(): String = sectionName
}