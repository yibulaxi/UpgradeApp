package com.velkonost.upgrade.ui.diary.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.velkonost.upgrade.model.DiaryNote
import com.velkonost.upgrade.model.Interest
import com.velkonost.upgrade.ui.metric.adapter.MetricListViewHolder

class NotesAdapter (
    private val context: Context,
    private val notes: MutableList<DiaryNote>
) : RecyclerView.Adapter<NotesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        NotesViewHolder.newInstance(parent, context)


    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.bind(notes[position])
    }

    override fun getItemCount(): Int {
        return notes.size
    }
}