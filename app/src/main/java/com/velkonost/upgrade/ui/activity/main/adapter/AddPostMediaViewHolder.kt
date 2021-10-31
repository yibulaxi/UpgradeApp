package com.velkonost.upgrade.ui.activity.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.velkonost.upgrade.R
import com.velkonost.upgrade.databinding.ItemListMetricBinding
import com.velkonost.upgrade.databinding.ItemMediaAddPostBinding
import com.velkonost.upgrade.event.ShowDetailInterest
import com.velkonost.upgrade.model.Creation
import com.velkonost.upgrade.model.Interest
import com.velkonost.upgrade.model.Media
import com.velkonost.upgrade.ui.metric.adapter.MetricListViewHolder
import org.greenrobot.eventbus.EventBus

class AddPostMediaViewHolder(
    val binding: ItemMediaAddPostBinding,
    val context: Context
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.handler = Handler()
    }

    fun bind(media: Media) {
        if (media.uri != null) binding.icon.setImageURI(media.uri)
        else if (media.url != null)
            Picasso.with(context)
                .load(media.url)
                .placeholder(R.drawable.ic_add_media)
                .into(binding.icon)

    }

    inner class Handler

    companion object {
        fun newInstance(
            parent: ViewGroup,
            context: Context,
        ) =
            AddPostMediaViewHolder(
                ItemMediaAddPostBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ), context = context
            )
    }
}