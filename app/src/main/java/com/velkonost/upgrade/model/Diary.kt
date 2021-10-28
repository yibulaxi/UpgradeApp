package com.velkonost.upgrade.model

class Diary {
     val notes: ArrayList<DiaryNote> = ArrayList()


}

class DiaryNote(
    private val text: String,
    private val date: String,
    private val amount: String,
    private val interestId: String
) {

}