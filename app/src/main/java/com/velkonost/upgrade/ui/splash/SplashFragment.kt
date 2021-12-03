package com.velkonost.upgrade.ui.splash

import android.animation.Animator
import android.app.Activity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.jaeger.library.StatusBarUtil
import com.velkonost.upgrade.App
import com.velkonost.upgrade.R
import com.velkonost.upgrade.databinding.FragmentSplashBinding
import com.velkonost.upgrade.event.ChangeNavViewVisibilityEvent
import com.velkonost.upgrade.event.InitUserSettingsEvent
import com.velkonost.upgrade.event.LoadMainEvent
import com.velkonost.upgrade.navigation.Navigator
import com.velkonost.upgrade.ui.base.BaseFragment
import com.velkonost.upgrade.ui.view.SimpleCustomSnackbar
import com.velkonost.upgrade.util.ext.observeOnce
import com.velkonost.upgrade.vm.UserSettingsViewModel
import org.greenrobot.eventbus.EventBus

class SplashFragment : BaseFragment<SplashViewModel, FragmentSplashBinding>(
    R.layout.fragment_splash,
    SplashViewModel::class,
    Handler::class
) {

    val userSettingsViewModel: UserSettingsViewModel by viewModels { viewModelFactory }

    private var allowGoNext: Boolean = true

    private var animateText: String = "UPGRADE"

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)
        StatusBarUtil.setColor(
            requireActivity(),
            ContextCompat.getColor(requireContext(), R.color.colorWhite),
            0
        )
        StatusBarUtil.setLightMode(requireActivity())
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
                    animateText = it?.greeting?: "UPGRADE"
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
                    initNewUserData(user.uid, user.displayName?: "Победитель")
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