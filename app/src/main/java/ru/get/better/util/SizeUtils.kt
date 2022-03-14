package ru.get.better.util

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue

fun Context.convertDpToPx(dip: Float): Float {
    val r: Resources = resources
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dip,
        r.displayMetrics
    )
}