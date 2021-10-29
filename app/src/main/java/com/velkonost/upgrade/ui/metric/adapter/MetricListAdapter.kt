package com.velkonost.upgrade.ui.metric.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.velkonost.upgrade.model.Interest

class MetricListAdapter(
    private val context: Context,
    private val interests: MutableList<Interest>
) : RecyclerView.Adapter<MetricListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MetricListViewHolder.newInstance(parent, context)


    override fun onBindViewHolder(holder: MetricListViewHolder, position: Int) {
        holder.bind(interest = interests[position])
    }

    override fun getItemCount(): Int {
        return interests.size
    }
}