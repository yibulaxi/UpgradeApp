package com.velkonost.upgrade.ui.metric.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.velkonost.upgrade.R
import com.velkonost.upgrade.databinding.FragmentAdapterPagerBinding
import com.velkonost.upgrade.databinding.ItemListMetricBinding
import com.velkonost.upgrade.event.SaveInterestsChangeVisibilityEvent
import com.velkonost.upgrade.model.*
import com.velkonost.upgrade.ui.welcome.adapter.WelcomeViewHolder
import org.greenrobot.eventbus.EventBus

class MetricListViewHolder  (
    val binding: ItemListMetricBinding,
    val context: Context
) : RecyclerView.ViewHolder(binding.root){
    init {
        binding.handler = Handler()
    }
    fun bind(interest: Interest) {
        binding.icon.setImageDrawable(AppCompatResources.getDrawable(context, interest.logo))
        binding.title.text = context.getString(interest.nameRes)
        binding.description.text = context.getString(interest.shortDescriptionRes)
        binding.value.text = interest.selectedValue.toString().replace(".", ",")
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