package ru.get.better.vm

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import ru.get.better.util.RxViewModel
import javax.inject.Inject

open class BaseViewModel @Inject constructor() : RxViewModel() {

    override fun onCleared() {
        EventBus.getDefault().unregister(this)
        super.onCleared()
    }

    @Subscribe
    fun onEvent(event: Any) {
        //Dummy event subscription to prevent exceptions
    }
}