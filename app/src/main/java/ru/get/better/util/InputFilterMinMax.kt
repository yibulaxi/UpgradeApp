package ru.get.better.util

import android.text.InputFilter
import android.text.Spanned
import java.lang.NumberFormatException


class InputFilterMinMax : InputFilter {
    private var min: Float
    private var max: Float

    constructor(min: Float, max: Float) {
        this.min = min
        this.max = max
    }

    constructor(min: String, max: String) {
        this.min = min.toFloat()
        this.max = max.toFloat()
    }

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        try {
            val input: Float = (dest.toString() + source.toString()).toFloat()
            if (isInRange(min, max, input)) return null
        } catch (nfe: NumberFormatException) {
        }
        return ""
    }

    private fun isInRange(a: Float, b: Float, c: Float): Boolean {
        return if (b > a) c in a..b else c in b..a
    }
}