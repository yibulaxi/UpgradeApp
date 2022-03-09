package ru.get.better.event

data class UpdateThemeEvent(
    val isDarkTheme: Boolean,
    val withAnimation: Boolean = false
    )