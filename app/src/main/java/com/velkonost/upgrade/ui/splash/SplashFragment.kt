package com.velkonost.upgrade.ui.splash

import android.animation.Animator
import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.jaeger.library.StatusBarUtil
import com.velkonost.upgrade.App
import com.velkonost.upgrade.R
import com.velkonost.upgrade.databinding.FragmentSplashBinding
import com.velkonost.upgrade.event.InitUserSettingsEvent
import com.velkonost.upgrade.navigation.Navigator
import com.velkonost.upgrade.ui.base.BaseFragment
import com.velkonost.upgrade.ui.view.SimpleCustomSnackbar
import org.greenrobot.eventbus.EventBus

class SplashFragment : BaseFragment<SplashViewModel, FragmentSplashBinding>(
    R.layout.fragment_splash,
    SplashViewModel::class,
    Handler::class
) {

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
                binding.logoText.animateText("UPGRADE")
            }
        })
    }

    private fun goNext() {
        if (binding.logoText.isAnimationLoaded) {
            if (App.preferences.uid.isNullOrEmpty()) {
                createSignInIntent()
            } else {
                if (App.preferences.isInterestsInitialized) {
                    Navigator.splashToMetric(this@SplashFragment)
                } else {
                    Navigator.splashToWelcome(this@SplashFragment)
                }
//                Navigator.splashToMetric(this@SplashFragment)
            }
        }
    }


    private fun createSignInIntent() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setLogo(R.drawable.logo) // Set logo drawable
            .setTheme(R.style.AuthUITheme) // Set theme
            .build()
        signInLauncher.launch(signInIntent)
    }


    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == Activity.RESULT_OK) {

            val user = FirebaseAuth.getInstance().currentUser

            if (user != null && user.uid.isNotEmpty()) {
                App.preferences.uid = user.uid

                if (response!!.isNewUser) {
                    onSignUpSuccess()
                    initNewUserData(user.uid)
                } else {
                    App.preferences.isInterestsInitialized = true
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

    private fun initNewUserData(userId: String) {
// interests // diary // got achievments // settings
// select interests

        EventBus.getDefault()
            .post(
                InitUserSettingsEvent(
                    userId
                )
            )

        Navigator.splashToWelcome(this@SplashFragment)
    }

    private fun onSignUpSuccess() {
        SimpleCustomSnackbar.make(
            binding.coordinator,
            "Вы успешно зарегистрировались",
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
            "Вы успешно авторизовались",
            Snackbar.LENGTH_SHORT,
            null,
            null,
            null,
            null,
            R.drawable.snack_success_gradient,
        )?.show()
    }

    private fun onAuthFailed() {
        SimpleCustomSnackbar.make(
            binding.coordinator,
            "Ошибка авторизации",
            Snackbar.LENGTH_SHORT,
            null,
            null,
            null,
            null,
            R.drawable.snack_warning_gradient,
        )?.show()
    }

    inner class Handler
}