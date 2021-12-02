package com.velkonost.upgrade.event

import com.velkonost.upgrade.model.UserSettings

data class UserSettingsReadyEvent(val userSettings: UserSettings)