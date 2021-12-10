package com.velkonost.upgrade.ui.welcome.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.mcdev.quantitizerlibrary.AnimationStyle
import com.mcdev.quantitizerlibrary.QuantitizerListener
import com.velkonost.upgrade.R
import com.velkonost.upgrade.databinding.FragmentAdapterPagerBinding
import com.velkonost.upgrade.event.SaveInterestsChangeVisibilityEvent
import com.velkonost.upgrade.event.SaveInterestsClickedEvent
import com.velkonost.upgrade.event.SwipeViewPagerEvent
import com.velkonost.upgrade.model.*
import org.greenrobot.eventbus.EventBus

class WelcomeViewHolder(
    val binding: FragmentAdapterPagerBinding,
    val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.handler = Handler()
    }

    fun bind(interest: Interest) {

        binding.firstValue.isVisible = true
        binding.secondValue.isVisible = true
        binding.thirdValue.isVisible = true
        binding.forthValue.isVisible = true
        binding.startBtn.isVisible = false

        binding.startLottieAnimationView.isVisible = false

        when (interest) {
            is Relationship -> {
                binding.lottieAnimationView.setAnimation(R.raw.relationship_anim)

                binding.firstValueText.text = context.getString(R.string.relationship_first_value)
                binding.secondValueText.text = context.getString(R.string.relationship_second_value)
                binding.thirdValueText.text = context.getString(R.string.relationship_third_value)
                binding.forthValueText.text = context.getString(R.string.relationship_forth_value)
            }
            is Health -> {
                binding.lottieAnimationView.setAnimation(R.raw.health_anim)

                binding.firstValueText.text = context.getString(R.string.health_first_value)
                binding.secondValueText.text = context.getString(R.string.health_second_value)
                binding.thirdValueText.text = context.getString(R.string.health_third_value)
                binding.forthValueText.text = context.getString(R.string.health_forth_value)
            }
            is Environment -> {
                binding.lottieAnimationView.setAnimation(R.raw.environment_anim)

                binding.firstValueText.text = context.getString(R.string.environment_first_value)
                binding.secondValueText.text = context.getString(R.string.environment_second_value)
                binding.thirdValueText.text = context.getString(R.string.environment_third_value)
                binding.forthValueText.text = context.getString(R.string.environment_forth_value)
            }
            is Finance -> {
                binding.lottieAnimationView.setAnimation(R.raw.finance_anim)

                binding.firstValueText.text = context.getString(R.string.finance_first_value)
                binding.secondValueText.text = context.getString(R.string.finance_second_value)
                binding.thirdValueText.text = context.getString(R.string.finance_third_value)
                binding.forthValueText.text = context.getString(R.string.finance_forth_value)
            }
            is Work -> {
                binding.lottieAnimationView.setAnimation(R.raw.work_anim)

                binding.firstValueText.text = context.getString(R.string.work_first_value)
                binding.secondValueText.text = context.getString(R.string.work_second_value)
                binding.thirdValueText.text = context.getString(R.string.work_third_value)
                binding.forthValueText.text = context.getString(R.string.work_forth_value)
            }
            is Chill -> {
                binding.lottieAnimationView.setAnimation(R.raw.chill_anim)

                binding.firstValueText.text = context.getString(R.string.chill_first_value)
                binding.secondValueText.text = context.getString(R.string.chill_second_value)
                binding.thirdValueText.text = context.getString(R.string.chill_third_value)
                binding.forthValueText.text = context.getString(R.string.chill_forth_value)
            }
            is Creation -> {
                binding.lottieAnimationView.setAnimation(R.raw.creation_anim)

                binding.firstValueText.text = context.getString(R.string.creation_first_value)
                binding.secondValueText.text = context.getString(R.string.creation_second_value)
                binding.thirdValueText.text = context.getString(R.string.creation_third_value)
                binding.forthValueText.text = context.getString(R.string.creation_forth_value)
            }
            is Spirit -> {
                binding.lottieAnimationView.setAnimation(R.raw.spirit_anim)

                binding.firstValueText.text = context.getString(R.string.spirit_first_value)
                binding.secondValueText.text = context.getString(R.string.spirit_second_value)
                binding.thirdValueText.text = context.getString(R.string.spirit_third_value)
                binding.forthValueText.text = context.getString(R.string.spirit_forth_value)
            }
            else -> {
                binding.firstValue.isVisible = false
                binding.secondValue.isVisible = false
                binding.thirdValue.isVisible = false
                binding.forthValue.isVisible = false
                binding.startBtn.isVisible = true

                binding.startLottieAnimationView.isVisible = true
                binding.startLottieAnimationView.setAnimation(R.raw.anim_welcome)
            }
        }

        binding.title.text = interest.name ?: context.getString(interest.nameRes!!)
        binding.description.text =
            interest.description ?: context.getString(interest.descriptionRes!!)

        binding.startBtn.setOnClickListener {
            EventBus.getDefault().post(SwipeViewPagerEvent(0))
        }

        binding.firstValue.setOnClickListener {
            interest.currentValue = 8f
            EventBus.getDefault().post(SwipeViewPagerEvent(0))

            if (interest is Spirit)
                EventBus.getDefault().post(SaveInterestsClickedEvent())
        }

        binding.secondValue.setOnClickListener {
            interest.currentValue = 6f
            EventBus.getDefault().post(SwipeViewPagerEvent(0))

            if (interest is Spirit)
                EventBus.getDefault().post(SaveInterestsClickedEvent())
        }

        binding.thirdValue.setOnClickListener {
            interest.currentValue = 4f
            EventBus.getDefault().post(SwipeViewPagerEvent(0))

            if (interest is Spirit)
                EventBus.getDefault().post(SaveInterestsClickedEvent())
        }

        binding.forthValue.setOnClickListener {
            interest.currentValue = 2f
            EventBus.getDefault().post(SwipeViewPagerEvent(0))

            if (interest is Spirit)
                EventBus.getDefault().post(SaveInterestsClickedEvent())
        }
    }

    inner class Handler

    companion object {
        fun newInstance(
            parent: ViewGroup,
            context: Context,
        ) =
            WelcomeViewHolder(
                FragmentAdapterPagerBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ), context = context
            )
    }
}