package ru.get.better

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import lv.chi.photopicker.ChiliPhotoPicker
import ru.get.better.di.AppModule
import ru.get.better.di.DaggerAppComponent
import ru.get.better.repo.databases.AppDatabase
import ru.get.better.repo.databases.ArticlesDatabase
import ru.get.better.rest.di.RetrofitModule
import ru.get.better.util.AnalyticEventsManager
import ru.get.better.util.GlideImageLoader
import ru.get.better.util.Preferences
import ru.get.better.util.ResourcesProvider
import timber.log.Timber


class App : DaggerApplication() {

    private val appComponent = DaggerAppComponent.builder()
        .appModule(AppModule(this))
        .retrofitModule(RetrofitModule(this))
        .build()

    private lateinit var firebaseAnalytics: FirebaseAnalytics


    override fun onCreate() {
        super.onCreate()

        instance = this

        firebaseAnalytics = Firebase.analytics
        preferences = Preferences(this@App)
        resourcesProvider = ResourcesProvider(this@App)
        analyticsEventsManager = AnalyticEventsManager(firebaseAnalytics)
        database = AppDatabase(this@App)
        articlesDatabase = ArticlesDatabase(this@App)

        GlobalScope.launch(Dispatchers.IO, CoroutineStart.DEFAULT) {
            ChiliPhotoPicker.init(
                loader = GlideImageLoader(),
                authority = "ru.get.better.fileprovider"
            )
        }


        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }


        appLaunchedLogic()
        createNotificationChannel()
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> = appComponent

    private fun createNotificationChannel() {
        GlobalScope.launch(Dispatchers.IO) {
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
    }

    private fun appLaunchedLogic() {
        GlobalScope.launch(Dispatchers.IO) {
            preferences.launchCount += 1

            if (preferences.firstLaunchDate == 0L) {
                preferences.firstLaunchDate = System.currentTimeMillis()
            }
        }
    }

    companion object {
        lateinit var instance: App
        lateinit var preferences: Preferences
        lateinit var database: AppDatabase
        lateinit var articlesDatabase: ArticlesDatabase


        @SuppressLint("StaticFieldLeak")
        lateinit var resourcesProvider: ResourcesProvider
        lateinit var analyticsEventsManager: AnalyticEventsManager

        val READ_EXTERNAL_STORAGE_REQUEST_CODE = 123

        val DAYS_UNTIL_RATE = 3
        val LAUNCHES_UNTIL_RATE = 3
    }
}