package ru.get.better.event

data class InitUserSettingsEvent(val userId: String, val login: String, val locale: String)