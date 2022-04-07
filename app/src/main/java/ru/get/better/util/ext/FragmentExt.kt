package ru.get.better.util.ext

import android.view.Gravity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.skydoves.balloon.*

import ru.get.better.App
import ru.get.better.R

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
        .setTextColor(
            ContextCompat.getColor(
                context!!,
                if (App.Companion.preferences.isDarkTheme) R.color.colorDarkBalloonText
                else R.color.colorLightBalloonText
            )
        )
        .setTextIsHtml(true)
        .setBackgroundColor(
            ContextCompat.getColor(
                context!!,
                if (App.preferences.isDarkTheme) R.color.colorDarkBalloonBackground
                else R.color.colorLightBalloonBackground
            )
        )
        .setBalloonAnimation(BalloonAnimation.CIRCULAR)
        .build()