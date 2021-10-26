package com.velkonost.upgrade.util

sealed class ProgressState{
    object Idle : ProgressState()
    object ShowProgress : ProgressState()
    object HideProgress : ProgressState()

}

fun ProgressState.doOn(
    showProgress: () -> Unit,
    hideProgress: () -> Unit
) {
    when(this) {
        ProgressState.ShowProgress -> showProgress()
        ProgressState.HideProgress -> hideProgress()
        else -> hideProgress()
    }
}

fun ProgressState.manageBy(progressDelegate: ProgressDelegate) {
    when(this) {
        ProgressState.ShowProgress -> progressDelegate.showProgress()
        ProgressState.HideProgress -> progressDelegate.hideProgress()
        else -> progressDelegate.hideProgress()
    }
}