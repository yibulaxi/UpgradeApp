package com.velkonost.upgrade.ui.splash

import com.velkonost.upgrade.util.RxViewModel
import com.velkonost.upgrade.util.SingleLiveEvent
import javax.inject.Inject

class SplashViewModel @Inject constructor(
) : RxViewModel() {

    val errorEvent = SingleLiveEvent<Error>()
}