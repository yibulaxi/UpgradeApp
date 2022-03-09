package ru.get.better.ui.diary.adapter.habits

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.content.res.ColorStateList
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.greenrobot.eventbus.EventBus
import ru.get.better.App
import ru.get.better.R
import ru.get.better.databinding.ItemHorizontalHabitBinding
import ru.get.better.event.HabitRealizationCompletedEvent
import ru.get.better.model.AllLogo
import ru.get.better.model.DiaryNote
import ru.get.better.model.recalculateDatesCompletion


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
        binding.cardView.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkItemHorizontalHabitBackgroundTint
                else R.color.colorLightItemHorizontalHabitBackgroundTint
            )
        )

        binding.iconTitle.setTextColor(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkItemHorizontalHabitText
                else R.color.colorLightItemHorizontalHabitText
            )
        )

        binding.title.setTextColor(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkItemHorizontalHabitTitleText
                else R.color.colorLightItemHorizontalHabitTitleText
            )
        )

        binding.completeBlock.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkItemHorizontalHabitCompleteBlockBackgroundTint
                else R.color.colorLightItemHorizontalHabitCompleteBlockBackgroundTint
            )
        )

        binding.icCheck.imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkItemHorizontalHabitTint
                else R.color.colorLightItemHorizontalHabitTint
            )
        )

        binding.cardView.animation =
            AnimationUtils.loadAnimation(context, R.anim.scale)

        binding.cardView.setOnTouchListener { v, event ->
            when (event.actionMasked) {
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

                                if (binding.completeBlock.alpha == 0.5f) {
                                    habitRealization.datesCompletion!!.firstOrNull {
                                        it.datesCompletionIsCompleted == false
                                                && it.datesCompletionDatetime!!.toLong() <= System.currentTimeMillis()
                                    }.let {
                                        if (it != null) {
                                            habitRealization.recalculateDatesCompletion()
                                            it.datesCompletionIsCompleted = true

                                            vibrator.cancel()
                                            EventBus.getDefault()
                                                .post(
                                                    HabitRealizationCompletedEvent(
                                                        habitRealization
                                                    )
                                                )
                                        }
                                    }

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