package ru.get.better.ui.faq.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.cachapa.expandablelayout.ExpandableLayout
import net.cachapa.expandablelayout.ExpandableLayout.OnExpansionUpdateListener
import ru.get.better.R

class FaqAdapter(
    private val recyclerView: RecyclerView,
    private val faqs: List<Pair<String, String>>
) : RecyclerView.Adapter<FaqAdapter.ViewHolder>() {

    private var selectedItem = UNSELECTED

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_faq, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return faqs.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener, OnExpansionUpdateListener {

        private val expandableLayout: ExpandableLayout =
            itemView.findViewById(R.id.expandable_layout)
        private val expandButton: TextView
        private val text: TextView

        fun bind() {
            val position = adapterPosition
            val isSelected = position == selectedItem
            expandButton.text = faqs[position].first

            expandButton.isSelected = isSelected
            expandableLayout.setExpanded(isSelected, false)

            text.text = faqs[position].second
        }

        override fun onClick(view: View?) {
            val holder = recyclerView.findViewHolderForAdapterPosition(selectedItem) as ViewHolder?
            if (holder != null) {
                holder.expandButton.isSelected = false
                holder.expandableLayout.collapse()
            }
            val position = adapterPosition
            if (position == selectedItem) {
                selectedItem = UNSELECTED
            } else {
                expandButton.isSelected = true
                expandableLayout.expand()
                selectedItem = position
            }
        }

        override fun onExpansionUpdate(expansionFraction: Float, state: Int) {
            Log.d("ExpandableLayout", "State: $state")
            if (state == ExpandableLayout.State.EXPANDING) {
                recyclerView.smoothScrollToPosition(adapterPosition)
            }
        }

        init {
            expandableLayout.setInterpolator(OvershootInterpolator())
            expandableLayout.setOnExpansionUpdateListener(this)

            expandButton = itemView.findViewById(R.id.expand_button)
            expandButton.setOnClickListener(this)

            text = itemView.findViewById(R.id.text)
        }
    }

    companion object {
        private const val UNSELECTED = -1
    }
}