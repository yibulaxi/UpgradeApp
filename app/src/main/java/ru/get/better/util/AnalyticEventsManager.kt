package ru.get.better.util

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

class AnalyticEventsManager @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) {

    fun tab1Tapped() = firebaseAnalytics.logEvent("tab1Tapped", null)
    fun tab2Tapped() = firebaseAnalytics.logEvent("tab2Tapped", null)
    fun tab4Tapped() = firebaseAnalytics.logEvent("tab4Tapped", null)
    fun tab5Tapped() = firebaseAnalytics.logEvent("tab5Tapped", null)

    fun tab1SpotlightShown() = firebaseAnalytics.logEvent("tab1SpotlightShown", null)
    fun tab3SpotlightShown() = firebaseAnalytics.logEvent("tab3SpotlightShown", null)
    fun tab2SpotlightShown() = firebaseAnalytics.logEvent("tab2SpotlightShown", null)

    fun tab1StartWheelShown() = firebaseAnalytics.logEvent("tab1StartWheelShown", null)
    fun tab1AffirmationShown() = firebaseAnalytics.logEvent("tab1AffirmationShown", null)
    fun tab1AffirmationTapped() = firebaseAnalytics.logEvent("tab1AffirmationTapped", null)
    fun tab1ListShown() = firebaseAnalytics.logEvent("tab1ListShown", null)
    fun tab1InterestDetailsShown() = firebaseAnalytics.logEvent("tab1InterestDetailsShown", null)
    fun tab1InterestDeleted() = firebaseAnalytics.logEvent("tab1InterestDeleted", null)
    fun tab1InterestEdited() = firebaseAnalytics.logEvent("tab1InterestEdited", null)
    fun tab1InterestAdded() = firebaseAnalytics.logEvent("tab1InterestAdded", null)
    fun tab1AdviceTapped() = firebaseAnalytics.logEvent("tab1AdviceTapped", null)
    fun tab1AffirmationShareTapped() = firebaseAnalytics.logEvent("tab1AffirmationShareTapped", null)

    fun rateAppLaterTapped() = firebaseAnalytics.logEvent("rateAppLaterTapped", null)
    fun rateAppTapped() = firebaseAnalytics.logEvent("rateAppTapped", null)

    fun tab2AllNotesTapped() = firebaseAnalytics.logEvent("tab2AllNotesTapped", null)
    fun tab2DiaryNotesTapped() = firebaseAnalytics.logEvent("tab2DiaryNotesTapped", null)
    fun tab2GoalNotesTapped() = firebaseAnalytics.logEvent("tab2GoalNotesTapped", null)
    fun tab2HabitNotesTapped() = firebaseAnalytics.logEvent("tab2HabitNotesTapped", null)
    fun tab2TrackerNotesTapped() = firebaseAnalytics.logEvent("tab2TrackerNotesTapped", null)
    fun tab2AdviceTapped() = firebaseAnalytics.logEvent("tab2AdviceTapped", null)
    fun tab2CalendarTapped() = firebaseAnalytics.logEvent("tab2CalendarTapped", null)
    fun tab2CalendarCancelTapped() = firebaseAnalytics.logEvent("tab2CalendarCancelTapped", null)
    fun tab2CalendarApplyTapped() = firebaseAnalytics.logEvent("tab2CalendarCancelTapped", null)
    fun tab2TagsTapped() = firebaseAnalytics.logEvent("tab2TagsTapped", null)
    fun tab2TagsCancelTapped() = firebaseAnalytics.logEvent("tab2TagsCancelTapped", null)
    fun tab2TagsApplyTapped() = firebaseAnalytics.logEvent("tab2TagsApplyTapped", null)
    fun tab2FilterApplied() = firebaseAnalytics.logEvent("tab2FilterApplied", null)
    fun tab2NotesShown() = firebaseAnalytics.logEvent("tab2NotesShown", null)
    fun tab2NoteDeleted() = firebaseAnalytics.logEvent("tab2NoteDeleted", null)
    fun tab2NoteDetailsShown() = firebaseAnalytics.logEvent("tab2NoteDetailsShown", null)
    fun tab2NoteDetailsEditTapped() = firebaseAnalytics.logEvent("tab2NoteDetailsEditTapped", null)

    fun tab4ShortArticlesTapped() = firebaseAnalytics.logEvent("tab4ShortArticlesTapped", null)
    fun tab4MidArticlesTapped() = firebaseAnalytics.logEvent("tab4MidArticlesTapped", null)
    fun tab4LongArticlesTapped() = firebaseAnalytics.logEvent("tab4LongArticlesTapped", null)
    fun tab4ShortArticleShown() = firebaseAnalytics.logEvent("tab4ShortArticleShown", null)
    fun tab4MidArticleShown() = firebaseAnalytics.logEvent("tab4MidArticleShown", null)
    fun tab4LongArticleShown() = firebaseAnalytics.logEvent("tab4LongArticleShown", null)
    fun tab4ShortArticleCompletelyRead() = firebaseAnalytics.logEvent("tab4ShortArticleCompletelyRead", null)
    fun tab4MidArticleCompletelyRead() = firebaseAnalytics.logEvent("tab4MidArticleCompletelyRead", null)
    fun tab4LongArticleCompletelyRead() = firebaseAnalytics.logEvent("tab4LongArticleCompletelyRead", null)

