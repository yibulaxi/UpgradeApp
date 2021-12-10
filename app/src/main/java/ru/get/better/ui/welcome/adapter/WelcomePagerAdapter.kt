package ru.get.better.ui.welcome.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.get.better.model.*

class WelcomePagerAdapter(
    private val context: Context,
) : RecyclerView.Adapter<WelcomeViewHolder>() {

    private val interests = arrayListOf<Interest>(
        DefaultInterest,
        Relationship(),
        Health(),
        Environment(),
        Finance(),
        Work(),
        Chill(),
        Creation(),
        Spirit()
    )

    fun getInterests(): List<Interest> {
        interests.forEach { it.startValue = it.currentValue }

        return interests
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        WelcomeViewHolder.newInstance(parent, context)


    override fun onBindViewHolder(holder: WelcomeViewHolder, position: Int) {
        holder.bind(interest = interests[position])
    }

    override fun getItemCount(): Int {
        return interests.size
    }
}