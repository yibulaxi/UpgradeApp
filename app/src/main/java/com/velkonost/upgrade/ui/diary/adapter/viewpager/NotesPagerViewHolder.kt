package com.velkonost.upgrade.ui.diary.adapter.viewpager

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.velkonost.upgrade.R
import com.velkonost.upgrade.databinding.ItemAdapterPagerNotesBinding
import com.velkonost.upgrade.event.EditDiaryNoteEvent
import com.velkonost.upgrade.model.DefaultInterest
import com.velkonost.upgrade.model.DiaryNote
import com.velkonost.upgrade.model.Interest
import com.velkonost.upgrade.model.Media
import com.velkonost.upgrade.ui.diary.adapter.NotesMediaAdapter
import org.greenrobot.eventbus.EventBus

class NotesPagerViewHolder(
    val binding: ItemAdapterPagerNotesBinding,
    val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    private var MAX_ELEVATION_FACTOR = 8

    init {
        binding.handler = Handler()
    }

    fun bind(note: DiaryNote) {
        binding.text.text = Html.fromHtml(note.text)
        binding.date.text = note.date

        binding.icon.setImageDrawable(
            AppCompatResources.getDrawable(
                context,
                DefaultInterest.getInterestById(note.interest.interestId.toInt()).logo
            )
        )

        binding.amount.text = note.changeOfPoints.toString().replace(".", ",")

        when {
            note.changeOfPoints.toFloat() > 0 -> binding.amount.background =
                AppCompatResources.getDrawable(context, R.drawable.snack_success_gradient)
            note.changeOfPoints.toFloat() < 0 -> binding.amount.background =
                AppCompatResources.getDrawable(context, R.drawable.snack_warning_gradient)
            else -> binding.amount.background =
                AppCompatResources.getDrawable(context, R.drawable.bg_list_metric_value)
        }

        binding.cardView.maxCardElevation = (binding.cardView.cardElevation
                * MAX_ELEVATION_FACTOR)

        binding.edit.setOnClickListener { EventBus.getDefault().post(EditDiaryNoteEvent(note)) }

        binding.recycler.isVisible = !note.media.isNullOrEmpty()
        (binding.recycler.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.HORIZONTAL
        val media = arrayListOf<Media>()
        note.media?.map { media.add(Media(url = it)) }

        binding.recycler.adapter = NotesMediaAdapter(context, media)
    }

    inner class Handler

    companion object {
        fun newInstance(
            parent: ViewGroup,
            context: Context,
        ) =
            NotesPagerViewHolder(
                ItemAdapterPagerNotesBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ), context = context
            )
    }
}