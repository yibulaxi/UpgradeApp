package ru.get.better

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.res.Configuration
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import lv.chi.photopicker.ChiliPhotoPicker
import ru.get.better.di.AppModule
import ru.get.better.di.DaggerAppComponent
import ru.get.better.rest.di.RetrofitModule
import ru.get.better.util.GlideImageLoader
import ru.get.better.util.LocaleUtils
import ru.get.better.util.Preferences
import ru.get.better.util.ResourcesProvider
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
        resourcesProvider = ResourcesProvider(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        ChiliPhotoPicker.init(
            loader = GlideImageLoader(),
            authority = "com.velkonost.upgrade.fileprovider"
        )

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

        @SuppressLint("StaticFieldLeak")
        lateinit var resourcesProvider: ResourcesProvider

        val READ_EXTERNAL_STORAGE_REQUEST_CODE = 123
    }
}