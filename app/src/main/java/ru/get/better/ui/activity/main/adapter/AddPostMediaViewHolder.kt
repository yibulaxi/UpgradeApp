package ru.get.better.ui.activity.main.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ru.get.better.App
import ru.get.better.R
import ru.get.better.databinding.ItemMediaAddPostBinding
import ru.get.better.glide.GlideRequests
import ru.get.better.model.Media

class AddPostMediaViewHolder(
    val binding: ItemMediaAddPostBinding,
    val context: Context,
    private val glideRequests: GlideRequests
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.handler = Handler()
    }

    fun bind(media: Media) {
        if (media.uri != null) binding.icon.setImageURI(media.uri)
        else if (media.url != null) {
            glideRequests.load(media.url)
                .placeholder(R.drawable.ic_add_media)
                .into(binding.icon)
//            Picasso.with(context)
//                .load(media.url)
//                .placeholder(R.drawable.ic_add_media)
//                .into(binding.icon)
        }

        binding.icon.strokeColor = ColorStateList.valueOf(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkItemMediaAddPostIconStroke
                else R.color.colorLightItemMediaAddPostIconStroke
            )
        )

    }

    inner class Handler

    companion object {
        fun newInstance(
            parent: ViewGroup,
            context: Context,
            glideRequests: GlideRequests
        ) =
            AddPostMediaViewHolder(
                ItemMediaAddPostBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ), context = context, glideRequests
            )
    }
}