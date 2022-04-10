package ru.get.better.util

sealed class ProgressState {
    object Idle : ProgressState()
    object ShowProgress : ProgressState()
    object HideProgress : ProgressState()

}

