package ru.get.better.model

import android.content.Context

data class Achievement(
    val achievementId: String,
    val datetime: String? = null,
    var isCompleted: Boolean = false,
    val title: String,
    val experience: Int
)

enum class AchievementId(val id: String) {
    CreateEachTypeNote("createEachTypeNote"),
    Get50Efficiency("get50Efficiency"),
    Get70Efficiency("get70Efficiency"),
    Get100Efficiency("get100Efficiency"),
    Create50Notes("create50Notes"),
    Create100Notes("create100Notes"),
    Create200Notes("create200Notes"),
    CreateNotes3Days("createNotes3Days"),
    CreateNotes7Days("createNotes7Days"),
    CreateNotes21Days("createNotes21Days"),
    Get10PointsInterest("get10PointsInterest"),
    Complete1Habit("complete1Habit"),
    Complete3Habit("complete3Habit"),
    Complete9Habit("complete9Habit"),
}

fun getInitialAchievements() =
    listOf<Achievement>(
        Achievement(
            achievementId = AchievementId.CreateEachTypeNote.id,
            title = "Написать записи каждого типа",
            experience = 1
        ),
        Achievement(
            achievementId = AchievementId.Complete1Habit.id,
            title = "Освоить 1 привычку",
            experience = 2
        ),
        Achievement(
            achievementId = AchievementId.Get50Efficiency.id,
            title = "Набрать 50% эффективности",
            experience = 4
        ),
        Achievement(
            achievementId = AchievementId.CreateNotes3Days.id,
            title = "Писать записи 3 дня подряд",
            experience = 2
        ),
        Achievement(
            achievementId = AchievementId.Get70Efficiency.id,
            title = "Набрать 70% эффективности",
            experience = 6
        ),
        Achievement(
            achievementId = AchievementId.Complete3Habit.id,
            title = "Освоить 3 привычки",
            experience = 7
        ),
        Achievement(
            achievementId = AchievementId.Create50Notes.id,
            title = "Создать 50 записей",
            experience = 11
        ),
        Achievement(
            achievementId = AchievementId.Get100Efficiency.id,
            title = "Набрать 100% эффективности",
            experience = 17
        ),
        Achievement(
            achievementId = AchievementId.Create100Notes.id,
            title = "Создать 100 записей",
            experience = 19
        ),
        Achievement(
            achievementId = AchievementId.CreateNotes7Days.id,
            title = "Писать записи 7 дней подряд",
            experience = 14
        ),
        Achievement(
            achievementId = AchievementId.Create200Notes.id,
            title = "Создать 200 записей",
            experience = 27
        ),
        Achievement(
            achievementId = AchievementId.CreateNotes21Days.id,
            title = "Писать записи 21 день подряд",
            experience = 13
        ),
        Achievement(
            achievementId = AchievementId.Get10PointsInterest.id,
            title = "Набрать 10 в любой сфере",
            experience = 9
        ),
        Achievement(
            achievementId = AchievementId.Complete9Habit.id,
            title = "Освоить 9 привычек",
            experience = 15
        ),
    )

