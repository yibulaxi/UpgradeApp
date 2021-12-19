package ru.get.better.model

import androidx.room.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "user_achievements")
data class UserAchievements(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "userId")
    val userId: String,

    @ColumnInfo(name = "userLvl")
    val userLvl: String,

    @ColumnInfo(name = "userExp")
    val userExp: String,

    @ColumnInfo(name = "efficiency")
    val efficiency: String,

    @ColumnInfo(name = "completedTask")
    @TypeConverters(CompletedTasksConverters::class)
    val completedTasks: List<CompletedTask>,

    @ColumnInfo(name = "completedAchievements")
    @TypeConverters(CompletedAchievementsConverters::class)
    val completedAchievements: List<CompletedAchievement>
)

data class CompletedTask(
    val taskId: String,
    val datetime: String
)

data class CompletedAchievement(
    val achievementId: String,
    val datetime: String
)

class CompletedTasksConverters {

    @TypeConverter
    fun fromCompletedTasksToJson(completedTasks: List<CompletedTask>): String =
        Gson().toJson(completedTasks)

    @TypeConverter
    fun fromJsonToCompletedTasks(json: String): List<CompletedTask> =
        Gson().fromJson(json, object : TypeToken<List<CompletedTask>>() {}.type)
}

class CompletedAchievementsConverters {

    @TypeConverter
    fun fromCompletedAchievementsToJson(completedAchievements: List<CompletedAchievement>): String =
        Gson().toJson(completedAchievements)

    @TypeConverter
    fun fromJsonToCompletedAchievements(json: String): List<CompletedAchievement> =
        Gson().fromJson(json, object : TypeToken<List<CompletedAchievement>>() {}.type)
}