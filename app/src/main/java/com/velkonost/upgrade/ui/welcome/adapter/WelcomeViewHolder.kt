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
        binding.spinner.isVisible = true
        binding.valueDescription.isVisible = true

        when (interest) {
            is Relationship -> {
                binding.lottieAnimationView.setAnimation(R.raw.relationship_anim)
            }
            is Health -> {
                binding.lottieAnimationView.setAnimation(R.raw.health_anim)
            }
            is Environment -> {
                binding.lottieAnimationView.setAnimation(R.raw.environment_anim)
            }
            is Finance -> {
                binding.lottieAnimationView.setAnimation(R.raw.finance_anim)
            }
            is Work -> {
                binding.lottieAnimationView.setAnimation(R.raw.work_anim)
            }
            is Chill -> {
                binding.lottieAnimationView.setAnimation(R.raw.chill_anim)
            }
            is Creation -> {
                binding.lottieAnimationView.setAnimation(R.raw.creation_anim)
            }
            is Spirit -> {
                binding.lottieAnimationView.setAnimation(R.raw.spirit_anim)
                EventBus.getDefault().post(SaveInterestsChangeVisibilityEvent(isVisible = true))
            }
            else -> {
                binding.lottieAnimationView.setAnimation(R.raw.anim_welcome)

                binding.spinner.isVisible = false
                binding.valueDescription.isVisible = false
            }
        }

        binding.title.text = interest.name?: context.getString(interest.nameRes!!)
//        binding.description.text = context.getString(interest.descriptionRes)

        binding.spinner.maxValue = 10
        binding.spinner.minValue = 0
        binding.spinner.value = interest.currentValue!!.toInt()

        binding.valueDescription.text =
            context.resources.getStringArray(R.array.interest_values)[interest.currentValue!!.toInt()]

        binding.spinner.textAnimationStyle = AnimationStyle.SLIDE_IN
        binding.spinner.animationDuration = 0
        binding.spinner.setQuantitizerListener(object : QuantitizerListener {
            override fun onDecrease() {
                if (interest.currentValue?.toInt() == 0) return

                interest.currentValue = interest.currentValue?.minus(1)
                binding.valueDescription.text =
                    context.resources.getStringArray(R.array.interest_values)[interest.currentValue!!.toInt()]
            }

            override fun onIncrease() {
                if (interest.currentValue?.toInt() == 10) return

                interest.currentValue = interest.currentValue?.plus(1)
                binding.valueDescription.text =
                    context.resources.getStringArray(R.array.interest_values)[interest.currentValue!!.toInt()]
            }

        })
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