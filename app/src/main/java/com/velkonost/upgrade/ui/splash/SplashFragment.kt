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
import com.velkonost.upgrade.navigation.Navigator
import com.velkonost.upgrade.ui.base.BaseFragment
import com.velkonost.upgrade.ui.view.SimpleCustomSnackbar

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
                goNext()
            } else {
              onAuthFailed()
            }
        } else {
            onAuthFailed()
        }
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

    inner class Handler {

    }
}