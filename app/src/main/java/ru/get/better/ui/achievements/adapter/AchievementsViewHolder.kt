package ru.get.better.ui.achievements.adapter

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import ru.get.better.R

class AchievementsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val title: TextView = itemView.findViewById(R.id.title)
    val subtitle: TextView = itemView.findViewById(R.id.subtitle)
    val checkBlock: ConstraintLayout = itemView.findViewById(R.id.checkBlock)
    val icCheck: AppCompatImageView = itemView.findViewById(R.id.icCheck)
    val timelineBlock: MaterialCardView = itemView.findViewById(R.id.timelineBlock)
}