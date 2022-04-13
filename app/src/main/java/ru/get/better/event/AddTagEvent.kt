package ru.get.better.event

import ru.get.better.model.NoteType

data class AddTagEvent(val tag: String)
data class RemoveTagEvent(val tag: String)