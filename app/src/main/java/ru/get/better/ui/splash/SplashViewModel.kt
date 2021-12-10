package ru.get.better.ui.splash

import ru.get.better.util.RxViewModel
import ru.get.better.util.SingleLiveEvent
import javax.inject.Inject

class SplashViewModel @Inject constructor(
) : RxViewModel() {

    val errorEvent = SingleLiveEvent<Error>()
}