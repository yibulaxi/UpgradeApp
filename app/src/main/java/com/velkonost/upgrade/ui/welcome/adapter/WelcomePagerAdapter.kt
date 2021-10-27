package com.velkonost.upgrade.ui.welcome.adapter

import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.velkonost.upgrade.R
import com.velkonost.upgrade.model.*

class WelcomePagerAdapter(
    private val context: Context,
) : RecyclerView.Adapter<WelcomeViewHolder>() {

    private val interests = arrayListOf(
        Interest, Relationship(), Health(), Environment(), Finance(), Work(), Chill(), Creation(), Spirit()
    )

    fun getInterests() = interests

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        WelcomeViewHolder.newInstance(parent, context)


    override fun onBindViewHolder(holder: WelcomeViewHolder, position: Int) {
        holder.bind(interest = interests[position])
    }

    override fun getItemCount(): Int {
        return interests.size
    }
}