package ru.get.better.event

data class ShowNoteDetailEvent(
    val position: Int,
    val noteId: String
    )