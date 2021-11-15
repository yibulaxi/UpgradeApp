package com.velkonost.upgrade.vm

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.velkonost.upgrade.App
import com.velkonost.upgrade.event.InitUserSettingsEvent
import com.velkonost.upgrade.event.LoadMainEvent
import com.velkonost.upgrade.event.UpdateDifficultyEvent
import com.velkonost.upgrade.model.UserSettings
import com.velkonost.upgrade.navigation.Navigator
import com.velkonost.upgrade.rest.UserSettingsFields
import com.velkonost.upgrade.rest.UserSettingsTable
import com.velkonost.upgrade.util.RxViewModel
import com.velkonost.upgrade.util.SingleLiveEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject

class UserSettingsViewModel @Inject constructor(
) : BaseViewModel() {

    init {
        EventBus.getDefault().register(this)
    }

    var userSettings: UserSettings = UserSettings()

    private fun setUserSettings(
        documentSnapshot: DocumentSnapshot
    ) {
        userSettings = UserSettings(
            difficulty = documentSnapshot.get(UserSettingsTable().tableFields[UserSettingsFields.Difficulty]!!)
                .toString().toInt(),
            isInterestsInitialized = documentSnapshot.getBoolean("is_interests_initialized"),
            isPushAvailable = documentSnapshot.getBoolean(UserSettingsTable().tableFields[UserSettingsFields.IsPushAvailable]!!),
            dateRegistration = documentSnapshot.getLong(UserSettingsTable().tableFields[UserSettingsFields.DateRegistration]!!)
        )
    }

    internal fun getUserSettings() {
        cloudFirestoreDatabase.collection(UserSettingsTable().tableName)
            .document(App.preferences.uid!!)
            .get()
            .addOnSuccessListener {
                setUserSettings(it)
            }
            .addOnFailureListener { }
    }

    private fun updateDifficulty(difficulty: Int) {
        val data = hashMapOf(
            UserSettingsTable().tableFields[UserSettingsFields.Difficulty] to difficulty
        )

        cloudFirestoreDatabase.collection(UserSettingsTable().tableName)
            .document(App.preferences.uid!!)
            .update(data as Map<String, Any>)
            .addOnSuccessListener {

            }
            .addOnFailureListener { }
    }

    @Subscribe
    fun onUpdateDifficultyEvent(e: UpdateDifficultyEvent) {
        updateDifficulty(e.difficulty)
    }

    @Subscribe
    fun onInitUserSettingsEvent(e: InitUserSettingsEvent) {

        val userSettings = hashMapOf(
            UserSettingsTable().tableFields[UserSettingsFields.IsPushAvailable] to true,
            UserSettingsTable().tableFields[UserSettingsFields.Difficulty] to 1,
            "is_interests_initialized" to false,
            UserSettingsTable().tableFields[UserSettingsFields.DateRegistration] to System.currentTimeMillis()
        )

        cloudFirestoreDatabase
            .collection(UserSettingsTable().tableName).document(e.userId)
            .set(userSettings)
            .addOnSuccessListener { }
            .addOnFailureListener { }
    }
}