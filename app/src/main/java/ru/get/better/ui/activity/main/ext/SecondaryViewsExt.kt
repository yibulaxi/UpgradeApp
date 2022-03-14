package ru.get.better.ui.activity.main.ext

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.dialog_rate.view.*
import ru.get.better.App
import ru.get.better.R
import ru.get.better.ui.activity.main.MainActivity

enum class SecondaryViews(id: Int) {
    Empty(-1),
    Affirmation(0),
    RateDialog(1),
    MetricSpotlight(2),
    DiarySpotlight(3),
    AddPostSpotlight(4)
}

fun MainActivity.setupRateDialog() {
    val view: View = layoutInflater.inflate(
        R.layout.dialog_rate,
        null
    )
    val alertDialogBuilder = AlertDialog.Builder(
        this,
        if (App.preferences.isDarkTheme) R.style.DialogThemeDark
        else R.style.DialogThemeLight
    )
    alertDialogBuilder.setPositiveButton(getString(R.string.rate), null)
    alertDialogBuilder.setNegativeButton(getString(R.string.later), null)

    val alertDialog: AlertDialog = alertDialogBuilder.create()
    alertDialog.setTitle(App.resourcesProvider.getStringLocale(R.string.rate_app))

    alertDialog.setCancelable(false)

    view.rateTitle.text = App.resourcesProvider.getStringLocale(R.string.rate_text)
    view.rateEndtitle.text = App.resourcesProvider.getStringLocale(R.string.rate_text2)
    view.rateAnim.playAnimation()

    view.rateContainer.setBackgroundColor(
        ContextCompat.getColor(
            this,
            if (App.preferences.isDarkTheme) R.color.colorDarkDialogAlertAddInterestBackground
            else R.color.colorLightDialogAlertAddInterestBackground
        )
    )

    view.rateTitle.setTextColor(
        ContextCompat.getColor(
            this,
            if (App.preferences.isDarkTheme) R.color.colorDarkDialogAlertAddInterestNameText
            else R.color.colorLightDialogAlertAddInterestNameText
        )
    )

    view.rateEndtitle.setTextColor(
        ContextCompat.getColor(
            this,
            if (App.preferences.isDarkTheme) R.color.colorDarkDialogAlertAddInterestNameText
            else R.color.colorLightDialogAlertAddInterestNameText
        )
    )

    alertDialog.setOnShowListener {
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val uri: Uri = Uri.parse("market://details?id=ru.get.better")
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)

            goToMarket.addFlags(
                Intent.FLAG_ACTIVITY_NO_HISTORY or
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            )

            try {
                startActivity(goToMarket)
            } catch (e: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=ru.get.better")
                    )
                )
            }

            App.preferences.isAppRated = true

            alertDialog.dismiss()
            currentSecondaryView = SecondaryViews.Empty
        }

        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
            App.preferences.isRateAppLater = true

            alertDialog.dismiss()
            currentSecondaryView = SecondaryViews.Empty
        }
    }

    alertDialog.setView(view)
    alertDialog.show()
    currentSecondaryView = SecondaryViews.RateDialog
}