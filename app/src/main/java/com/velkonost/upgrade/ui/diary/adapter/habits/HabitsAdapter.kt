package com.velkonost.upgrade.ui.diary.adapter.habits

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.velkonost.upgrade.model.DiaryNote
import com.velkonost.upgrade.model.Interest
import com.velkonost.upgrade.ui.metric.adapter.MetricListViewHolder

class HabitsAdapter (
    private val context: Context,
    private val habitsRealization: MutableList<DiaryNote>
) : RecyclerView.Adapter<HabitsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        HabitsViewHolder.newInstance(parent, context)


    override fun onBindViewHolder(holder: HabitsViewHolder, position: Int) {
        holder.bind(habitRealization = habitsRealization[position])
    }

    override fun getItemCount(): Int {
        return habitsRealization.size
    }

}