package ru.get.better.ui.splash

import android.animation.Animator
import android.app.Activity
import android.content.res.Configuration
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.os.ConfigurationCompat
import androidx.fragment.app.viewModels
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import org.greenrobot.eventbus.EventBus
import ru.get.better.App
import ru.get.better.R
import ru.get.better.databinding.FragmentSplashBinding
import ru.get.better.event.ChangeNavViewVisibilityEvent
import ru.get.better.event.InitUserSettingsEvent
import ru.get.better.event.LoadMainEvent
import ru.get.better.event.UpdateThemeEvent
import ru.get.better.navigation.Navigator
import ru.get.better.ui.base.BaseFragment
import ru.get.better.ui.view.SimpleCustomSnackbar
import ru.get.better.util.ext.observeOnce
import ru.get.better.vm.UserSettingsViewModel
import java.util.*

class SplashFragment : BaseFragment<SplashViewModel, FragmentSplashBinding>(
    R.layout.fragment_splash,
    SplashViewModel::class,
    Handler::class
) {

    val userSettingsViewModel: UserSettingsViewModel by viewModels { viewModelFactory }

    private var allowGoNext: Boolean = true

    private var animateText: String = "GET BETTER"

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)

        EventBus.getDefault().post(
            ChangeNavViewVisibilityEvent(
                isVisible = false
            )
        )

        if (App.preferences.uid.isNullOrEmpty()) {
            start()
        } else {
            userSettingsViewModel
                .getUserSettingsById(App.preferences.uid!!)
                .observeOnce(this) {
                    animateText = it?.let {
                        App.resourcesProvider.getStringLocale(
                            R.string.hello, App.preferences.locale?: "ru"
                        ) + " " + it.login
                    } ?: "GET BETTER"
                    start()
                }
        }
    }

    override fun updateThemeAndLocale() {
        binding.splashContainer.setBackgroundColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSplashBackground
            else R.color.colorLightFragmentSplashBackground
        ))

        binding.logoText.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSplashLogoTextText
            else R.color.colorLightFragmentSplashLogoTextText
        ))
    }

    private fun setupLocale() {
        if (App.preferences.isFirstLaunch || App.preferences.locale.isNullOrEmpty()) {
            App.preferences.isFirstLaunch = false

            val locale = ConfigurationCompat.getLocales(resources.configuration)[0].language
            App.preferences.locale =
                if (locale == "ru" || locale == "ua" || locale == "kz" || locale == "be" || locale == "uk") "ru"
                else "en"
        }

        Locale.setDefault(Locale(App.preferences.locale ?: "ru"))
        val resources = requireActivity().resources
        val config = resources.configuration
        config.setLocale(Locale(App.preferences.locale ?: "ru"))
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun setupInitTheme() {
        if (App.preferences.isFirstLaunch) {
            EventBus.getDefault().post(
                UpdateThemeEvent(
                    when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
                        Configuration.UI_MODE_NIGHT_NO -> false
                        Configuration.UI_MODE_NIGHT_YES -> true
                        Configuration.UI_MODE_NIGHT_UNDEFINED -> false
                        else -> false
                    }
                )
            )
        } else {
//            EventBus.getDefault().post(
//                UpdateThemeEvent(
//                    App.preferences.isDarkTheme
//                )
//            )
        }

    }

    private fun start() {
        setupInitTheme()
        setupLocale()

        binding.animationView.imageAssetsFolder = "images"
        binding.animationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationEnd(animation: Animator?) {
                goNext()
            }

            override fun onAnimationRepeat(animation: Animator?) {
                goNext()
            }

            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationStart(animation: Animator?) {
                binding.logoText.setCharacterDelay(
                    (150L * 7) / animateText.length
                )
                binding.logoText.animateText(animateText)
            }
        })
        binding.animationView.playAnimation()
    }

    override fun onStart() {
        super.onStart()
        allowGoNext = true
    }

    private fun goNext() {
        if (binding.logoText.isAnimationLoaded && allowGoNext) {
            if (App.preferences.uid.isNullOrEmpty()) {
                Navigator.splashToAuth(this@SplashFragment)
            } else {
                userSettingsViewModel
                    .getUserSettingsById(App.preferences.uid!!)
                    .observeOnce(this) {
                        if (it!!.isInterestsInitialized!!) {
                            EventBus.getDefault().post(
                                LoadMainEvent(
                                    isAuthSuccess = true,
                                    this@SplashFragment
                                )
                            )
                            allowGoNext = false

                        } else {
                            Navigator.splashToWelcome(this@SplashFragment)
                        }
                    }
            }
        }
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == Activity.RESULT_OK) {

            val user = FirebaseAuth.getInstance().currentUser

            if (user != null && user.uid.isNotEmpty()) {
                App.preferences.uid = user.uid
//                App.preferences.userName = user.displayName

                if (response!!.isNewUser) {
                    onSignUpSuccess()
                    initNewUserData(user.uid, user.displayName ?: getString(R.string.winner_name))
                } else {
//                    App.preferences.isInterestsInitialized = true
                    onSignInSuccess()
                    goNext()
                }

            } else {
                onAuthFailed()
            }
        } else {
            onAuthFailed()
        }
    }

    private fun initNewUserData(
        userId: String,
        login: String
    ) {

        val locale = ConfigurationCompat.getLocales(resources.configuration)[0].language

        EventBus.getDefault()
            .post(
                InitUserSettingsEvent(
                    userId = userId,
                    login = login,
                    locale =
                    if (locale == "ru" || locale == "ua" || locale == "kz" || locale == "be" || locale == "uk") "ru"
                    else "en"
                )
            )

        Navigator.splashToWelcome(this@SplashFragment)
    }

    private fun onSignUpSuccess() {
        SimpleCustomSnackbar.make(
            binding.coordinator,
            getString(R.string.signup_success),
            Snackbar.LENGTH_SHORT,
            null,
            null,
            R.drawable.ic_check,
            null,
            R.drawable.snack_success_gradient_light,
            R.drawable.snack_success_gradient_light,
        )?.show()
    }

    private fun onSignInSuccess() {
        SimpleCustomSnackbar.make(
            binding.coordinator,
            getString(R.string.signin_success),
            Snackbar.LENGTH_SHORT,
            null,
            null,
            R.drawable.ic_check,
            null,
            R.drawable.snack_success_gradient_light,
            R.drawable.snack_success_gradient_light,
        )?.show()
    }

    private fun onAuthFailed() {
        SimpleCustomSnackbar.make(
            binding.coordinator,
            getString(R.string.signin_error),
            Snackbar.LENGTH_SHORT,
            null,
            null,
            R.drawable.ic_close,
            null,
            R.drawable.snack_warning_gradient_light,
            R.drawable.snack_warning_gradient_light,
        )?.show()
    }

    inner class Handler
}