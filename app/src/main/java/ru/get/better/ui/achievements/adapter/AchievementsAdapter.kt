package ru.get.better.ui.achievements.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.lriccardo.timelineview.TimelineAdapter
import com.lriccardo.timelineview.TimelineView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import ru.get.better.App
import ru.get.better.R
import ru.get.better.event.UpdateThemeEvent
import ru.get.better.model.Achievement

class AchievementsAdapter(
    var items: List<Achievement>,
    ) : TimelineAdapter, RecyclerView.Adapter<AchievementsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_timeline, parent, false)
        return AchievementsViewHolder(view)
    }

    override fun onBindViewHolder(holder: AchievementsViewHolder, position: Int) {
        holder.title.text = items[position].title
        holder.title.setTextColor(
            ContextCompat.getColor(
                App.instance.applicationContext,
                if (App.preferences.isDarkTheme) R.color.colorDarkItemTimelineTitleText
                else R.color.colorLightItemTimelineTitleText
            )
        )

        holder.subtitle.setTextColor(
            ContextCompat.getColor(
                App.instance.applicationContext,
                if (App.preferences.isDarkTheme) R.color.colorDarkItemTimelineSubtitleText
                else R.color.colorLightItemTimelineSubtitleText
            )
        )

        holder.timelineBlock.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                App.instance.applicationContext,
                if (App.preferences.isDarkTheme) R.color.colorDarkItemTimelineBackgroundTint
                else R.color.colorLightItemTimelineBackgroundTint
            )
        )

        holder.checkBlock.setBackgroundColor(
            ContextCompat.getColor(
                App.instance.applicationContext,
                if (App.preferences.isDarkTheme) R.color.colorDarkItemTimelineCheckBlockBackground
                else R.color.colorLightItemTimelineCheckBlockBackground
            )
        )

        holder.icCheck.imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                App.instance.applicationContext,
                if (App.preferences.isDarkTheme) R.color.colorDarkItemTimelineIcCheckTint
                else R.color.colorLightItemTimelineIcCheckTint
            )
        )

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
        return when (items[position].isCompleted) {
            true -> TimelineView.LineStyle.Normal
            false -> TimelineView.LineStyle.Dashed
        }
    }
}
