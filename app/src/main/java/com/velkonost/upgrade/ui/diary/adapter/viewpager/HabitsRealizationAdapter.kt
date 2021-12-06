package com.velkonost.upgrade.ui.diary.adapter.viewpager

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.velkonost.upgrade.model.DiaryNote
import com.velkonost.upgrade.model.DiaryNoteDatesCompletion
import com.velkonost.upgrade.ui.diary.adapter.habits.HabitsViewHolder

class HabitsRealizationAdapter (
    private val context: Context,
    private val datesCompletion: MutableList<DiaryNoteDatesCompletion>
) : RecyclerView.Adapter<HabitsRealizationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        HabitsRealizationViewHolder.newInstance(parent, context)


    override fun onBindViewHolder(holder: HabitsRealizationViewHolder, position: Int) {
        holder.bind(dateCompletion = datesCompletion[position])
    }

    override fun getItemCount(): Int {
        return datesCompletion.size
    }

}