package ru.get.better.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings_table")
class UserSettings(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "userId")
    val userId: String,

    @ColumnInfo(name = "authType")
    val authType: String = "1",

    @ColumnInfo(name = "login")
    val login: String? = null,

    @ColumnInfo(name = "password")
    val password: String? = null,

    @ColumnInfo(name = "difficulty")
    var difficulty: String? = "1",

    @ColumnInfo(name = "greeting")
    val greeting: String? = "GET BETTER",

    @ColumnInfo(name = "dateRegistration")
    val dateRegistration: String? = null,

    @ColumnInfo(name = "dateLastLogin")
    val dateLastLogin: String? = null,

    @ColumnInfo(name = "avatar")
    val avatar: String? = null,

    @ColumnInfo(name = "locale")
    var locale: String? = null,

    @ColumnInfo(name = "isInterestsInitialized")
    val isInterestsInitialized: Boolean? = true
)

fun UserSettings.getDifficultyValue(): Float {
    return when (difficulty?.toInt()) {
        0 -> 0.1f
        1 -> 0.06f
        2 -> 0.03f
        else -> 0.01f
    }
}
