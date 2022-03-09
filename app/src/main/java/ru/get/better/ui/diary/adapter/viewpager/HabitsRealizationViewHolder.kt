package ru.get.better.ui.diary.adapter.viewpager

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import ru.get.better.App
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
                    ContextCompat.getColor(
                        context,
                        if (App.Companion.preferences.isDarkTheme) R.color.colorDarkHabitDateCompleted
                        else R.color.colorLightHabitDateCompleted
                    )
                else ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.colorDarkHabitDateIncompleted
                    else R.color.colorLightHabitDateIncompleted
                )
            )
        )

        binding.text.setTextColor(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkItemHabitRealizationTextText
                else R.color.colorLightItemHabitRealizationTextText
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