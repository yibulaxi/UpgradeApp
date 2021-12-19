package ru.get.better.vm

import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import ru.get.better.App
import ru.get.better.event.InitUserSettingsEvent
import ru.get.better.event.UpdateDifficultyEvent
import ru.get.better.model.UserSettings
import ru.get.better.repo.UserSettingsRepository
import ru.get.better.rest.UserSettingsFields
import ru.get.better.rest.UserSettingsTable
import ru.get.better.util.SingleLiveEvent
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
                isMetricWheelSpotlightShown = documentSnapshot.getBoolean(UserSettingsTable().tableFields[UserSettingsFields.IsMetricWheelSpotlightShown]!!)!!,
                isDiaryHabitsSpotlightShown = documentSnapshot.getBoolean(UserSettingsTable().tableFields[UserSettingsFields.IsDiaryHabitsSpotlightShown]!!)!!,
                isMainAddPostSpotlightShown = documentSnapshot.getBoolean(UserSettingsTable().tableFields[UserSettingsFields.IsMainAddPostSpotlightShown]!!)!!
            )

        App.preferences.isMetricWheelSpotlightShown =
            documentSnapshot.getBoolean(UserSettingsTable().tableFields[UserSettingsFields.IsMetricWheelSpotlightShown]!!)!!
        App.preferences.isDiaryHabitsSpotlightShown =
            documentSnapshot.getBoolean(UserSettingsTable().tableFields[UserSettingsFields.IsDiaryHabitsSpotlightShown]!!)!!
        App.preferences.isMainAddPostSpotlightShown =
            documentSnapshot.getBoolean(UserSettingsTable().tableFields[UserSettingsFields.IsMainAddPostSpotlightShown]!!)!!

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
                getUserSettings()
            }
            .addOnFailureListener { }
    }

    fun updateField(
        field: UserSettingsFields,
        value: Any
    ) {
        val data = hashMapOf(
            UserSettingsTable().tableFields[field] to value
        )

        cloudFirestoreDatabase.collection(UserSettingsTable().tableName)
            .document(App.preferences.uid!!)
            .update(data as Map<String, Any>)
            .addOnSuccessListener {
                getUserSettings()
            }
            .addOnFailureListener {

            }
    }

    @Subscribe
    fun onUpdateDifficultyEvent(e: UpdateDifficultyEvent) {
        updateDifficulty(e.difficulty)
    }

    @Subscribe
    fun onInitUserSettingsEvent(e: InitUserSettingsEvent) {

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
            "is_interests_initialized" to false,
            UserSettingsTable().tableFields[UserSettingsFields.IsMetricWheelSpotlightShown] to false,
            UserSettingsTable().tableFields[UserSettingsFields.IsDiaryHabitsSpotlightShown] to false,
            UserSettingsTable().tableFields[UserSettingsFields.IsMainAddPostSpotlightShown] to false
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