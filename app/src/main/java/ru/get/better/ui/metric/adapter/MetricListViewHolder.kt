package ru.get.better.ui.metric.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import org.greenrobot.eventbus.EventBus
import ru.get.better.App
import ru.get.better.R
import ru.get.better.databinding.ItemListMetricBinding
import ru.get.better.event.ShowAddInterestDialogEvent
import ru.get.better.event.ShowDetailInterest
import ru.get.better.model.EmptyInterest
import ru.get.better.model.Interest

class MetricListViewHolder(
    val binding: ItemListMetricBinding,
    val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.handler = Handler()
    }

    fun bind(interest: Interest) {
        binding.value.background = ContextCompat.getDrawable(
            context,
            if (App.preferences.isDarkTheme) R.drawable.bg_list_metric_value_dark
            else R.drawable.bg_list_metric_value_light
        )

        binding.title.setTextColor(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkItemListMetricTitleText
                else R.color.colorLightItemListMetricTitleText
            )
        )

        binding.description.setTextColor(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkItemListMetricDescriptionText
                else R.color.colorLightItemListMetricDescriptionText
            )
        )

        binding.value.setTextColor(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkItemListMetricValueText
                else R.color.colorLightItemListMetricValueText
            )
        )

        binding.separator.setBackgroundColor(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkItemListMetricSeparatorBackground
                else R.color.colorLightItemListMetricSeparatorBackground
            )
        )

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
                    ContextCompat.getColor(
                        context,
                        if (App.preferences.isDarkTheme) R.color.colorDarkMetricListCreateInterestTint
                        else R.color.colorLightMetricListCreateInterestTint
                    )
                )
            )

            binding.title.text = App.resourcesProvider.getStringLocale(
                R.string.create_new_interest_title,
                App.preferences.locale
            )
            binding.description.text = App.resourcesProvider.getStringLocale(
                R.string.create_new_interest_description,
                App.preferences.locale
            )

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

            binding.title.text = interest.name

            binding.description.text = interest.description

            binding.value.text = String.format("%.2f", interest.currentValue)
                .replace(".", ",")

            binding.value.background = when (interest.currentValue) {
                0f -> AppCompatResources.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.snack_warning_gradient_dark
                    else R.drawable.snack_warning_gradient_light
                )
                10f -> AppCompatResources.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.bg_list_metric_value_dark
                    else R.drawable.bg_list_metric_value_light
                )
                else -> AppCompatResources.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.snack_success_gradient_dark
                    else R.drawable.snack_success_gradient_light
                )
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