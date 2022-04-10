package ru.get.better.ui.splash

import android.animation.Animator
import android.app.Activity
import android.content.res.Configuration
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.os.ConfigurationCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
import ru.get.better.vm.UserSettingsViewModel
import java.util.*

class SplashFragment : BaseFragment<FragmentSplashBinding>(
    R.layout.fragment_splash,
    Handler::class
) {

    val userSettingsViewModel: UserSettingsViewModel by viewModels { viewModelFactory }

    private var allowGoNext: Boolean = true

    private var animateText: String = "GET BETTER"

    private var isGoNextCalled = false

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)

        GlobalScope.launch(Dispatchers.IO) {
            EventBus.getDefault().post(
                ChangeNavViewVisibilityEvent(
                    isVisible = false
                )
            )
        }

        GlobalScope.launch(Dispatchers.IO) {
            if (App.preferences.uid.isNullOrEmpty()) {
                start()
            } else {
                GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
                    userSettingsViewModel
                        .getUserSettingsById(App.preferences.uid!!).let {
                            animateText = it?.let {
                                App.resourcesProvider.getStringLocale(
                                    R.string.hello, App.preferences.locale
                                ) + " " + it.login
                            } ?: "GET BETTER"
                            start()
                        }
                }
            }
        }
    }

    override fun updateThemeAndLocale() {
        lifecycleScope.launch(Dispatchers.IO) {
            binding.splashContainer.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSplashBackground
                    else R.color.colorLightFragmentSplashBackground
                )
            )

            binding.logoText.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkFragmentSplashLogoTextText
                    else R.color.colorLightFragmentSplashLogoTextText
                )
            )
        }
    }

    private fun setupLocale() {
        GlobalScope.launch(Dispatchers.IO) {
            if (App.preferences.isFirstLaunch || App.preferences.locale.isNullOrEmpty()) {
                App.preferences.isFirstLaunch = false

                val locale = ConfigurationCompat.getLocales(resources.configuration)[0].language
                App.preferences.locale =
                    if (locale == "ru" || locale == "ua" || locale == "kz" || locale == "be" || locale == "uk") "ru"
                    else "en"
            }

            Locale.setDefault(Locale(App.preferences.locale))
            val resources = requireActivity().resources
            val config = resources.configuration
            config.setLocale(Locale(App.preferences.locale))
            resources.updateConfiguration(config, resources.displayMetrics)
        }
    }

    private fun setupInitTheme() {
        GlobalScope.launch(Dispatchers.IO) {
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
            }
        }
    }

    private fun start() {
        setupInitTheme()
        setupLocale()

        binding.animationView.imageAssetsFolder = "images"
        binding.animationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationEnd(animation: Animator?) {}

            override fun onAnimationRepeat(animation: Animator?) {
                if (!isGoNextCalled) {
                    isGoNextCalled = true
                    goNext()
                }
            }

            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationStart(animation: Animator?) {
                binding.logoText.setCharacterDelay(
                    (150L * 7) / animateText.length
                )
                binding.logoText.animateText(animateText)


            }
        })

        lifecycleScope.launch(Dispatchers.IO) {
            binding.animationView.setAnimation(
                if (App.preferences.isDarkTheme) R.raw.logo_dark_anim
                else R.raw.logo_light_anim
            )
            binding.animationView.playAnimation()
        }
    }

    override fun onStart() {
        super.onStart()
        allowGoNext = true
    }

    private var auth: FirebaseAuth = Firebase.auth
    private fun loginAnonymously() {
        auth.signInAnonymously()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    App.preferences.uid = user?.uid

                    initNewUserData(user!!.uid, "userName")
                }
            }
    }

    private fun goNext() {
        if (binding.logoText.isAnimationLoaded && allowGoNext) {
            if (App.preferences.uid.isNullOrEmpty()) {
                loginAnonymously()
            } else {
                GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
                    userSettingsViewModel
                        .getUserSettingsById(App.preferences.uid!!)?.let {
                            if (App.preferences.isInterestsInitialized) {
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
    }

    private fun initNewUserData(
        userId: String,
        login: String
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            val locale = ConfigurationCompat.getLocales(resources.configuration)[0].language

            EventBus.getDefault()
                .post(
                    InitUserSettingsEvent(
                        userId = userId,
                        login = login,
                        locale =
                        if (
                            locale == "ru"
                            || locale == "ua"
                            || locale == "kz"
                            || locale == "be"
                            || locale == "uk"
                        ) "ru" else "en"
                    )
                )
        }.invokeOnCompletion {
            lifecycleScope.launch(Dispatchers.Main) {
                Navigator.splashToWelcome(this@SplashFragment)
            }
        }
    }

    inner class Handler {

    }
}