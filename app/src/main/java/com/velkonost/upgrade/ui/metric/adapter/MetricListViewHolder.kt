package com.velkonost.upgrade.ui.metric.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.velkonost.upgrade.R
import com.velkonost.upgrade.databinding.ItemListMetricBinding
import com.velkonost.upgrade.event.ShowAddInterestDialogEvent
import com.velkonost.upgrade.event.ShowDetailInterest
import com.velkonost.upgrade.model.EmptyInterest
import com.velkonost.upgrade.model.Interest
import org.greenrobot.eventbus.EventBus

class MetricListViewHolder(
    val binding: ItemListMetricBinding,
    val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.handler = Handler()
    }

    fun bind(interest: Interest) {
        if (interest is EmptyInterest) {
            binding.icon.setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.ic_plus_box
                )
            )
            ImageViewCompat.setImageTintList(
                binding.icon,
                ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.colorTgWhite)
                )
            )

            binding.title.text = "Создай новую сферу интересов"
            binding.description.text = "Подстраивай колесо жизни под себя"

            binding.value.isVisible = false

            binding.container.setOnClickListener {
                EventBus.getDefault().post(ShowAddInterestDialogEvent(true))
            }

        } else {
            binding.icon.setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    interest.getLogo()
                )
            )
            ImageViewCompat.setImageTintList(binding.icon, null)

            binding.title.text = interest.name ?: context.getString(interest.nameRes!!)

            binding.description.text = interest.description


            binding.value.text = String.format("%.2f", interest.currentValue)
                .replace(".", ",")

            binding.value.background = when (interest.currentValue) {
                0f -> AppCompatResources.getDrawable(context, R.drawable.snack_warning_gradient)
                10f -> AppCompatResources.getDrawable(context, R.drawable.bg_list_metric_value)
                else -> AppCompatResources.getDrawable(context, R.drawable.snack_success_gradient)
            }

            binding.container.setOnClickListener {
                EventBus.getDefault().post(ShowDetailInterest(interest))
            }
        }
    }

    inner class Handler

    companion object {
        fun newInstance(
            parent: ViewGroup,
            context: Context,
        ) =
            MetricListViewHolder(
                ItemListMetricBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ), context = context
            )
    }
}