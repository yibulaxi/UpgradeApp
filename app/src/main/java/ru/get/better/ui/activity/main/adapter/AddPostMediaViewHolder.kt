package ru.get.better.ui.activity.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ru.get.better.R
import ru.get.better.databinding.ItemMediaAddPostBinding
import ru.get.better.model.Media

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