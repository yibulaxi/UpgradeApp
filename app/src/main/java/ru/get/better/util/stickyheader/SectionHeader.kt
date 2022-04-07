package ru.get.better.util.stickyheader

import ru.get.better.model.DiaryNote

class SectionHeader(
    private var section: Int,
    private val sectionName: String
) : Section {
    override fun type(): Int {
        return Section.HEADER
    }

    override var diaryNote: DiaryNote? = null

    override fun sectionPosition(): Int {
        return section
    }

    override fun reduceSection() {
        section --
    }

    override fun sectionName(): String = sectionName
}