    fun tab5AchievementsTapped() = firebaseAnalytics.logEvent("tab5AchievementsTapped", null)
    fun tab5LightThemeTapped() = firebaseAnalytics.logEvent("tab5LightThemeTapped", null)
    fun tab5DarkThemeTapped() = firebaseAnalytics.logEvent("tab5DarkThemeTapped", null)
    fun tab5NotificationsOffTapped() = firebaseAnalytics.logEvent("tab5NotificationsOffTapped", null)
    fun tab5NotificationsOnTapped() = firebaseAnalytics.logEvent("tab5NotificationsOnTapped", null)

//    Max Events
    fun tab5LanguageRuTapped() = firebaseAnalytics.logEvent("tab5LanguageRuTapped", null)
    fun tab5LanguageEnTapped() = firebaseAnalytics.logEvent("tab5LanguageEnTapped", null)
    fun tab5DifficultyLowTapped() = firebaseAnalytics.logEvent("tab5DifficultyLowTapped", null)
    fun tab5DifficultyNormalTapped() = firebaseAnalytics.logEvent("tab5DifficultyNormalTapped", null)
    fun tab5DifficultyHighTapped() = firebaseAnalytics.logEvent("tab5DifficultyHighTapped", null)
    fun tab5DifficultyHardcoreTapped() = firebaseAnalytics.logEvent("tab5DifficultyHardcoreTapped", null)
    fun tab5AboutAppTapped() = firebaseAnalytics.logEvent("tab5AboutAppTapped", null)
    fun tab5FaqTapped() = firebaseAnalytics.logEvent("tab5FaqTapped", null)
    fun tab5RateAppTapped() = firebaseAnalytics.logEvent("tab5RateAppTapped", null)
    fun tab5WriteToDeveloperTapped() = firebaseAnalytics.logEvent("tab5WriteToDeveloperTapped", null)

    fun achievementsAchievementsTapped() = firebaseAnalytics.logEvent("achievementsAchievementsTapped", null)
    fun achievementsTasksTapped() = firebaseAnalytics.logEvent("achievementsTasksTapped", null)

    fun addNoteTabTapped() = firebaseAnalytics.logEvent("addNoteTabTapped", null)
    fun addNoteTapped() = firebaseAnalytics.logEvent("addNoteTapped", null)
    fun addTrackerTapped() = firebaseAnalytics.logEvent("addTrackerTapped", null)
    fun addGoalTapped() = firebaseAnalytics.logEvent("addGoalTapped", null)
    fun addHabitTapped() = firebaseAnalytics.logEvent("addHabitTapped", null)

    fun noteNegativePointTapped() = firebaseAnalytics.logEvent("noteNegativePointTapped", null)
    fun noteNeutralPointTapped() = firebaseAnalytics.logEvent("noteNeutralPointTapped", null)
    fun notePositivePointTapped() = firebaseAnalytics.logEvent("notePositivePointTapped", null)
    fun noteCancelled() = firebaseAnalytics.logEvent("noteCancelled", null)
    fun noteAddMediaTapped() = firebaseAnalytics.logEvent("noteAddMediaTapped", null)
    fun noteMediasAdded() = firebaseAnalytics.logEvent("noteMediasAdded", null)
    fun noteTagAdded() = firebaseAnalytics.logEvent("noteTagAdded", null)
    fun noteTagDeleted() = firebaseAnalytics.logEvent("noteTagDeleted", null)
    fun habitDailyTapped() = firebaseAnalytics.logEvent("habitDailyTapped", null)
    fun habitWeeklyTapped() = firebaseAnalytics.logEvent("habitWeeklyTapped", null)

    fun trackerStarted() = firebaseAnalytics.logEvent("trackerStarted", null)
    fun trackerStopped() = firebaseAnalytics.logEvent("trackerStopped", null)
    fun noteSaved() = firebaseAnalytics.logEvent("noteSaved", null)
    fun goalSaved() = firebaseAnalytics.logEvent("goalSaved", null)
    fun habitSaved() = firebaseAnalytics.logEvent("habitSaved", null)

    fun habitTaskCompleted() = firebaseAnalytics.logEvent("habitTaskCompleted", null)

    fun permissionStorageAllowed() = firebaseAnalytics.logEvent("permissionStorageAllowed", null)
    fun permissionStorageDeclined() = firebaseAnalytics.logEvent("permissionStorageDeclined", null)

    fun appOpened() = firebaseAnalytics.logEvent("appOpened", null)
    fun registrationCompleted() = firebaseAnalytics.logEvent("registrationCompleted", null)
    fun registrationSkipped() = firebaseAnalytics.logEvent("registrationSkipped", null)


}