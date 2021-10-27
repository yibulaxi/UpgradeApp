package com.velkonost.upgrade.ui.settings

import android.os.Bundle
import androidx.core.content.ContextCompat
import com.jaeger.library.StatusBarUtil
import com.velkonost.upgrade.databinding.FragmentSettingsBinding
import com.velkonost.upgrade.ui.HomeViewModel
import com.velkonost.upgrade.ui.base.BaseFragment
import com.velkonost.upgrade.ui.metric.MetricFragment

class SettingsFragment : BaseFragment<HomeViewModel, FragmentSettingsBinding>(
    com.velkonost.upgrade.R.layout.fragment_settings,
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

    inner class Handler {}
}