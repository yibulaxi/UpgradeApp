package com.velkonost.upgrade.ui.settings.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skydoves.powerspinner.*

class DifficultyAdapterIconSpinnerAdapter(
    powerSpinnerView: PowerSpinnerView,
    override var index: Int,
    override var onSpinnerItemSelectedListener: OnSpinnerItemSelectedListener<IconSpinnerItem>?,
    override val spinnerView: PowerSpinnerView
) : RecyclerView.Adapter<IconSpinnerAdapter.IconSpinnerViewHolder>(),
    PowerSpinnerInterface<IconSpinnerItem> {



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): IconSpinnerAdapter.IconSpinnerViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: IconSpinnerAdapter.IconSpinnerViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun notifyItemSelected(index: Int) {
        TODO("Not yet implemented")
    }

    override fun setItems(itemList: List<IconSpinnerItem>) {
        TODO("Not yet implemented")
    }

}