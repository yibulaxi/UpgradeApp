package ru.get.better.rest

import androidx.annotation.StringRes
import retrofit2.HttpException
import ru.get.better.R

class Error(
    val message: String = "",
    @StringRes var messageId: Int = R.string.default_error_message,
    val code: String = ""
)

fun Throwable.toError(): Error =
    when (this) {
        is HttpException -> try {
            Json.DEFAULT.toObject(response()?.errorBody()?.string(), Error::class.java)
        } catch (e: Exception) {
            Error()
        }!!
        else -> Error()
    }