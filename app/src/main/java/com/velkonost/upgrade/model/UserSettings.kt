package com.velkonost.upgrade.model

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
    val difficulty: String? = "1",

    @ColumnInfo(name = "isPushAvailable")
    val isPushAvailable: Boolean? = true,

    @ColumnInfo(name = "greeting")
    val greeting: String? = "UPGRADE",

    @ColumnInfo(name = "dateRegistration")
    val dateRegistration: String? = null,

    @ColumnInfo(name = "dateLastLogin")
    val dateLastLogin: String? = null,

    @ColumnInfo(name = "avatar")
    val avatar: String? = null,

    @ColumnInfo(name = "locale")
    val locale: String? = null,

    @ColumnInfo(name = "isInterestsInitialized")
    val isInterestsInitialized: Boolean? = true,

    @ColumnInfo(name = "isMetricWheelSpotlightShown")
    var isMetricWheelSpotlightShown: Boolean = false,

    @ColumnInfo(name = "isDiaryHabitsSpotlightShown")
    var isDiaryHabitsSpotlightShown: Boolean = false,

    @ColumnInfo(name = "isMainAddPostSpotlightShown")
    var isMainAddPostSpotlightShown: Boolean = false
)


fun UserSettings.getDifficultyValue(): Float {
    return when (difficulty?.toInt()) {
        0 -> 0.3f
        1 -> 0.1f
        2 -> 0.05f
        else -> 0.01f
    }
}
