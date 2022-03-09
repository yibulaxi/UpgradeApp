package ru.get.better.push

import android.app.IntentService
import android.content.Intent
import android.util.Log


class NotificationService(name: String?) : IntentService(name) {

    constructor() : this("servicerykdtr")

    private val notificationHelper: NotificationHelper by lazy {
        NotificationHelper(
            applicationContext
        )
    }

    override fun onHandleIntent(intent: Intent?) {
        Log.d("keke", "service")
        notificationHelper.createNotification(
            title = "my title",
            message = "my message",
            link = "google.com",
            pageTitle = "pageTitle",
            filter = "filter",
            section = "section"
        )
//        val builder: Notification.Builder = Builder(this)
//        builder.setContentTitle("My Title")
//        builder.setContentText("This is the Body")
//        builder.setSmallIcon(R.drawable.whatever)
//        val notifyIntent = Intent(this, MainActivity::class.java)
//        val pendingIntent =
//            PendingIntent.getActivity(this, 2, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT)
//        //to be able to launch your activity from the notification
//        builder.setContentIntent(pendingIntent)
//        val notificationCompat: Notification = builder.build()
//        val managerCompat = NotificationManagerCompat.from(this)
//        managerCompat.notify(NOTIFICATION_ID, notificationCompat)
    }
}