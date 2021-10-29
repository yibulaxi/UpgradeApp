package com.velkonost.upgrade

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.velkonost.upgrade.di.AppModule
import com.velkonost.upgrade.di.DaggerAppComponent
import com.velkonost.upgrade.rest.di.RetrofitModule
import com.velkonost.upgrade.util.Preferences
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import timber.log.Timber


class App : DaggerApplication() {

    private val appComponent = DaggerAppComponent.builder()
        .appModule(AppModule(this))
        .retrofitModule(RetrofitModule(this))
        .build()

    override fun onCreate() {
        super.onCreate()

        instance = this
        preferences = Preferences(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        createNotificationChannel()
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> = appComponent

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel =
                NotificationChannel(
                    getString(R.string.default_notification_channel_id),
                    getString(R.string.default_notification_channel_id),
                    importance
                )
            notificationManager.createNotificationChannel(notificationChannel)
            Timber.d("isNotificationChannelCreated")
        }
    }

    companion object {
        lateinit var instance: App
        lateinit var preferences: Preferences
    }
}