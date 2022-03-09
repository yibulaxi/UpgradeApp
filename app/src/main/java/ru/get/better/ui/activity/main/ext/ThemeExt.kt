package ru.get.better.ui.activity.main.ext

import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import org.greenrobot.eventbus.EventBus
import ru.get.better.App
import ru.get.better.R
import ru.get.better.event.LoadMainEvent
import ru.get.better.ui.activity.main.MainActivity

fun MainActivity.setAppTheme(isDarkTheme: Boolean) {
//    AppCompatDelegate.setDefaultNightMode(themeMode)
    App.preferences.isDarkTheme = isDarkTheme

    AppCompatDelegate.setDefaultNightMode(
        if (App.preferences.isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES
        else AppCompatDelegate.MODE_NIGHT_NO
    )


    val intent = Intent(this, MainActivity::class.java)
    intent.putExtra("updateTheme", "true")
    startActivity(intent)
    overridePendingTransition(0, 0)
    finish()
//    this.recreate()

}