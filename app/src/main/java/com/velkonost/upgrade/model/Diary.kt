package com.velkonost.upgrade.model

import androidx.room.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable

class Diary {
    val notes: ArrayList<DiaryNote> = ArrayList()
}

@Entity(tableName = "user_diary_table")
class DiaryNote(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "diaryNoteId")
    val diaryNoteId: String = "",

    @ColumnInfo(name = "noteType")
    val noteType: Int = 1,

    @ColumnInfo(name = "date")
    val date: String = "",

    @ColumnInfo(name = "title")
    var title: String? = null,

    @ColumnInfo(name = "text")
    var text: String? = null,

    @ColumnInfo(name = "media")
    @TypeConverters(MediaConverters::class)
    var media: ArrayList<String>? = arrayListOf(),

    @ColumnInfo(name = "changeOfPoints")
    var changeOfPoints: Int = ChangeOfPoints.Neutral.id,

    @ColumnInfo(name = "datetimeStart")
    val datetimeStart: String? = null,

    @ColumnInfo(name = "datetimeEnd")
    var datetimeEnd: String? = null,

    @ColumnInfo(name = "isActiveNow")
    var isActiveNow: Boolean? = false,

    @ColumnInfo(name = "interest")
    @TypeConverters(DiaryNoteInterestConverters::class)
    val interest: DiaryNoteInterest? = null,

    @ColumnInfo(name = "initialAmount")
    val initialAmount: Int? = 66,

    @ColumnInfo(name = "regularity")
    val regularity: Int? = null,

    @ColumnInfo(name = "isPushAvailable")
    val isPushAvailable: Boolean? = false,

    @ColumnInfo(name = "color")
    val color: String? = null,

    @ColumnInfo(name = "currentAmount")
    val currentAmount: Int? = null,

    @ColumnInfo(name = "datesCompletion")
    @TypeConverters(DatesCompletionConverters::class)
    val datesCompletion: ArrayList<DiaryNoteDatesCompletion>? = arrayListOf(),

    @ColumnInfo(name = "tags")
    @TypeConverters(MediaConverters::class)
    val tags: ArrayList<String>? = arrayListOf()
)

data class DiaryNoteInterest(
    var interestId: String,
    var interestName: String,
    var interestIcon: String
)

data class DiaryNoteDatesCompletion (
    val datesCompletionDatetime: String? = null,
    var datesCompletionIsCompleted: Boolean? = null
): Serializable

enum class Regularity(val id: Int) {
    Daily(1),
    Weekly(2)
}

enum class Milliseconds(val mills: Long) {
    Day(86400000),
    Week(604800000)
}

enum class NoteType(val id: Int) {
    Note(1),
    Habit(2),
    Goal(3),
    Tracker(4),
    HabitRealization(5)
}

enum class ChangeOfPoints(val id: Int) {
    Positive(0),
    Neutral(1),
    Negative(2)
}

class MediaConverters {

    @TypeConverter
    fun fromMediaToJson(media: ArrayList<String>): String =
        Gson().toJson(media)

    @TypeConverter
    fun fromJsonToMedia(json: String): ArrayList<String> {
        if (json.isEmpty())
            return arrayListOf()
        return Gson().fromJson(json, object : TypeToken<ArrayList<String>>() {}.type)
    }
}

class DiaryNoteInterestConverters {

    @TypeConverter
    fun fromDiaryNoteInterestToJson(diaryNoteInterest: DiaryNoteInterest): String =
        Gson().toJson(diaryNoteInterest)

    @TypeConverter
    fun fromJsonToDiaryNoteInterest(json: String): DiaryNoteInterest =
        Gson().fromJson(json, object : TypeToken<DiaryNoteInterest>() {}.type)
}

class DatesCompletionConverters {

    @TypeConverter
    fun fromDatesCompletionToJson(datesCompletion: ArrayList<DiaryNoteDatesCompletion>): String =
        Gson().toJson(datesCompletion)

    @TypeConverter
    fun fromJsonToDatesCompletion(json: String): ArrayList<DiaryNoteDatesCompletion> =
        Gson().fromJson(json, object : TypeToken<ArrayList<DiaryNoteDatesCompletion>>() {}.type)
}