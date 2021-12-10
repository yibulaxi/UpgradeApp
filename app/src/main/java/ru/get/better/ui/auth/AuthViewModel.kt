package ru.get.better.ui.auth

import ru.get.better.util.RxViewModel
import ru.get.better.util.SingleLiveEvent
import javax.inject.Inject

class AuthViewModel @Inject constructor(
) : RxViewModel() {

    val errorEvent = SingleLiveEvent<Error>()

}