package com.velkonost.upgrade.util.ext

import android.view.Gravity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.skydoves.balloon.*
import com.velkonost.upgrade.R

fun Fragment.getBalloon(
    text: String,

    ): Balloon =
    Balloon.Builder(context!!)
        .setArrowSize(10)
        .setArrowOrientation(ArrowOrientation.TOP)
        .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
        .setArrowPosition(0.5f)
        .setTextGravity(Gravity.START)
        .setPadding(10)
        .setWidth(BalloonSizeSpec.WRAP)
        .setHeight(BalloonSizeSpec.WRAP)
        .setTextSize(15f)
        .setCornerRadius(4f)
        .setAlpha(0.9f)
        .setText(text)
        .setTextColor(ContextCompat.getColor(context!!, R.color.colorTgWhite))
        .setTextIsHtml(true)
        .setBackgroundColor(ContextCompat.getColor(context!!, R.color.colorTgPrimary))
        .setBalloonAnimation(BalloonAnimation.CIRCULAR)
        .build()