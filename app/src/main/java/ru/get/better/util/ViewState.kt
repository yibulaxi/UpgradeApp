package ru.get.better.util

import ru.get.better.rest.Error

sealed class ViewState<out T> {
    object Idle : ViewState<Nothing>()
    object ShowProgress : ViewState<Nothing>()
    object HideProgress : ViewState<Nothing>()
    object Success : ViewState<Nothing>()
    class Error<T>(val error: ru.get.better.rest.Error) : ViewState<T>()
    class Data<T>(val data: T) : ViewState<T>()

}

fun <T> ViewState<T>.doOn(
    idle: () -> Unit = {},
    showProgress: () -> Unit = {},
    hideProgress: () -> Unit = {},
    error: (Error) -> Unit = {},
    success: () -> Unit = {},
    data: (T) -> Unit = {}
) {
    when (this) {
        ViewState.Idle -> idle()
        ViewState.ShowProgress -> showProgress()
        ViewState.HideProgress -> hideProgress()
        ViewState.Success -> success()
        is ViewState.Error -> error(this.error)
        is ViewState.Data -> data(this.data)
    }
}