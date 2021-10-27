package com.velkonost.upgrade.ui.welcome

import com.velkonost.upgrade.util.RxViewModel
import com.velkonost.upgrade.util.SingleLiveEvent
import javax.inject.Inject

class WelcomeViewModel  @Inject constructor(
) : RxViewModel() {

    val errorEvent = SingleLiveEvent<Error>()
}