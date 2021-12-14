package ru.get.better.ui.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import com.firebase.ui.auth.AuthUI
import org.greenrobot.eventbus.EventBus
import ru.get.better.App
import ru.get.better.BuildConfig
import ru.get.better.databinding.FragmentSettingsBinding
import ru.get.better.event.UpdateDifficultyEvent
import ru.get.better.navigation.Navigator
import ru.get.better.ui.base.BaseFragment
import ru.get.better.util.ext.observeOnce
import ru.get.better.vm.BaseViewModel
import ru.get.better.vm.UserDiaryViewModel
import ru.get.better.vm.UserSettingsViewModel
import timber.log.Timber


class SettingsFragment : BaseFragment<BaseViewModel, FragmentSettingsBinding>(
    ru.get.better.R.layout.fragment_settings,
    BaseViewModel::class,
    Handler::class
) {

    private var allowChangeDifficulty = false

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

        binding.version.text = "Версия " + BuildConfig.VERSION_NAME

        binding.difficultySpinner.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem ->
            if (allowChangeDifficulty)
                EventBus.getDefault().post(UpdateDifficultyEvent(newIndex))
        }

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
            })
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