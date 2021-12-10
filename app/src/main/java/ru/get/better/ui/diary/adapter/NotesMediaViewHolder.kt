package ru.get.better.ui.diary.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ru.get.better.R
import ru.get.better.databinding.ItemNotesMediaBinding
import ru.get.better.model.Media

class NotesMediaViewHolder(
    val binding: ItemNotesMediaBinding,
    val context: Context
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.handler = Handler()
    }

    fun bind(media: Media) {
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
            NotesMediaViewHolder(
                ItemNotesMediaBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ), context = context
            )
    }
}