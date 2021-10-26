package com.velkonost.upgrade.ui.auth

import com.velkonost.upgrade.util.RxViewModel
import com.velkonost.upgrade.util.SingleLiveEvent
import javax.inject.Inject

class AuthViewModel @Inject constructor(
) : RxViewModel() {

    val errorEvent = SingleLiveEvent<Error>()

}