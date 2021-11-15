package com.velkonost.upgrade.ui.metric.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.velkonost.upgrade.R
import com.velkonost.upgrade.databinding.ItemListMetricBinding
import com.velkonost.upgrade.event.ShowDetailInterest
import com.velkonost.upgrade.model.Creation
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
        binding.icon.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.logo))
        binding.title.text = interest.name?: context.getString(interest.nameRes!!)
//        binding.description.text = context.getString(interest.shortDescriptionRes)
        binding.value.text = interest.currentValue.toString().replace(".", ",")

        binding.value.background = when (interest.currentValue) {
            0f -> AppCompatResources.getDrawable(context, R.drawable.snack_warning_gradient)
            10f -> AppCompatResources.getDrawable(context, R.drawable.bg_list_metric_value)
            else -> AppCompatResources.getDrawable(context, R.drawable.snack_success_gradient)
        }

        binding.separator.isVisible = interest !is Creation

        binding.container.setOnClickListener {
            EventBus.getDefault().post(ShowDetailInterest(interest))
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