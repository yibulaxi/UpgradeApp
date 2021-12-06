package com.velkonost.upgrade.util

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

fun Context.formatMillsToFullDateTime(mills: Long) =
    SimpleDateFormat("dd MMM yyyy HH:mm", Locale("ru")).format(mills)!!

fun Context.formatMillsToDateTimeWithoutYear(mills: Long) =
    SimpleDateFormat("dd MMM HH:mm", Locale("ru")).format(mills)!!