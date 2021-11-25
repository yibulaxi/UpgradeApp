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

    fun updateInterestById(interest: Interest) {
        for (i in 0 until interests.size) {
            if (interests[i].id == interest.id) {
                interests[i] = interest
                notifyItemChanged(i)
            }

        }
    }

    fun addInterest(interest: Interest) {
        interests.add(interests.size - 1, interest)
        notifyItemInserted(interests.size - 2)
    }

    fun deleteInterestById(id: String) {
        for (i in 0 until interests.size) {
            if (interests[i].id == id) {
                interests.removeAt(i)
                notifyItemRemoved(i)
                break
            }
        }
    }
}