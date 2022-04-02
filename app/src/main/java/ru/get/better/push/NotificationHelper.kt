package ru.get.better.push

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Icon
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.BADGE_ICON_SMALL
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.IconCompat
import ru.get.better.App
import ru.get.better.R
import ru.get.better.model.AllLogo
import ru.get.better.ui.activity.main.MainActivity
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
            2 /* Request code */, resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val icon = AllLogo().getRandomLogo()
        mBuilder = NotificationCompat.Builder(mContext)
            .setSmallIcon(R.drawable.tire)
            .setLargeIcon(BitmapFactory.decodeResource(mContext.resources, icon))
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
        mNotificationManager?.notify(2 /* Request Code */, mBuilder?.build())
    }
}