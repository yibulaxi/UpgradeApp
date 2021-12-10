package ru.get.better.event

import ru.get.better.model.DiaryNote

data class HabitRealizationCompletedEvent(val habitRealization: DiaryNote)