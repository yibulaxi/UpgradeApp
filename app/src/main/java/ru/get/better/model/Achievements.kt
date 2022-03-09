package ru.get.better.model

import ru.get.better.App
import ru.get.better.R

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
            title = App.resourcesProvider.getStringLocale(
                R.string.achievement_create_each_type_note,
                App.preferences.locale ?: "ru"
            ),
            experience = 1
        ),
        Achievement(
            achievementId = AchievementId.Complete1Habit.id,
            title = App.resourcesProvider.getStringLocale(
                R.string.achievement_complete_1_habit,
                App.preferences.locale ?: "ru"
            ),
            experience = 2
        ),
        Achievement(
            achievementId = AchievementId.Get50Efficiency.id,
            title = App.resourcesProvider.getStringLocale(
                R.string.achievement_get_50_efficiency,
                App.preferences.locale ?: "ru"
            ),
            experience = 4
        ),
        Achievement(
            achievementId = AchievementId.CreateNotes3Days.id,
            title = App.resourcesProvider.getStringLocale(
                R.string.achievement_create_notes_30_days,
                App.preferences.locale ?: "ru"
            ),
            experience = 2
        ),
        Achievement(
            achievementId = AchievementId.Get70Efficiency.id,
            title = App.resourcesProvider.getStringLocale(
                R.string.achievement_get_70_efficiency,
                App.preferences.locale ?: "ru"
            ),
            experience = 6
        ),
        Achievement(
            achievementId = AchievementId.Complete3Habit.id,
            title = App.resourcesProvider.getStringLocale(
                R.string.achievement_complete_3_habit,
                App.preferences.locale ?: "ru"
            ),
            experience = 7
        ),
        Achievement(
            achievementId = AchievementId.Create50Notes.id,
            title = App.resourcesProvider.getStringLocale(
                R.string.achievement_create_50_notes,
                App.preferences.locale ?: "ru"
            ),
            experience = 11
        ),
        Achievement(
            achievementId = AchievementId.Get100Efficiency.id,
            title = App.resourcesProvider.getStringLocale(
                R.string.achievement_get_100_efficiency,
                App.preferences.locale ?: "ru"
            ),
            experience = 17
        ),
        Achievement(
            achievementId = AchievementId.Create100Notes.id,
            title = App.resourcesProvider.getStringLocale(
                R.string.achievement_create_100_notes,
                App.preferences.locale ?: "ru"
            ),
            experience = 19
        ),
        Achievement(
            achievementId = AchievementId.CreateNotes7Days.id,
            title = App.resourcesProvider.getStringLocale(
                R.string.achievement_create_notes_7_days,
                App.preferences.locale ?: "ru"
            ),
            experience = 14
        ),
        Achievement(
            achievementId = AchievementId.Create200Notes.id,
            title = App.resourcesProvider.getStringLocale(
                R.string.achievement_create_200_notes,
                App.preferences.locale ?: "ru"
            ),
            experience = 27
        ),
        Achievement(
            achievementId = AchievementId.CreateNotes21Days.id,
            title = App.resourcesProvider.getStringLocale(
                R.string.achievement_create_notes_21_days,
                App.preferences.locale ?: "ru"
            ),
            experience = 13
        ),
        Achievement(
            achievementId = AchievementId.Get10PointsInterest.id,
            title = App.resourcesProvider.getStringLocale(
                R.string.achievement_get_10_points_interest,
                App.preferences.locale ?: "ru"
            ),
            experience = 9
        ),
        Achievement(
            achievementId = AchievementId.Complete9Habit.id,
            title = App.resourcesProvider.getStringLocale(
                R.string.achievement_complete_9_habits,
                App.preferences.locale ?: "ru"
            ),
            experience = 15
        ),
    )

