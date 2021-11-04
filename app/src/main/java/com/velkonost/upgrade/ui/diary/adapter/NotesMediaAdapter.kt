package com.velkonost.upgrade.ui.diary.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.velkonost.upgrade.event.OpenFullScreenMediaEvent
import com.velkonost.upgrade.model.Media
import org.greenrobot.eventbus.EventBus

class NotesMediaAdapter(
    private val context: Context,
    private val media: MutableList<Media>
) : RecyclerView.Adapter<NotesMediaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        NotesMediaViewHolder.newInstance(parent, context)


    override fun onBindViewHolder(holder: NotesMediaViewHolder, position: Int) {
        holder.bind(media[position])
        holder.binding.icon.setOnClickListener {
            EventBus.getDefault()
                .post(OpenFullScreenMediaEvent(media, position, holder.binding.icon))
        }
    }

    override fun getItemCount(): Int {
        return media.size
    }

    fun getMedia() = media
}