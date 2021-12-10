package ru.get.better.ui.diary.adapter.viewpager

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import ru.get.better.R
import ru.get.better.databinding.ItemHabitRealizationBinding
import ru.get.better.model.DiaryNoteDatesCompletion
import ru.get.better.util.formatMillsToDateTimeWithoutYear

class HabitsRealizationViewHolder(
    val binding: ItemHabitRealizationBinding,
    val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.handler = Handler()
    }

    fun bind(dateCompletion: DiaryNoteDatesCompletion) {
        binding.text.text =
            context.formatMillsToDateTimeWithoutYear(dateCompletion.datesCompletionDatetime!!.toLong())

        ViewCompat.setBackgroundTintList(
            binding.cardView,
            ColorStateList.valueOf(
                if (dateCompletion.datesCompletionIsCompleted!!)
                    ContextCompat.getColor(context, R.color.colorTgPrimaryDark)
                else ContextCompat.getColor(context, R.color.colorTgGray)
            )
        )
    }

    inner class Handler

    companion object {
        fun newInstance(
            parent: ViewGroup,
            context: Context,
        ) =
            HabitsRealizationViewHolder(
                ItemHabitRealizationBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ), context = context
            )
    }
}