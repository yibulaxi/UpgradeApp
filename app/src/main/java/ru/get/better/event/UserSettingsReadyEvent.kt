package ru.get.better.event

import ru.get.better.model.UserSettings

data class UserSettingsReadyEvent(val userSettings: UserSettings)