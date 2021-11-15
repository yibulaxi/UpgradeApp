package com.velkonost.upgrade.vm

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.velkonost.upgrade.event.LoadMainEvent
import com.velkonost.upgrade.navigation.Navigator
import com.velkonost.upgrade.util.RxViewModel
import com.velkonost.upgrade.util.SingleLiveEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject

open class BaseViewModel @Inject constructor(

) : RxViewModel() {

    val errorEvent = SingleLiveEvent<String>()
    val successEvent = SingleLiveEvent<String>()
    val setupNavMenuEvent = SingleLiveEvent<String>()

    var cloudFirestoreDatabase: FirebaseFirestore

    init {
//        EventBus.getDefault().register(this)
        cloudFirestoreDatabase = Firebase.firestore
    }

    override fun onCleared() {
        EventBus.getDefault().unregister(this)
        super.onCleared()
    }

    @Subscribe
    fun onEvent(event: Any) {
        //Dummy event subscription to prevent exceptions
    }
}