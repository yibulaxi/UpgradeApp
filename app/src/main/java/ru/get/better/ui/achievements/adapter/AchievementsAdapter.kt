package ru.get.better.ui.achievements.adapter

import android.view.LayoutInflater
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.lriccardo.timelineview.TimelineAdapter
import com.lriccardo.timelineview.TimelineView
import ru.get.better.R
import ru.get.better.model.Achievement
import ru.get.better.model.AchievementId

class AchievementsAdapter(var items: List<Achievement>):
    TimelineAdapter, RecyclerView.Adapter<AchievementsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_timeline, parent, false)
        return AchievementsViewHolder(view)
    }

    override fun onBindViewHolder(holder: AchievementsViewHolder, position: Int) {
        holder.title.text = items[position].title

//        when(items[position].achievementId) {
//            holder.checkBlock.isVisible =
//        }
        holder.checkBlock.visibility =
            if (items[position].isCompleted) VISIBLE
            else INVISIBLE

    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    override fun getItemCount(): Int = items.size

    override fun getTimelineViewType(position: Int): TimelineView.ViewType? {
        return when (position) {
            0 -> TimelineView.ViewType.FIRST
            items.size - 1 -> TimelineView.ViewType.LAST
            else -> super.getTimelineViewType(position)
        }
    }

    override fun getIndicatorStyle(position: Int): TimelineView.IndicatorStyle {
        return when (items[position].isCompleted) {
            true -> TimelineView.IndicatorStyle.Checked
            false -> TimelineView.IndicatorStyle.Empty
        }
    }

    override fun getLineStyle(position: Int): TimelineView.LineStyle {
        return when(items[position].isCompleted) {
            true -> TimelineView.LineStyle.Normal
            false -> TimelineView.LineStyle.Dashed
        }
    }
}
