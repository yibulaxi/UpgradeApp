package com.velkonost.upgrade.vm

import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import com.velkonost.upgrade.App
import com.velkonost.upgrade.event.InitUserSettingsEvent
import com.velkonost.upgrade.event.UpdateDifficultyEvent
import com.velkonost.upgrade.model.UserSettings
import com.velkonost.upgrade.repo.UserSettingsRepository
import com.velkonost.upgrade.rest.UserSettingsFields
import com.velkonost.upgrade.rest.UserSettingsTable
import com.velkonost.upgrade.util.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject

class UserSettingsViewModel @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository
) : BaseViewModel() {

    init {
        EventBus.getDefault().register(this)
    }

//    @SuppressLint("SimpleDateFormat")
//    fun convertLongToDateString(systemTime: Long): String {
//        return SimpleDateFormat("EEEE MMM-dd-yyyy' Time: 'HH:mm")
//            .format(systemTime).toString()
//    }


    val setUserSettingsEvent = SingleLiveEvent<UserSettings>()

    private fun setUserSettings(
        documentSnapshot: DocumentSnapshot
    ) {

        val firestoreUserSettings =
            UserSettings(
                userId = documentSnapshot.getString(UserSettingsTable().tableFields[UserSettingsFields.Id]!!)!!,
                authType = documentSnapshot.getString(UserSettingsTable().tableFields[UserSettingsFields.AuthType]!!)
                    ?: "",
                login = documentSnapshot.getString(UserSettingsTable().tableFields[UserSettingsFields.Login]!!)
                    ?: "",
                password = documentSnapshot.getString(UserSettingsTable().tableFields[UserSettingsFields.Password]!!)
                    ?: "",
                difficulty = documentSnapshot.get(UserSettingsTable().tableFields[UserSettingsFields.Difficulty]!!)
                    .toString(),
                isPushAvailable = documentSnapshot.getBoolean(UserSettingsTable().tableFields[UserSettingsFields.IsPushAvailable]!!)
                    ?: false,
                greeting = documentSnapshot.getString(UserSettingsTable().tableFields[UserSettingsFields.Greeting]!!)
                    ?: "",
                dateRegistration = documentSnapshot.getString(UserSettingsTable().tableFields[UserSettingsFields.DateRegistration]!!)
                    ?: "",
                dateLastLogin = documentSnapshot.getString(UserSettingsTable().tableFields[UserSettingsFields.DateLastLogin]!!)
                    ?: "",
                avatar = documentSnapshot.getString(UserSettingsTable().tableFields[UserSettingsFields.Avatar]!!)!!,
                locale = documentSnapshot.getString(UserSettingsTable().tableFields[UserSettingsFields.Locale]!!)!!,
                isInterestsInitialized = documentSnapshot.getBoolean("is_interests_initialized")!!,
            )

        updateUserSettings(firestoreUserSettings)
    }

    private fun updateUserSettings(
        userSettings: UserSettings
    ) = viewModelScope.launch {
        userSettingsRepository.insertOrUpdate(userSettings)
        setUserSettingsEvent.postValue(userSettings)
    }

    fun getUserSettingsById(id: String) =
        userSettingsRepository.getById(id)

    fun resetUserSettings() =
        viewModelScope.launch {
            userSettingsRepository.clear()
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
//
        val userSettings = hashMapOf(
            UserSettingsTable().tableFields[UserSettingsFields.Id] to e.userId,
            UserSettingsTable().tableFields[UserSettingsFields.AuthType] to 1.toString(),
            UserSettingsTable().tableFields[UserSettingsFields.Login] to e.login,
            UserSettingsTable().tableFields[UserSettingsFields.Password] to "",
            UserSettingsTable().tableFields[UserSettingsFields.Difficulty] to 1.toString(),
            UserSettingsTable().tableFields[UserSettingsFields.IsPushAvailable] to true,
            UserSettingsTable().tableFields[UserSettingsFields.Greeting] to "Привет, " + e.login,
            UserSettingsTable().tableFields[UserSettingsFields.DateRegistration] to System.currentTimeMillis()
                .toString(),
            UserSettingsTable().tableFields[UserSettingsFields.DateLastLogin] to System.currentTimeMillis()
                .toString(),
            UserSettingsTable().tableFields[UserSettingsFields.Avatar] to "",
            UserSettingsTable().tableFields[UserSettingsFields.Locale] to "",
            "is_interests_initialized" to false
        )

        cloudFirestoreDatabase
            .collection(UserSettingsTable().tableName).document(e.userId)
            .set(userSettings)
            .addOnSuccessListener {
                getUserSettings()
            }
            .addOnFailureListener { }
    }
}