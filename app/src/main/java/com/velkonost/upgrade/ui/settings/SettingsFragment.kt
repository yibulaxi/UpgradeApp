package com.velkonost.upgrade.ui.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import com.firebase.ui.auth.AuthUI
import com.jaeger.library.StatusBarUtil
import com.velkonost.upgrade.App
import com.velkonost.upgrade.BuildConfig
import com.velkonost.upgrade.databinding.FragmentSettingsBinding
import com.velkonost.upgrade.event.UpdateDifficultyEvent
import com.velkonost.upgrade.navigation.Navigator
import com.velkonost.upgrade.ui.base.BaseFragment
import com.velkonost.upgrade.vm.BaseViewModel
import com.velkonost.upgrade.vm.UserDiaryViewModel
import com.velkonost.upgrade.vm.UserSettingsViewModel
import org.greenrobot.eventbus.EventBus
import timber.log.Timber


class SettingsFragment : BaseFragment<BaseViewModel, FragmentSettingsBinding>(
    com.velkonost.upgrade.R.layout.fragment_settings,
    BaseViewModel::class,
    Handler::class
) {

    private val userSettingsViewModel: UserSettingsViewModel by lazy { ViewModelProviders.of(requireActivity()).get(
        UserSettingsViewModel::class.java) }

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)
        StatusBarUtil.setColor(
            requireActivity(),
            ContextCompat.getColor(requireContext(), com.velkonost.upgrade.R.color.colorWhite),
            0
        )
        StatusBarUtil.setLightMode(requireActivity())


        binding.name.text = App.preferences.userName
        binding.version.text = "Версия " + BuildConfig.VERSION_NAME

        binding.difficultySpinner.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem ->
            EventBus.getDefault().post(UpdateDifficultyEvent(newIndex))
        }
        binding.difficultySpinner.selectItemByIndex(
            userSettingsViewModel.userSettings.difficulty ?: 1
        )
    }

    inner class Handler {

        fun onWriteBlockClicked(v: View) {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/velkonost")))
            } catch (e: Exception) {
                Timber.e(e)
            }
        }

        fun onRateBlockClicked(v: View) {
            val uri: Uri = Uri.parse("market://details?id=com.velkonost.upgrade")
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
                        Uri.parse("http://play.google.com/store/apps/details?id=com.velkonost.upgrade")
                    )
                )
            }
        }

        fun onAboutClicked(v: View) {
            binding.blur.isVisible = true
            binding.aboutText.isVisible = true
        }

        fun onBlurClicked(v: View) {
            binding.blur.isVisible = false
            binding.aboutText.isVisible = false
        }

        fun onLogoutClicked(v: View) {
            AuthUI.getInstance()
                .signOut(requireContext())
                .addOnCompleteListener {
                    App.preferences.uid = ""
                    App.preferences.userName = ""

                    Navigator.settingsToSplash(this@SettingsFragment)
                }
        }
    }
}