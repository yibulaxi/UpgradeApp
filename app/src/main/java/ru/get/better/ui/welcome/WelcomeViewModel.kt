package ru.get.better.ui.welcome

import ru.get.better.util.RxViewModel
import ru.get.better.util.SingleLiveEvent
import javax.inject.Inject

class WelcomeViewModel @Inject constructor(
) : RxViewModel() {

    val errorEvent = SingleLiveEvent<Error>()
}