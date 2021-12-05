package com.velkonost.upgrade.ui.diary.adapter.habits

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.velkonost.upgrade.R
import com.velkonost.upgrade.databinding.ItemHorizontalHabitBinding
import android.annotation.SuppressLint
import android.content.Context.VIBRATOR_MANAGER_SERVICE
import android.content.Context.VIBRATOR_SERVICE
import android.content.res.ColorStateList
import android.os.VibrationEffect.EFFECT_TICK
import android.os.VibrationEffect.createOneShot
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.animation.Animation
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import android.view.animation.AlphaAnimation
import androidx.appcompat.content.res.AppCompatResources
import com.velkonost.upgrade.event.HabitRealizationCompletedEvent
import com.velkonost.upgrade.model.AllLogo
import com.velkonost.upgrade.model.DiaryNote
import org.greenrobot.eventbus.EventBus


class HabitsViewHolder(
    val binding: ItemHorizontalHabitBinding,
    val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    val vibrator: Vibrator = context.getSystemService(VIBRATOR_SERVICE) as Vibrator

    init {
        binding.handler = Handler()
    }

    @SuppressLint("ClickableViewAccessibility")
    fun bind(habitRealization: DiaryNote) {
        binding.cardView.animation =
            AnimationUtils.loadAnimation(context, R.anim.scale)

        binding.cardView.setOnTouchListener { v, event ->
            when(event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    binding.cardView.isHapticFeedbackEnabled = true
                    val pattern = longArrayOf(
                        0, // start
                        100, // duration
                        200 // sleep
                    )

                    vibrator.vibrate(pattern, 0)
                    binding.completeBlock.animate()
                        .alpha(0.5f)
                        .setDuration(2000)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator?) {
                                Log.d("keke_alpha", binding.completeBlock.alpha.toString())
                                if (binding.completeBlock.alpha == 0.5f) {
                                    habitRealization.datesCompletion!!.firstOrNull {
                                        it.datesCompletionIsCompleted == false
                                                && it.datesCompletionDatetime!!.toLong() <= System.currentTimeMillis()
                                    }.let {
                                        if (it != null)
                                            it.datesCompletionIsCompleted = true
                                    }
                                    EventBus.getDefault()
                                        .post(HabitRealizationCompletedEvent(habitRealization))
                                }
                                super.onAnimationEnd(animation)
                            }
                        })

                }
                MotionEvent.ACTION_UP -> {
                    if (
                        habitRealization.datesCompletion!!.firstOrNull {
                            it.datesCompletionIsCompleted == false
                                    && it.datesCompletionDatetime!!.toLong() <= System.currentTimeMillis()
                        } != null
                    ) {
                        vibrator.cancel()

                        binding.completeBlock.animate().cancel()
                        binding.completeBlock.alpha = 0f
                    }
                }
                else -> {
                    if (
                        habitRealization.datesCompletion!!.firstOrNull {
                            it.datesCompletionIsCompleted == false
                                    && it.datesCompletionDatetime!!.toLong() <= System.currentTimeMillis()
                        } != null
                    ) {
                        vibrator.cancel()

                        binding.completeBlock.animate().cancel()
                        binding.completeBlock.alpha = 0f
                    }
                }
            }
            false
        }

        binding.icon.setImageDrawable(
            AppCompatResources.getDrawable(
                context,
                AllLogo().getLogoById(habitRealization.interest!!.interestIcon)
            )
        )

        binding.title.text = habitRealization.text


    }

    inner class Handler

    companion object {
        fun newInstance(
            parent: ViewGroup,
            context: Context,
        ) =
            HabitsViewHolder(
                ItemHorizontalHabitBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ), context = context
            )
    }
}