package com.velkonost.upgrade.model

class UserSettings(
    val authType: Int = 1,
    val login: String? = null,
    val password: String? = null,
    val difficulty: Int? = 1,
    val isPushAvailable: Boolean? = true,
    val greeting: String? = null,
    val dateRegistration: Long? = null,
    val dateLastLogin: Long? = null,
    val avatar: String? = null,
    val locale: String? = null,
    val isInterestsInitialized: Boolean? = true,
) {
    fun getDifficultyValue(): Float {
        return when (difficulty) {
            0 -> 0.3f
            1 -> 0.1f
            2 -> 0.05f
            else -> 0.01f
        }
    }

}