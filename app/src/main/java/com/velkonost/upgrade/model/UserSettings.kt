package com.velkonost.upgrade.model

class UserSettings(
    val difficulty: Int? = 1,
    val is_interests_initialized: Boolean? = true,
    val is_push_available: Boolean? = true
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