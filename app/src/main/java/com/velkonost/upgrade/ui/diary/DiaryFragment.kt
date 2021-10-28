package com.velkonost.upgrade.ui.diary

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import com.jaeger.library.StatusBarUtil
import com.velkonost.upgrade.R
import com.velkonost.upgrade.databinding.FragmentDiaryBinding
import com.velkonost.upgrade.databinding.FragmentMetricBinding
import com.velkonost.upgrade.event.UpdateDiaryEvent
import com.velkonost.upgrade.event.UpdateMetricsEvent
import com.velkonost.upgrade.navigation.Navigator
import com.velkonost.upgrade.ui.HomeViewModel
import com.velkonost.upgrade.ui.base.BaseFragment
import com.velkonost.upgrade.ui.diary.adapter.NotesAdapter
import com.velkonost.upgrade.ui.metric.MetricFragment
import org.greenrobot.eventbus.Subscribe

class DiaryFragment : BaseFragment<HomeViewModel, FragmentDiaryBinding>(
    R.layout.fragment_diary,
    HomeViewModel::class,
    Handler::class
) {
    private lateinit var adapter: NotesAdapter

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)
        StatusBarUtil.setColor(
            requireActivity(),
            ContextCompat.getColor(requireContext(), R.color.colorWhite),
            0
        )
        StatusBarUtil.setLightMode(requireActivity())

        binding.viewModel = ViewModelProviders.of(requireActivity()).get(HomeViewModel::class.java)

        setupDiary()
    }

    private fun setupDiary() {
        if (binding.viewModel!!.getDiary().notes.size == 0) {
            binding.emptyText.isVisible = true
            binding.recycler.isVisible = false
        } else {
            binding.emptyText.isVisible = false
            binding.recycler.isVisible = true

            adapter = NotesAdapter(context!!, binding.viewModel!!.getDiary().notes)
            binding.recycler.adapter = adapter
        }
    }

    @Subscribe
    fun onUpdateDiaryEvent(e: UpdateDiaryEvent) {
//        if (isAdded) {
        Navigator.refresh(this@DiaryFragment)
//        }
    }

    inner class Handler {}
}