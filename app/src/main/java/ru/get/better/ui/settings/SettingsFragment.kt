package ru.get.better.ui.settings

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.transition.Fade
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import com.firebase.ui.auth.AuthUI
import com.google.android.material.transition.MaterialFadeThrough
import kotlinx.android.synthetic.main.fragment_settings.*
import org.greenrobot.eventbus.EventBus
import ru.get.better.App
import ru.get.better.BuildConfig
import ru.get.better.R
import ru.get.better.databinding.FragmentSettingsBinding
import ru.get.better.event.UpdateDifficultyEvent
import ru.get.better.event.UpdateThemeEvent
import ru.get.better.model.AllLogo
import ru.get.better.navigation.Navigator
import ru.get.better.ui.base.BaseFragment
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

    private var currentDifficulty: Int = 0

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


//        requireView().post {
        binding.version.text = getString(R.string.version) + " " + BuildConfig.VERSION_NAME

        setupThemeSwitch()
        setupPushSwitch()

        binding.difficultySpinner.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem ->
            if (allowChangeDifficulty) {
                currentDifficulty = newIndex
                EventBus.getDefault().post(UpdateDifficultyEvent(newIndex))
            }
        }


        binding.localeSpinner.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem ->
            if (allowChangeLocale) {

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

                EventBus.getDefault().post(
                    UpdateThemeEvent(
                        isDarkTheme = App.preferences.isDarkTheme,
                        withTextAnimation = true
                    )
                )

            }
        }

        binding.icon.setImageResource(AllLogo().getRandomLogo())
        userSettingsViewModel.getUserSettingsById(
            App.preferences.uid!!
        )
            .observeOnce(viewLifecycleOwner, {
                binding.name.text = it!!.login

                currentDifficulty = it.difficulty!!.toInt()
                binding.difficultySpinner.selectItemByIndex(currentDifficulty)
                binding.difficultySpinner.setOnSpinnerOutsideTouchListener { view, motionEvent ->
                    binding.difficultySpinner.dismiss()
                }
                allowChangeDifficulty = true

                setupLocaleSpinner(App.preferences.locale)
            })
