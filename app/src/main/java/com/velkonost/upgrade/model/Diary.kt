package com.velkonost.upgrade.model

class Diary {
    val notes: ArrayList<DiaryNote> = ArrayList()
}

class DiaryNote(
    val id: String,
    val noteType: Int,
    val date: String,
    val title: String? = null,
    val text: String? = null,
    val media: ArrayList<String>? = null,
    val changeOfPoints: Int,
    val datetimeStart: String? = null,
    val datetimeEnd: String? = null,
    val isActiveNow: Boolean? = false,
    val interest: DiaryNoteInterest,
    val initialAmount: Int? = 66,
    val regularity: Int? = null,
    val isPushAvailable: Boolean? = false,
    val color: String? = null,
    val currentAmount: Int? = null,
    val datesCompletion: ArrayList<DiaryNoteDatesCompletion>? = null,
    val tags: ArrayList<String>? = null
)

data class DiaryNoteInterest(
    val interestId: String,
    val interestName: String,
    val interestIcon: String
)

data class DiaryNoteDatesCompletion(
    val dates_completion_datetime: String,
    val dates_completion_is_completed: Boolean
)