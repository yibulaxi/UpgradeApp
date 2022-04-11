package ru.get.better.util.stickyheader

import ru.get.better.model.DiaryNote

class SectionItem(
    private var section: Int,
    private val sectionName: String,
    override var diaryNote: DiaryNote?
) : Section {
    override fun type(): Int {
        return Section.ITEM
    }


    override fun sectionPosition(): Int {
        return section
    }

    override fun reduceSection() {
        section--
    }

    override fun sectionName(): String = sectionName
}