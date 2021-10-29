package com.velkonost.upgrade.model

class Diary {
    val notes: ArrayList<DiaryNote> = ArrayList()


}

class DiaryNote(
    val text: String,
    val date: String,
    val amount: String,
    val interestId: String
)