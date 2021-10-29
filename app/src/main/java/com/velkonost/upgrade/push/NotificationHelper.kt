package com.velkonost.upgrade.push

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.velkonost.upgrade.R
import com.velkonost.upgrade.ui.activity.main.MainActivity
import timber.log.Timber

class NotificationHelper(private val mContext: Context) {
    private var mNotificationManager: NotificationManager? = null
    private var mBuilder: NotificationCompat.Builder? = null

    /**
     * Create and push the notification
     */
    @SuppressLint("UnspecifiedImmutableFlag")
    fun createNotification(
        title: String?,
        message: String?,
        link: String?,
        pageTitle: String?,
        filter: String?,
        section: String?
    ) {
        /**Creates an explicit intent for an Activity in your app */
        val resultIntent = Intent(mContext, MainActivity::class.java)
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        resultIntent.putExtra("link", link)
        resultIntent.putExtra("pageTitle", pageTitle)
        resultIntent.putExtra("filter", filter)
        resultIntent.putExtra("section", section)

        Timber.d("title: $title")
        Timber.d("message: $message")
        Timber.d("link: $link")
        Timber.d("pageTitle: $pageTitle")
        Timber.d("filter: $filter")

        val resultPendingIntent = PendingIntent.getActivity(
            mContext,
            0 /* Request code */, resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        mBuilder = NotificationCompat.Builder(mContext)
            .setSmallIcon(R.drawable.ic_launcher_notification)
            .setColor(ContextCompat.getColor(mContext, R.color.colorWhite))
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
            .setContentIntent(resultPendingIntent)

        mNotificationManager =
            mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mBuilder?.setChannelId(mContext.getString(R.string.default_notification_channel_id))
        }
//        if (App.preferences.userId != 0)
//            mNotificationManager?.notify(0 /* Request Code */, mBuilder?.build())
    }
}