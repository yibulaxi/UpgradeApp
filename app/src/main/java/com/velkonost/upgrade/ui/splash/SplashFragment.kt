package com.velkonost.upgrade.ui.splash

import com.velkonost.upgrade.R
import com.velkonost.upgrade.databinding.FragmentSplashBinding
import com.velkonost.upgrade.ui.base.BaseFragment

class SplashFragment : BaseFragment<SplashViewModel, FragmentSplashBinding>(
    R.layout.fragment_splash,
    SplashViewModel::class,
    Handler::class
) {
    inner class Handler {

    }
}