package com.velkonost.upgrade.util

sealed class ViewState<out T> {
    object Idle : ViewState<Nothing>()
    object ShowProgress : ViewState<Nothing>()
    object HideProgress : ViewState<Nothing>()
    object Success : ViewState<Nothing>()
    class Error<T>(val error: com.velkonost.upgrade.rest.Error) : ViewState<T>()
    class Data<T>(val data: T) : ViewState<T>()

}

fun <T> ViewState<T>.doOn(
    idle: () -> Unit = {},
    showProgress: () -> Unit = {},
    hideProgress: () -> Unit = {},
    error: (com.velkonost.upgrade.rest.Error) -> Unit = {},
    success: () -> Unit = {},
    data: (T) -> Unit = {}
){
    when(this){
        ViewState.Idle -> idle()
        ViewState.ShowProgress -> showProgress()
        ViewState.HideProgress -> hideProgress()
        ViewState.Success -> success()
        is ViewState.Error -> error(this.error)
        is ViewState.Data -> data(this.data)
    }
}

fun <T> ViewState<T>.doOn(
    idle: () -> Unit = {},
    progressDelegate: ProgressDelegate,
    error: (com.velkonost.upgrade.rest.Error) -> Unit = {},
    success: () -> Unit = {},
    data: (T) -> Unit = {}
){
    when(this){
        ViewState.Idle -> idle()
        ViewState.ShowProgress -> progressDelegate.showProgress()
        ViewState.HideProgress -> progressDelegate.hideProgress()
        ViewState.Success -> success()
        is ViewState.Error -> error(this.error)
        is ViewState.Data -> data(this.data)
    }
}