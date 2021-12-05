package com.velkonost.upgrade.event

import com.velkonost.upgrade.model.DiaryNote

data class HabitRealizationCompletedEvent(val habitRealization: DiaryNote)