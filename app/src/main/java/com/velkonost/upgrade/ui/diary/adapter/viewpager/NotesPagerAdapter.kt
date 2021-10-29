package com.velkonost.upgrade.ui.diary.adapter.viewpager

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.velkonost.upgrade.model.DiaryNote

class NotesPagerAdapter(
    private val context: Context,
    private val notes: MutableList<DiaryNote>
) : RecyclerView.Adapter<NotesPagerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        NotesPagerViewHolder.newInstance(parent, context)


    override fun onBindViewHolder(holder: NotesPagerViewHolder, position: Int) {
        holder.bind(notes[position])
    }

    override fun getItemCount(): Int {
        return notes.size
    }
}