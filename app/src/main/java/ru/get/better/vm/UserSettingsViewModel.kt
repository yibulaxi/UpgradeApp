package ru.get.better.vm

import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import ru.get.better.App
import ru.get.better.event.InitUserSettingsEvent
import ru.get.better.event.UpdateDifficultyEvent
import ru.get.better.model.UserSettings
import javax.inject.Inject

class UserSettingsViewModel @Inject constructor() : BaseViewModel() {

    init {
        EventBus.getDefault().register(this)
    }

    suspend fun getUserSettingsById(id: String) =
        coroutineScope {
            withContext(Dispatchers.IO) {
                App.database.userSettingsDao().getById(id)
            }
        }

    suspend fun resetUserSettings() =
        coroutineScope {
            withContext(Dispatchers.IO) {
                App.database.userSettingsDao().clear()
            }
        }

    private fun updateLocale(locale: String) {
        GlobalScope.launch {
            val currentUserSettings = App.database.userSettingsDao().getById(App.preferences.uid!!)
            currentUserSettings!!.locale = locale

            App.database.userSettingsDao().update(currentUserSettings)
        }
    }

    private fun updateDifficulty(difficulty: Int) {
        GlobalScope.launch {
            val currentUserSettings = App.database.userSettingsDao().getById(App.preferences.uid!!)
            currentUserSettings!!.difficulty = difficulty.toString()

            App.database.userSettingsDao().update(currentUserSettings)
        }
    }

    @Subscribe
    fun onUpdateDifficultyEvent(e: UpdateDifficultyEvent) {
        updateDifficulty(e.difficulty)
    }

    fun initUserSettings(
        userId: String,
        login: String,
        locale: String
    ) {
        GlobalScope.launch {
            val userSettings = UserSettings(
                userId = userId,
                authType = 1.toString(),
                login = login,
                password = "",
                difficulty = 1.toString(),
                greeting = login,
                dateRegistration = System.currentTimeMillis().toString(),
                dateLastLogin = System.currentTimeMillis().toString(),
                avatar = "",
                locale = locale,
                isInterestsInitialized = false,
            )

            App.database.userSettingsDao().insert(userSettings)
        }
    }

    @Subscribe
    fun onInitUserSettingsEvent(e: InitUserSettingsEvent) {
        GlobalScope.launch {
            val userSettings = UserSettings(
                userId = e.userId,
                authType = 1.toString(),
                login = e.login,
                password = "",
                difficulty = 1.toString(),
                greeting = e.login,
                dateRegistration = System.currentTimeMillis().toString(),
                dateLastLogin = System.currentTimeMillis().toString(),
                avatar = "",
                locale = e.locale,
                isInterestsInitialized = false,
            )

            App.database.userSettingsDao().insert(userSettings)
        }
    }
}