package ru.get.better.ui.activity.main.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.get.better.model.Media

class AddPostMediaAdapter(
    private val context: Context,
    private val media: MutableList<Media>
) : RecyclerView.Adapter<AddPostMediaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        AddPostMediaViewHolder.newInstance(parent, context)


    override fun onBindViewHolder(holder: AddPostMediaViewHolder, position: Int) {
        holder.bind(media[position])
    }

    override fun getItemCount(): Int {
        return media.size
    }

    fun getMedia() = media
}