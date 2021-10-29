package com.velkonost.upgrade.ui.achievements

import android.os.Bundle
import androidx.core.content.ContextCompat
import com.jaeger.library.StatusBarUtil
import com.velkonost.upgrade.databinding.FragmentAchievementsBinding
import com.velkonost.upgrade.ui.HomeViewModel
import com.velkonost.upgrade.ui.base.BaseFragment

class AchievementsFragment : BaseFragment<HomeViewModel, FragmentAchievementsBinding>(
    com.velkonost.upgrade.R.layout.fragment_achievements,
    HomeViewModel::class,
    Handler::class
) {

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)
        StatusBarUtil.setColor(
            requireActivity(),
            ContextCompat.getColor(requireContext(), com.velkonost.upgrade.R.color.colorWhite),
            0
        )
        StatusBarUtil.setLightMode(requireActivity())
    }

    inner class Handler
}