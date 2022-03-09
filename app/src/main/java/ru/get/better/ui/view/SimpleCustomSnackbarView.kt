package ru.get.better.ui.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat

import com.google.android.material.snackbar.ContentViewCallback
import kotlinx.android.synthetic.main.view_snackbar_simple.view.*
import ru.get.better.App
import ru.get.better.R

class SimpleCustomSnackbarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), ContentViewCallback {

    lateinit var tvMsg: TextView
    lateinit var tvAction: TextView
    lateinit var imLeft: ImageView
    lateinit var layRoot: ConstraintLayout
    lateinit var backgroundRoot: View
    lateinit var icon: AppCompatImageView

    init {
        View.inflate(context, R.layout.view_snackbar_simple, this)
        clipToPadding = false
        this.tvMsg = findViewById(R.id.tv_message)
        this.tvAction = findViewById(R.id.tv_action)
        this.imLeft = findViewById(R.id.im_action_left)
        this.layRoot = findViewById(R.id.snack_constraint)
        this.backgroundRoot = findViewById(R.id.background)
        this.icon = findViewById(R.id.icon)

        this.backgroundRoot.background = ContextCompat.getDrawable(
            context,
            if (App.preferences.isDarkTheme) R.drawable.snack_warning_gradient_dark
            else R.drawable.snack_warning_gradient_light
        )

        this.icon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
            context,
            if (App.preferences.isDarkTheme) R.color.colorDarkViewSnackbarSimpleIconTint
            else R.color.colorLightViewSnackbarSimpleIconTint
        ))

        this.tvMsg.setTextColor(ContextCompat.getColor(
            context,
            if (App.preferences.isDarkTheme) R.color.colorDarkViewSnackbarSimpleTvMessageText
            else R.color.colorLightViewSnackbarSimpleTvMessageText
        ))

        this.tvAction.setTextColor(ContextCompat.getColor(
            context,
            if (App.preferences.isDarkTheme) R.color.colorDarkViewSnackbarSimpleTvActionText
            else R.color.colorLightViewSnackbarSimpleTvActionText
        ))
    }

    override fun animateContentIn(delay: Int, duration: Int) {
        val scaleX = ObjectAnimator.ofFloat(im_action_left, View.SCALE_X, 0f, 1f)
        val scaleY = ObjectAnimator.ofFloat(im_action_left, View.SCALE_Y, 0f, 1f)
        val animatorSet = AnimatorSet().apply {
            interpolator = OvershootInterpolator()
            setDuration(500)
            playTogether(scaleX, scaleY)
        }
        animatorSet.start()
    }

    override fun animateContentOut(delay: Int, duration: Int) {
    }
}