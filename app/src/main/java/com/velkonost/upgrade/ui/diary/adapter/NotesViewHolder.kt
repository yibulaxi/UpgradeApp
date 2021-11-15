package com.velkonost.upgrade.ui.diary.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.velkonost.upgrade.databinding.ItemNoteBinding
import com.velkonost.upgrade.event.ShowNoteDetailEvent
import com.velkonost.upgrade.model.DefaultInterest
import com.velkonost.upgrade.model.DiaryNote
import com.velkonost.upgrade.model.Interest
import org.greenrobot.eventbus.EventBus

class NotesViewHolder(
    val binding: ItemNoteBinding,
    val context: Context
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.handler = Handler()
    }

    fun bind(note: DiaryNote, position: Int) {

        binding.title.text = note.text
        binding.description.text = note.date

        binding.value.setImageDrawable(
            AppCompatResources.getDrawable(
                context,
                DefaultInterest.getInterestById(note.interest.interestId.toInt()).logo
            )
        )

        binding.container.setOnClickListener {
            EventBus.getDefault().post(ShowNoteDetailEvent(position))
        }
    }

    inner class Handler

    companion object {
        fun newInstance(
            parent: ViewGroup,
            context: Context,
        ) =
            NotesViewHolder(
                ItemNoteBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ), context = context
            )
    }
}