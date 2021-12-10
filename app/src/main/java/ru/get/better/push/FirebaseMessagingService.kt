package ru.get.better.push

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.android.AndroidInjection
import timber.log.Timber

class FirebaseMessagingService : FirebaseMessagingService() {

//    @Inject
//    lateinit var repo: FirebaseRepo

    private val notificationHelper: NotificationHelper by lazy {
        NotificationHelper(
            applicationContext
        )
    }

    override fun onCreate() {
        super.onCreate()
        AndroidInjection.inject(this)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        var pageTitle: String? = null
        var filter: String? = null
        var section: String? = null

        remoteMessage.data.let {
            pageTitle = it["pageTitle"]
            filter = it["filter"]
            section = it["section"]
        }

        remoteMessage.notification?.let {
            Timber.d("RECEIVED PUSH: $it")
            Timber.d("RECEIVED PUSH: body: ${it.body}")
            Timber.d("RECEIVED PUSH: clickAction: ${it.clickAction}")
            notificationHelper.createNotification(
                it.title,
                it.body,
                it.clickAction,
                pageTitle,
                filter,
                section
            )
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

//        App.preferences.pushToken = token
        Timber.d("Token pushed. $token")

//        repo.sendToken(token, androidId)
//            .subscribe({
//
//            },
//                { Timber.e(it) })
    }
}