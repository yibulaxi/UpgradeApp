package ru.get.better.ui.welcome.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import org.greenrobot.eventbus.EventBus
import ru.get.better.App
import ru.get.better.R
import ru.get.better.databinding.FragmentAdapterPagerBinding
import ru.get.better.event.SaveInterestsClickedEvent
import ru.get.better.event.SwipeViewPagerEvent
import ru.get.better.model.*

class WelcomeViewHolder(
    val binding: FragmentAdapterPagerBinding,
    val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.handler = Handler()
    }

    fun bind(interest: Interest) {
        binding.container.setBackgroundColor(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAdapterPagerBackground
                else R.color.colorLightFragmentAdapterPagerBackground
            )
        )

        binding.firstFrame.background = ContextCompat.getDrawable(
            context,
            if (App.preferences.isDarkTheme) R.drawable.gradient_welcome_first_value_dark
            else R.drawable.gradient_welcome_first_value_light
        )

        binding.secondFrame.background = ContextCompat.getDrawable(
            context,
            if (App.preferences.isDarkTheme) R.drawable.gradient_welcome_second_value_dark
            else R.drawable.gradient_welcome_second_value_light
        )

        binding.thirdFrame.background = ContextCompat.getDrawable(
            context,
            if (App.preferences.isDarkTheme) R.drawable.gradient_welcome_third_value_dark
            else R.drawable.gradient_welcome_third_value_light
        )

        binding.forthFrame.background = ContextCompat.getDrawable(
            context,
            if (App.preferences.isDarkTheme) R.drawable.gradient_welcome_forth_value_dark
            else R.drawable.gradient_welcome_forth_value_light
        )

        binding.background.background = ContextCompat.getDrawable(
            context,
            if (App.preferences.isDarkTheme) R.drawable.snack_neutral_gradient_dark
            else R.drawable.snack_neutral_gradient_light
        )

        binding.title.setTextColor(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAdapterPagerTitleText
                else R.color.colorLightFragmentAdapterPagerTitleText
            )
        )

        binding.description.setTextColor(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAdapterPagerDescriptionText
                else R.color.colorLightFragmentAdapterPagerDescriptionText
            )
        )

        binding.firstValueText.setTextColor(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAdapterPagerFirstValueText
                else R.color.colorLightFragmentAdapterPagerFirstValueText
            )
        )

        binding.secondValue.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAdapterPagerSecondValueBackgroundTint
                else R.color.colorLightFragmentAdapterPagerSecondValueBackgroundTint
            )
        )

        binding.secondValueText.setTextColor(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAdapterPagerSecondValueText
                else R.color.colorLightFragmentAdapterPagerSecondValueText
            )
        )

        binding.thirdValue.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAdapterPagerThirdValueBackgroundTint
                else R.color.colorLightFragmentAdapterPagerThirdValueBackgroundTint
            )
        )

        binding.thirdValueText.setTextColor(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAdapterPagerThirdValueText
                else R.color.colorLightFragmentAdapterPagerThirdValueText
            )
        )

        binding.forthValueText.setTextColor(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAdapterPagerForthValueText
                else R.color.colorLightFragmentAdapterPagerForthValueText
            )
        )

        binding.tvMessage.setTextColor(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAdapterPagerTvMessageText
                else R.color.colorLightFragmentAdapterPagerTvMessageText
            )
        )

        binding.firstValue.isVisible = true
        binding.secondValue.isVisible = true
        binding.thirdValue.isVisible = true
        binding.forthValue.isVisible = true
        binding.startBtn.isVisible = false

        binding.startLottieAnimationView.isVisible = false

        when (interest) {
            is Relationship -> {
                binding.lottieAnimationView.setAnimation(R.raw.relationship_anim)

                binding.firstValueText.text = App.resourcesProvider.getStringLocale(
                    R.string.relationship_first_value,
                    App.preferences.locale
                )
                binding.secondValueText.text = App.resourcesProvider.getStringLocale(
                    R.string.relationship_second_value,
                    App.preferences.locale
                )
                binding.thirdValueText.text = App.resourcesProvider.getStringLocale(
                    R.string.relationship_third_value,
                    App.preferences.locale
                )
                binding.forthValueText.text = App.resourcesProvider.getStringLocale(
                    R.string.relationship_forth_value,
                    App.preferences.locale
                )
            }
            is Health -> {
                binding.lottieAnimationView.setAnimation(R.raw.health_anim)

                binding.firstValueText.text = App.resourcesProvider.getStringLocale(
                    R.string.health_first_value,
                    App.preferences.locale
                )
                binding.secondValueText.text = App.resourcesProvider.getStringLocale(
                    R.string.health_second_value,
                    App.preferences.locale
                )
                binding.thirdValueText.text = App.resourcesProvider.getStringLocale(
                    R.string.health_third_value,
                    App.preferences.locale
                )
                binding.forthValueText.text = App.resourcesProvider.getStringLocale(
                    R.string.health_forth_value,
                    App.preferences.locale
                )
            }
            is Environment -> {
                binding.lottieAnimationView.setAnimation(R.raw.environment_anim)

                binding.firstValueText.text = App.resourcesProvider.getStringLocale(
                    R.string.environment_first_value,
                    App.preferences.locale
                )
                binding.secondValueText.text = App.resourcesProvider.getStringLocale(
                    R.string.environment_second_value,
                    App.preferences.locale
                )
                binding.thirdValueText.text = App.resourcesProvider.getStringLocale(
                    R.string.environment_third_value,
                    App.preferences.locale
                )
                binding.forthValueText.text = App.resourcesProvider.getStringLocale(
                    R.string.environment_forth_value,
                    App.preferences.locale
                )
            }
            is Finance -> {
                binding.lottieAnimationView.setAnimation(R.raw.finance_anim)

                binding.firstValueText.text = App.resourcesProvider.getStringLocale(
                    R.string.finance_first_value,
                    App.preferences.locale
                )
                binding.secondValueText.text = App.resourcesProvider.getStringLocale(
                    R.string.finance_second_value,
                    App.preferences.locale
                )
                binding.thirdValueText.text = App.resourcesProvider.getStringLocale(
                    R.string.finance_third_value,
                    App.preferences.locale
                )
                binding.forthValueText.text = App.resourcesProvider.getStringLocale(
                    R.string.finance_forth_value,
                    App.preferences.locale
                )
            }
            is Work -> {
                binding.lottieAnimationView.setAnimation(R.raw.work_anim)

                binding.firstValueText.text = App.resourcesProvider.getStringLocale(
                    R.string.work_first_value,
                    App.preferences.locale
                )
                binding.secondValueText.text = App.resourcesProvider.getStringLocale(
                    R.string.work_second_value,
                    App.preferences.locale
                )
                binding.thirdValueText.text = App.resourcesProvider.getStringLocale(
                    R.string.work_third_value,
                    App.preferences.locale
                )
                binding.forthValueText.text = App.resourcesProvider.getStringLocale(
                    R.string.work_forth_value,
                    App.preferences.locale
                )
            }
            is Chill -> {
                binding.lottieAnimationView.setAnimation(R.raw.chill_anim)

                binding.firstValueText.text = App.resourcesProvider.getStringLocale(
                    R.string.chill_first_value,
                    App.preferences.locale
                )
                binding.secondValueText.text = App.resourcesProvider.getStringLocale(
                    R.string.chill_second_value,
                    App.preferences.locale
                )
                binding.thirdValueText.text = App.resourcesProvider.getStringLocale(
                    R.string.chill_third_value,
                    App.preferences.locale
                )
                binding.forthValueText.text = App.resourcesProvider.getStringLocale(
                    R.string.chill_forth_value,
                    App.preferences.locale
                )
            }
            is Creation -> {
                binding.lottieAnimationView.setAnimation(R.raw.creation_anim)

                binding.firstValueText.text = App.resourcesProvider.getStringLocale(
                    R.string.creation_first_value,
                    App.preferences.locale
                )
                binding.secondValueText.text = App.resourcesProvider.getStringLocale(
                    R.string.creation_second_value,
                    App.preferences.locale
                )
                binding.thirdValueText.text = App.resourcesProvider.getStringLocale(
                    R.string.creation_third_value,
                    App.preferences.locale
                )
                binding.forthValueText.text = App.resourcesProvider.getStringLocale(
                    R.string.creation_forth_value,
                    App.preferences.locale
                )
            }
            is Spirit -> {
                binding.lottieAnimationView.setAnimation(R.raw.spirit_anim)

                binding.firstValueText.text = App.resourcesProvider.getStringLocale(
                    R.string.spirit_first_value,
                    App.preferences.locale
                )
                binding.secondValueText.text = App.resourcesProvider.getStringLocale(
                    R.string.spirit_second_value,
                    App.preferences.locale
                )
                binding.thirdValueText.text = App.resourcesProvider.getStringLocale(
                    R.string.spirit_third_value,
                    App.preferences.locale
                )
                binding.forthValueText.text = App.resourcesProvider.getStringLocale(
                    R.string.spirit_forth_value,
                    App.preferences.locale
                )
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

        binding.tvMessage.text = App.resourcesProvider.getStringLocale(R.string.start)

        binding.title.text = interest.name
        binding.description.text = interest.description

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