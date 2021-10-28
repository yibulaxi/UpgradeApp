package com.velkonost.upgrade.ui.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.jaeger.library.StatusBarUtil
import com.velkonost.upgrade.App
import com.velkonost.upgrade.BuildConfig
import com.velkonost.upgrade.R
import com.velkonost.upgrade.databinding.FragmentSettingsBinding
import com.velkonost.upgrade.ui.HomeViewModel
import com.velkonost.upgrade.ui.activity.main.MainActivity
import com.velkonost.upgrade.ui.base.BaseFragment
import com.velkonost.upgrade.ui.metric.MetricFragment
import timber.log.Timber

class SettingsFragment : BaseFragment<HomeViewModel, FragmentSettingsBinding>(
    com.velkonost.upgrade.R.layout.fragment_settings,
    HomeViewModel::class,
    Handler::class
) {
    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)
        StatusBarUtil.setColor(
            requireActivity(),
            ContextCompat.getColor(requireContext(), R.color.colorWhite),
            0
        )
        StatusBarUtil.setLightMode(requireActivity())

        binding.name.text = App.preferences.userName
        binding.version.text = "Версия " + BuildConfig.VERSION_NAME
    }

    inner class Handler {

        fun onWriteBlockClicked(v: View) {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/velkonost")))
            } catch (e: Exception) { // show error message
                Timber.e(e)

            }
        }

        fun onRateBlockClicked(v: View) {
            val uri: Uri = Uri.parse("market://details?id=com.velkonost.upgrade")
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            // To count with Play market backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            try {
                startActivity(goToMarket)
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=com.velkonost.upgrade")))
            }
        }
    }
}