//        }

    }

    private fun setupThemeSwitch() {
        binding.themeSwitch.isChecked = App.preferences.isDarkTheme
        binding.themeSwitch.setOnCheckedChangeListener { view, isChecked ->
            App.preferences.isDarkTheme = isChecked
            EventBus.getDefault().post(
                UpdateThemeEvent(
                    isChecked,
                    withAnimation = true
                )
            )
        }
    }

    private fun setupPushSwitch() {
        binding.pushSwitch.isChecked = App.preferences.isPushAvailable
        binding.pushSwitch.setOnCheckedChangeListener { view, isChecked ->
            App.preferences.isPushAvailable = isChecked
        }
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

    private val updateThemeDuration = 700L
    private val updateTextDuration = 500L
    override fun updateThemeAndLocale(
        withAnimation: Boolean,
        withTextAnimation: Boolean
    ) {

        if (!withAnimation) {
            if (withTextAnimation) {
                binding.title.animate()
                    .alpha(0f)
                    .setDuration(updateTextDuration)
                    .withEndAction {
                        binding.title.text =
                            App.resourcesProvider.getStringLocale(R.string.settings_title)
                        binding.title.animate()
                            .alpha(1f)
                            .duration = updateTextDuration
                    }

                binding.logout.animate()
                    .alpha(0f)
                    .setDuration(updateTextDuration)
                    .withEndAction {
                        binding.logout.text = App.resourcesProvider.getStringLocale(R.string.logout)
                        binding.logout.animate()
                            .alpha(1f)
                            .duration = updateTextDuration
                    }

                binding.themeTitle.animate()
                    .alpha(0f)
                    .setDuration(updateTextDuration)
                    .withEndAction {
                        binding.themeTitle.text =
                            App.resourcesProvider.getStringLocale(R.string.theme_title)
                        binding.themeTitle.animate()
                            .alpha(1f)
                            .duration = updateTextDuration
                    }

                binding.pushTitle.animate()
                    .alpha(0f)
                    .setDuration(updateTextDuration)
                    .withEndAction {
                        binding.pushTitle.text =
                            App.resourcesProvider.getStringLocale(R.string.push_notifications_title)
                        binding.pushTitle.animate()
                            .alpha(1f)
                            .duration = updateTextDuration
                    }

//                binding.pushValue.animate()
//                    .alpha(0f)
//                    .setDuration(updateTextDuration)
//                    .withEndAction {
//                        binding.pushValue.text =
//                            App.resourcesProvider.getStringLocale(R.string.soon)
//                        binding.pushValue.animate()
//                            .alpha(1f)
//                            .duration = updateTextDuration
//                    }

                binding.localeTitle.animate()
                    .alpha(0f)
                    .setDuration(updateTextDuration)
                    .withEndAction {
                        binding.localeTitle.text =
                            App.resourcesProvider.getStringLocale(R.string.locale_title)
                        binding.localeTitle.animate()
                            .alpha(1f)
                            .duration = updateTextDuration
                    }

                binding.localeSpinner.animate()
                    .alpha(0f)
                    .setDuration(updateTextDuration)
                    .withEndAction {
                        binding.localeSpinner.hint =
                            App.resourcesProvider.getStringLocale(R.string.system)
                        binding.localeSpinner.animate()
                            .alpha(1f)
                            .duration = updateTextDuration
                    }

                binding.localeValue.animate()
                    .alpha(0f)
                    .setDuration(updateTextDuration)
                    .withEndAction {
                        binding.localeValue.text =
                            App.resourcesProvider.getStringLocale(R.string.system)
                        binding.localeValue.animate()
                            .alpha(1f)
                            .duration = updateTextDuration
                    }

                binding.difficultyTitle.animate()
                    .alpha(0f)
                    .setDuration(updateTextDuration)
                    .withEndAction {
                        binding.difficultyTitle.text =
                            App.resourcesProvider.getStringLocale(R.string.difficulty_title)
                        binding.difficultyTitle.animate()
                            .alpha(1f)
                            .duration = updateTextDuration
                    }
                binding.difficultySpinner.animate()
                    .alpha(0f)
                    .setDuration(updateTextDuration)
                    .withEndAction {
                        binding.difficultySpinner.hint =
                            App.resourcesProvider.getStringLocale(R.string.soon)
                        binding.difficultySpinner.animate()
                            .alpha(1f)
                            .duration = updateTextDuration
                    }
                binding.difficultyValue.animate()
                    .alpha(0f)
                    .setDuration(updateTextDuration)
                    .withEndAction {
                        binding.difficultyValue.text =
                            App.resourcesProvider.getStringLocale(R.string.soon)
                        binding.difficultyValue.animate()
                            .alpha(1f)
                            .duration = updateTextDuration
                    }

                binding.version.animate()
                    .alpha(0f)
                    .setDuration(updateTextDuration)
                    .withEndAction {
                        binding.version.text =
                            App.resourcesProvider.getStringLocale(R.string.version)
                        binding.version.animate()
                            .alpha(1f)
                            .duration = updateTextDuration
                    }

                binding.about.animate()
                    .alpha(0f)
                    .setDuration(updateTextDuration)
                    .withEndAction {
                        binding.about.text =
                            App.resourcesProvider.getStringLocale(R.string.about_app)
                        binding.about.animate()
                            .alpha(1f)
                            .duration = updateTextDuration
                    }

                binding.faq.animate()
                    .alpha(0f)
                    .setDuration(updateTextDuration)
                    .withEndAction {
                        binding.faq.text =
                            App.resourcesProvider.getStringLocale(R.string.often_questions_title)
                        binding.faq.animate()
                            .alpha(1f)
                            .duration = updateTextDuration
                    }

                binding.rate.animate()
                    .alpha(0f)
                    .setDuration(updateTextDuration)
                    .withEndAction {
                        binding.rate.text = App.resourcesProvider.getStringLocale(R.string.rate_app)
                        binding.rate.animate()
                            .alpha(1f)
                            .duration = updateTextDuration
                    }

                binding.write.animate()
                    .alpha(0f)
                    .setDuration(updateTextDuration)
                    .withEndAction {
                        binding.write.text =
                            App.resourcesProvider.getStringLocale(R.string.write_developer)
                        binding.write.animate()
                            .alpha(1f)
                            .duration = updateTextDuration

                        binding.aboutTextTitle.text =
                            App.resourcesProvider.getStringLocale(R.string.app_description)

                        binding.difficultySpinner.setItems(R.array.difficulty_values)
                        binding.localeSpinner.setItems(R.array.locale_values)

                        binding.difficultySpinner.selectItemByIndex(currentDifficulty)

                        binding.localeSpinner.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem -> }
                        binding.localeSpinner.selectItemByIndex(
                            if (App.preferences.locale == "ru") 0
                            else 1
                        )
                        binding.localeSpinner.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem ->
                            if (allowChangeLocale) {

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

                                EventBus.getDefault().post(
                                    UpdateThemeEvent(
                                        isDarkTheme = App.preferences.isDarkTheme,
                                        withTextAnimation = true
                                    )
                                )

                            }
                        }
                    }
            } else {
                binding.title.text = App.resourcesProvider.getStringLocale(R.string.settings_title)
                binding.logout.text = App.resourcesProvider.getStringLocale(R.string.logout)
                binding.themeTitle.text =
                    App.resourcesProvider.getStringLocale(R.string.theme_title)
                binding.pushTitle.text =
                    App.resourcesProvider.getStringLocale(R.string.push_notifications_title)
//                binding.pushValue.text = App.resourcesProvider.getStringLocale(R.string.soon)
                binding.localeTitle.text =
                    App.resourcesProvider.getStringLocale(R.string.locale_title)
                binding.localeSpinner.hint = App.resourcesProvider.getStringLocale(R.string.system)
                binding.localeValue.text = App.resourcesProvider.getStringLocale(R.string.system)
                binding.difficultyTitle.text =
                    App.resourcesProvider.getStringLocale(R.string.difficulty_title)
                binding.difficultySpinner.hint =
                    App.resourcesProvider.getStringLocale(R.string.soon)
                binding.difficultyValue.text = App.resourcesProvider.getStringLocale(R.string.soon)
                binding.version.text = App.resourcesProvider.getStringLocale(R.string.version)
                binding.about.text = App.resourcesProvider.getStringLocale(R.string.about_app)
                binding.faq.text =
                    App.resourcesProvider.getStringLocale(R.string.often_questions_title)
                binding.rate.text = App.resourcesProvider.getStringLocale(R.string.rate_app)
                binding.write.text = App.resourcesProvider.getStringLocale(R.string.write_developer)

                binding.aboutTextTitle.text =
                    App.resourcesProvider.getStringLocale(R.string.app_description)

                binding.difficultySpinner.setItems(R.array.difficulty_values)
                binding.localeSpinner.setItems(R.array.locale_values)

                binding.difficultySpinner.selectItemByIndex(currentDifficulty)

                binding.localeSpinner.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem -> }
                binding.localeSpinner.selectItemByIndex(
                    if (App.preferences.locale == "ru") 0
                    else 1
                )
                binding.localeSpinner.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newItem ->
                    if (allowChangeLocale) {

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

                        EventBus.getDefault().post(
                            UpdateThemeEvent(
                                isDarkTheme = App.preferences.isDarkTheme,
                                withTextAnimation = true
                            )
                        )

                    }
                }
            }


        }

        updateTheme(withAnimation)


    }

    private fun updateTheme(
        withAnimation: Boolean
    ) {
        binding.separatorTheme.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsSeparatorLocaleBackground
                else R.color.colorLightFragmentSettingsSeparatorLocaleBackground
            )
        )

        binding.separatorLocale.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsSeparatorLocaleBackground
                else R.color.colorLightFragmentSettingsSeparatorLocaleBackground
            )
        )

        binding.separator1.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsSeparator1Background
                else R.color.colorLightFragmentSettingsSeparator1Background
            )
        )

        binding.separator2.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsSeparatorBackground
                else R.color.colorLightFragmentSettingsSeparatorBackground
            )
        )

        binding.separator3.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsSeparatorBackground
                else R.color.colorLightFragmentSettingsSeparatorBackground
            )
        )

        binding.separator4.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsSeparatorBackground
                else R.color.colorLightFragmentSettingsSeparatorBackground
            )
        )

        binding.separator5.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsSeparatorBackground
                else R.color.colorLightFragmentSettingsSeparatorBackground
            )
        )

        binding.aboutTextTitle.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsAboutTextText
                else R.color.colorLightFragmentSettingsAboutTextText
            )
        )

        binding.localeSpinner.arrowTint = ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsLocaleSpinnerArrowTint
            else R.color.colorLightFragmentSettingsLocaleSpinnerArrowTint
        )

        binding.localeSpinner.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsLocaleSpinnerText
                else R.color.colorLightFragmentSettingsLocaleSpinnerText
            )
        )

        binding.localeSpinner.setHintTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsLocaleSpinnerHint
                else R.color.colorLightFragmentSettingsLocaleSpinnerHint
            )
        )

        binding.localeSpinner.dividerColor = ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsLocaleSpinnerDivider
            else R.color.colorLightFragmentSettingsLocaleSpinnerDivider
        )

        binding.localeSpinner.spinnerPopupBackgroundColor = ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsLocaleSpinnerPopupBackground
            else R.color.colorLightFragmentSettingsLocaleSpinnerPopupBackground
        )

        binding.difficultySpinner.arrowTint = ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsLocaleSpinnerArrowTint
            else R.color.colorLightFragmentSettingsLocaleSpinnerArrowTint
        )

        binding.difficultySpinner.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsLocaleSpinnerText
                else R.color.colorLightFragmentSettingsLocaleSpinnerText
            )
        )

        binding.difficultySpinner.setHintTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsLocaleSpinnerHint
                else R.color.colorLightFragmentSettingsLocaleSpinnerHint
            )
        )

        binding.difficultySpinner.dividerColor = ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsLocaleSpinnerDivider
            else R.color.colorLightFragmentSettingsLocaleSpinnerDivider
        )

        binding.difficultySpinner.spinnerPopupBackgroundColor = ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsLocaleSpinnerPopupBackground
            else R.color.colorLightFragmentSettingsLocaleSpinnerPopupBackground
        )


        if (withAnimation) {
            val titleTextColorAnimation = ValueAnimator.ofObject(
                ArgbEvaluator(),
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorLightFragmentSettingsTitleText
                    else R.color.colorDarkFragmentSettingsTitleText
                ),
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsTitleText
                    else R.color.colorLightFragmentSettingsTitleText
                )
            )
            titleTextColorAnimation.duration = updateThemeDuration
            titleTextColorAnimation.addUpdateListener {
                binding.title.setTextColor(it.animatedValue.toString().toInt())
            }
            titleTextColorAnimation.start()
        } else {
            binding.title.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsTitleText
                    else R.color.colorLightFragmentSettingsTitleText
                )
            )
        }

        if (withAnimation) {
            val logoutContainerBackgroundTintAnimation = ValueAnimator.ofObject(
                ArgbEvaluator(),
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorLightFragmentSettingsLogoutBackgroundTint
                    else R.color.colorDarkFragmentSettingsLogoutBackgroundTint
                ),
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsLogoutBackgroundTint
                    else R.color.colorLightFragmentSettingsLogoutBackgroundTint
                )
            )
            logoutContainerBackgroundTintAnimation.duration = updateThemeDuration
            logoutContainerBackgroundTintAnimation.addUpdateListener {
                binding.logoutContainer.backgroundTintList = ColorStateList.valueOf(
                    it.animatedValue.toString().toInt()
                )
            }
            logoutContainerBackgroundTintAnimation.start()
        } else {
            binding.logoutContainer.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsLogoutBackgroundTint
                    else R.color.colorLightFragmentSettingsLogoutBackgroundTint
                )
            )
        }

        if (withAnimation) {
            val logoutTextColorAnimation = ValueAnimator.ofObject(
                ArgbEvaluator(),
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorLightFragmentSettingsLogoutText
                    else R.color.colorDarkFragmentSettingsLogoutText
                ),
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsLogoutText
                    else R.color.colorLightFragmentSettingsLogoutText
                )
            )
            logoutTextColorAnimation.duration = updateThemeDuration
            logoutTextColorAnimation.addUpdateListener {
                binding.logout.setTextColor(it.animatedValue.toString().toInt())
            }
            logoutTextColorAnimation.start()
        } else {
            binding.logout.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsLogoutText
                    else R.color.colorLightFragmentSettingsLogoutText
                )
            )
        }

        if (withAnimation) {
            val userBlockBackgroundTintAnimation = ValueAnimator.ofObject(
                ArgbEvaluator(),
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorLightFragmentSettingsUserBlockBackgroundTint
                    else R.color.colorDarkFragmentSettingsUserBlockBackgroundTint
                ),
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsUserBlockBackgroundTint
                    else R.color.colorLightFragmentSettingsUserBlockBackgroundTint
                )
            )
            userBlockBackgroundTintAnimation.duration = updateThemeDuration
            userBlockBackgroundTintAnimation.addUpdateListener {
                binding.userBlock.backgroundTintList = ColorStateList.valueOf(
                    it.animatedValue.toString().toInt()
                )
            }
            userBlockBackgroundTintAnimation.start()
        } else {
            binding.userBlock.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsUserBlockBackgroundTint
                    else R.color.colorLightFragmentSettingsUserBlockBackgroundTint
                )
            )
        }

        if (withAnimation) {
            val nameTextColorAnimation = ValueAnimator.ofObject(
                ArgbEvaluator(),
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorLightFragmentSettingsPushTitleText
                    else R.color.colorDarkFragmentSettingsPushTitleText
                ),
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsPushTitleText
                    else R.color.colorLightFragmentSettingsPushTitleText
                )
            )
            nameTextColorAnimation.duration = updateThemeDuration
            nameTextColorAnimation.addUpdateListener {
                binding.name.setTextColor(it.animatedValue.toString().toInt())
            }
            nameTextColorAnimation.start()
        } else {
            binding.name.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsPushTitleText
                    else R.color.colorLightFragmentSettingsPushTitleText
                )
            )
        }

        if (withAnimation) {
            val generalBlockBackgroundTintAnimation = ValueAnimator.ofObject(
                ArgbEvaluator(),
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorLightFragmentSettingsGeneralBlockBackgroundTint
                    else R.color.colorDarkFragmentSettingsGeneralBlockBackgroundTint
                ),
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsGeneralBlockBackgroundTint
                    else R.color.colorLightFragmentSettingsGeneralBlockBackgroundTint
                )
            )
            generalBlockBackgroundTintAnimation.duration = updateThemeDuration
            generalBlockBackgroundTintAnimation.addUpdateListener {
                binding.generalBlock.backgroundTintList = ColorStateList.valueOf(
                    it.animatedValue.toString().toInt()
                )
            }
            generalBlockBackgroundTintAnimation.start()
        } else {
            binding.generalBlock.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsGeneralBlockBackgroundTint
                    else R.color.colorLightFragmentSettingsGeneralBlockBackgroundTint
                )
            )
        }

        if (withAnimation) {
            val themeTitleAnimation = ValueAnimator.ofObject(
                ArgbEvaluator(),
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorLightFragmentSettingsPushTitleText
                    else R.color.colorDarkFragmentSettingsPushTitleText
                ),
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsPushTitleText
                    else R.color.colorLightFragmentSettingsPushTitleText
                )
            )
            themeTitleAnimation.duration = updateThemeDuration
            themeTitleAnimation.addUpdateListener {
                binding.themeTitle.setTextColor(it.animatedValue.toString().toInt())
                binding.pushTitle.setTextColor(it.animatedValue.toString().toInt())
                binding.localeTitle.setTextColor(it.animatedValue.toString().toInt())
                binding.difficultyTitle.setTextColor(it.animatedValue.toString().toInt())
            }
            themeTitleAnimation.start()
        } else {
            binding.themeTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsPushTitleText
                    else R.color.colorLightFragmentSettingsPushTitleText
                )
            )

            binding.pushTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsPushTitleText
                    else R.color.colorLightFragmentSettingsPushTitleText
                )
            )

            binding.localeTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsPushTitleText
                    else R.color.colorLightFragmentSettingsPushTitleText
                )
            )

            binding.difficultyTitle.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsPushTitleText
                    else R.color.colorLightFragmentSettingsPushTitleText
                )
            )
        }

        if (withAnimation) {
            val pushValueTextColorAnimation = ValueAnimator.ofObject(
                ArgbEvaluator(),
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorLightFragmentSettingsPushValueText
                    else R.color.colorDarkFragmentSettingsPushValueText
                ),
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsPushValueText
                    else R.color.colorLightFragmentSettingsPushValueText
                )
            )
            pushValueTextColorAnimation.duration = updateThemeDuration
            pushValueTextColorAnimation.addUpdateListener {
//                binding.pushValue.setTextColor(it.animatedValue.toString().toInt())
                binding.difficultyValue.setTextColor(it.animatedValue.toString().toInt())
            }
            pushValueTextColorAnimation.start()
        } else {
//            binding.pushValue.setTextColor(
//                ContextCompat.getColor(
//                    requireContext(),
//                    if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsPushValueText
//                    else R.color.colorLightFragmentSettingsPushValueText
//                )
//            )

            binding.difficultyValue.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsPushValueText
                    else R.color.colorLightFragmentSettingsPushValueText
                )
            )
        }

        if (withAnimation) {
            val localeValueTextColorAnimation = ValueAnimator.ofObject(
                ArgbEvaluator(),
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorLightFragmentSettingsLocaleValueText
                    else R.color.colorDarkFragmentSettingsLocaleValueText
                ),
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsLocaleValueText
                    else R.color.colorLightFragmentSettingsLocaleValueText
                )
            )
            localeValueTextColorAnimation.duration = updateThemeDuration
            localeValueTextColorAnimation.addUpdateListener {
                binding.localeValue.setTextColor(it.animatedValue.toString().toInt())
            }
            localeValueTextColorAnimation.start()
        } else {
            binding.localeValue.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsLocaleValueText
                    else R.color.colorDarkFragmentSettingsLocaleValueText
                )
            )
        }

        if (withAnimation) {
            val footerBlockCardBackgroundTintAnimation = ValueAnimator.ofObject(
                ArgbEvaluator(),
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorLightFragmentSettingsFooterBlockBackgroundTint
                    else R.color.colorDarkFragmentSettingsFooterBlockBackgroundTint
                ),
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsFooterBlockBackgroundTint
                    else R.color.colorLightFragmentSettingsFooterBlockBackgroundTint
                )
            )
            footerBlockCardBackgroundTintAnimation.duration = updateThemeDuration
            footerBlockCardBackgroundTintAnimation.addUpdateListener {
                binding.footerBlockCard.backgroundTintList = ColorStateList.valueOf(
                    it.animatedValue.toString().toInt()
                )
            }
            footerBlockCardBackgroundTintAnimation.start()
        } else {
            binding.footerBlockCard.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsFooterBlockBackgroundTint
                    else R.color.colorLightFragmentSettingsFooterBlockBackgroundTint
                )
            )
        }

        if (withAnimation) {
            val footerBlockTextColorAnimation = ValueAnimator.ofObject(
                ArgbEvaluator(),
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorLightFragmentSettingsVersionText
                    else R.color.colorDarkFragmentSettingsVersionText
                ),
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsVersionText
                    else R.color.colorLightFragmentSettingsVersionText
                )
            )
            footerBlockTextColorAnimation.duration = updateThemeDuration
            footerBlockTextColorAnimation.addUpdateListener {
                binding.version.setTextColor(it.animatedValue.toString().toInt())
                binding.about.setTextColor(it.animatedValue.toString().toInt())
                binding.faq.setTextColor(it.animatedValue.toString().toInt())
                binding.rate.setTextColor(it.animatedValue.toString().toInt())
                binding.write.setTextColor(it.animatedValue.toString().toInt())
            }
            footerBlockTextColorAnimation.start()
        } else {
            binding.version.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsVersionText
                    else R.color.colorLightFragmentSettingsVersionText
                )
            )

            binding.about.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsVersionText
                    else R.color.colorLightFragmentSettingsVersionText
                )
            )

            binding.faq.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsVersionText
                    else R.color.colorLightFragmentSettingsVersionText
                )
            )

            binding.rate.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsVersionText
                    else R.color.colorLightFragmentSettingsVersionText
                )
            )

            binding.write.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSettingsVersionText
                    else R.color.colorLightFragmentSettingsVersionText
                )
            )
        }

        if (withAnimation) {
            val containerBackgroundAnimation = ValueAnimator.ofObject(
                ArgbEvaluator(),
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorLightFragmentMetricBackground
                    else R.color.colorDarkFragmentMetricBackground
                ),
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkFragmentMetricBackground
                    else R.color.colorLightFragmentMetricBackground
                )
            )
            containerBackgroundAnimation.duration = updateThemeDuration
            containerBackgroundAnimation.addUpdateListener {
                binding.settingsContainer.setBackgroundColor(it.animatedValue.toString().toInt())
            }
            containerBackgroundAnimation.start()
        } else {
            binding.settingsContainer.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkFragmentMetricBackground
                    else R.color.colorLightFragmentMetricBackground
                )
            )
        }

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