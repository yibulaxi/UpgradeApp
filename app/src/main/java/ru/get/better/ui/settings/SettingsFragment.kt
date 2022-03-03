package ru.get.better.ui.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.ConfigurationCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.greenrobot.eventbus.EventBus
import ru.get.better.App
import ru.get.better.BuildConfig
import ru.get.better.R
import ru.get.better.databinding.FragmentSettingsBinding
import ru.get.better.event.UpdateDifficultyEvent
import ru.get.better.event.UpdateLocaleEvent
import ru.get.better.model.AllLogo
import ru.get.better.navigation.Navigator
import ru.get.better.ui.base.BaseFragment
import ru.get.better.util.LocaleUtils
import ru.get.better.util.ext.observeOnce
import ru.get.better.vm.SettingsViewModel
import ru.get.better.vm.UserDiaryViewModel
import ru.get.better.vm.UserSettingsViewModel
import timber.log.Timber
import java.util.*


class SettingsFragment : BaseFragment<SettingsViewModel, FragmentSettingsBinding>(
    ru.get.better.R.layout.fragment_settings,
    SettingsViewModel::class,
    Handler::class
) {

    private var allowChangeDifficulty = false
    private var allowChangeLocale = false

    private val userSettingsViewModel: UserSettingsViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(
            UserSettingsViewModel::class.java
        )
    }

    private val userDiaryViewModel: UserDiaryViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(
            UserDiaryViewModel::class.java
        )
    }

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)

        binding.version.text = getString(R.string.version) + " " +  BuildConfig.VERSION_NAME

        binding.difficultySpinner.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem ->
            if (allowChangeDifficulty)
                EventBus.getDefault().post(UpdateDifficultyEvent(newIndex))
        }

        binding.localeSpinner.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem ->
            if (allowChangeLocale) {
//                EventBus.getDefault().post(
//                    UpdateLocaleEvent(
//                        if (newIndex == 0) "ru"
//                        else "en"
//                    )
//                )

                App.preferences.locale =
                    if (newIndex == 0) "ru"
                    else "en"

                val locale = Locale(
                    if (newIndex == 0) "ru"
                    else "en"
                )
                Locale.setDefault(locale)
                val resources = requireActivity().resources
                val config = resources.configuration
                config.setLocale(locale)
                resources.updateConfiguration(config, resources.displayMetrics)

                Navigator.refresh(this@SettingsFragment)
            }
        }

        binding.icon.setImageResource(AllLogo().getRandomLogo())
        userSettingsViewModel.getUserSettingsById(
            App.preferences.uid!!
        )
            .observeOnce(viewLifecycleOwner, {
                binding.name.text = it!!.login
                binding.difficultySpinner.selectItemByIndex(it.difficulty!!.toInt())
                binding.difficultySpinner.setOnSpinnerOutsideTouchListener { view, motionEvent ->
                    binding.difficultySpinner.dismiss()
                }
                allowChangeDifficulty = true

                setupLocaleSpinner(App.preferences.locale)
            })
    }

    private fun setupLocaleSpinner(locale: String?) {
        binding.localeSpinner.setOnSpinnerOutsideTouchListener { view, motionEvent ->
            binding.localeSpinner.dismiss()
        }

        if (!locale.isNullOrEmpty()) {
            binding.localeSpinner.selectItemByIndex(
                if (locale == "ru") 0
                else 1
            )
        }
        allowChangeLocale = true
    }

    override fun onViewModelReady(viewModel: SettingsViewModel) {

    }

    inner class Handler {

        fun onFaqBlockClicked(v: View) {
            Navigator.fromSettingsToFaq(this@SettingsFragment)
        }

        fun onWriteBlockClicked(v: View) {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/velkonost")))
            } catch (e: Exception) {
                Timber.e(e)
            }
        }

        fun onRateBlockClicked(v: View) {
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
                    App.preferences.isDiaryHabitsSpotlightShown = false
                    App.preferences.isMainAddPostSpotlightShown = false
                    App.preferences.isMetricWheelSpotlightShown = false

                    userSettingsViewModel.resetUserSettings()
                    userDiaryViewModel.resetDiary()

                    Navigator.settingsToSplash(this@SettingsFragment)
                }
        }
    }
}