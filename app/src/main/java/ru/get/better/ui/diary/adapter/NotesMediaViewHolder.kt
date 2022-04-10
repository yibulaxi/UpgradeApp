package ru.get.better.ui.diary.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.get.better.R
import ru.get.better.databinding.ItemNotesMediaBinding
import ru.get.better.model.Media
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions


class NotesMediaViewHolder(
    val binding: ItemNotesMediaBinding,
    val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.handler = Handler()
    }

    fun bind(media: Media) {
        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(6))
        Glide.with(itemView.context)
            .load(media.url)
            .apply(requestOptions)
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