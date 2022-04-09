package ru.get.better.model

import androidx.room.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable

class Diary {
    val notes: ArrayList<DiaryNote> = ArrayList()
}

@Entity(tableName = "user_diary_table")
data class DiaryNote(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "diaryNoteId")
    val diaryNoteId: String = "",

    @ColumnInfo(name = "noteType")
    val noteType: Int = 1,

    @ColumnInfo(name = "date")
    var date: String = "",

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
    var datetimeStart: String? = null,

    @ColumnInfo(name = "datetimeEnd")
    var datetimeEnd: String? = null,

    @ColumnInfo(name = "isActiveNow")
    var isActiveNow: Boolean? = false,

    @ColumnInfo(name = "interest")
    @TypeConverters(DiaryNoteInterestConverters::class)
    var interest: DiaryNoteInterest? = null,

    @ColumnInfo(name = "initialAmount")
    var initialAmount: Int? = 66,

    @ColumnInfo(name = "regularity")
    var regularity: Int? = null,

    @ColumnInfo(name = "isPushAvailable")
    var isPushAvailable: Boolean? = false,

    @ColumnInfo(name = "color")
    var color: String? = null,

    @ColumnInfo(name = "currentAmount")
    var currentAmount: Int? = null,

    @ColumnInfo(name = "datesCompletion")
    @TypeConverters(DatesCompletionConverters::class)
    var datesCompletion: ArrayList<DiaryNoteDatesCompletion>? = arrayListOf(),

    @ColumnInfo(name = "tags")
    @TypeConverters(MediaConverters::class)
    var tags: ArrayList<String>? = arrayListOf()
)

data class DiaryNoteInterest(
    var interestId: String,
    var interestName: String,
    var interestIcon: String
)

data class DiaryNoteDatesCompletion(
    var datesCompletionDatetime: String? = null,
    var datesCompletionIsCompleted: Boolean? = null
) : Serializable

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
    fun fromMediaToJson(media: ArrayList<String>?): String? =
        Gson().toJson(media)

    @TypeConverter
    fun fromJsonToMedia(json: String?): ArrayList<String>? {
        if (json.isNullOrEmpty())
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

fun List<DiaryNote?>.getHabitsRealization(): List<DiaryNote> {
    val habitsRealization = arrayListOf<DiaryNote>()
    forEach { habit ->
        val datesCompletion =
            habit!!.datesCompletion?.filter { it.datesCompletionIsCompleted == true }

        datesCompletion?.forEach { dateCompletion ->
            val date = habit.date
            habitsRealization.add(
                habit.copy(
                    date = dateCompletion.datesCompletionDatetime!!,
                    datetimeStart = date,
                    noteType = NoteType.HabitRealization.id
                )
            )
        }
    }

    return habitsRealization
}

fun DiaryNote.recalculateDatesCompletion() {
    datesCompletion!!.filter { it.datesCompletionIsCompleted == false }
        .forEachIndexed { index, dateCompletion ->
            dateCompletion.datesCompletionDatetime =
                (
                        System.currentTimeMillis()
                                + index * when (regularity) {
                            Regularity.Daily.id -> Milliseconds.Day.mills
                            Regularity.Weekly.id -> Milliseconds.Week.mills
                            else -> Milliseconds.Day.mills
                        }
                        )
                    .toString()
        }
}
