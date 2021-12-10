package ru.get.better.ui.splash

import android.animation.Animator
import android.app.Activity
import android.os.Bundle
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
import ru.get.better.navigation.Navigator
import ru.get.better.ui.base.BaseFragment
import ru.get.better.ui.view.SimpleCustomSnackbar
import ru.get.better.util.ext.observeOnce
import ru.get.better.vm.UserSettingsViewModel

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
                    animateText = it?.greeting ?: "GET BETTER"
                    start()
                }
        }
    }

    private fun start() {
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
//                createSignInIntent()
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

//    private fun createSignInIntent() {
//        val providers = arrayListOf(
//            AuthUI.IdpConfig.EmailBuilder().build(),
//            AuthUI.IdpConfig.PhoneBuilder().build(),
//            AuthUI.IdpConfig.GoogleBuilder().build()
//        )
//
//        val signInIntent = AuthUI.getInstance()
//            .createSignInIntentBuilder()
//            .setAvailableProviders(providers)
//            .setIsSmartLockEnabled(true)
//            .setLogo(R.drawable.logo)
//            .setTheme(R.style.AuthUITheme)
//            .build()
//
//        signInLauncher.launch(signInIntent)
//    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == Activity.RESULT_OK) {

            val user = FirebaseAuth.getInstance().currentUser

            if (user != null && user.uid.isNotEmpty()) {
                App.preferences.uid = user.uid
//                App.preferences.userName = user.displayName

                if (response!!.isNewUser) {
                    onSignUpSuccess()
                    initNewUserData(user.uid, user.displayName ?: "Победитель")
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
        EventBus.getDefault()
            .post(
                InitUserSettingsEvent(
                    userId = userId,
                    login = login
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
            null,
            null,
            R.drawable.snack_success_gradient,
        )?.show()
    }

    private fun onSignInSuccess() {
        SimpleCustomSnackbar.make(
            binding.coordinator,
            getString(R.string.signin_success),
            Snackbar.LENGTH_SHORT,
            null,
            null,
            null,
            null,
            R.drawable.snack_success_gradient,
            R.drawable.snack_success_gradient,
        )?.show()
    }

    private fun onAuthFailed() {
        SimpleCustomSnackbar.make(
            binding.coordinator,
            getString(R.string.signin_error),
            Snackbar.LENGTH_SHORT,
            null,
            null,
            null,
            null,
            R.drawable.snack_warning_gradient,
            R.drawable.snack_warning_gradient,
        )?.show()
    }

    inner class Handler
}