package ru.get.better.ui.auth

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.ConfigurationCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.greenrobot.eventbus.EventBus
import ru.get.better.App
import ru.get.better.R
import ru.get.better.databinding.FragmentAuthBinding
import ru.get.better.event.*
import ru.get.better.model.UserSettings
import ru.get.better.navigation.Navigator
import ru.get.better.ui.base.BaseFragment
import ru.get.better.util.Keyboard
import ru.get.better.vm.UserSettingsViewModel

class AuthFragment : BaseFragment<AuthViewModel, FragmentAuthBinding>(
    R.layout.fragment_auth,
    AuthViewModel::class,
    Handler::class
) {

    val userSettingsViewModel: UserSettingsViewModel by viewModels { viewModelFactory }

    private var auth: FirebaseAuth = Firebase.auth

    private lateinit var selectedAuthType: AuthType

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)

        EventBus.getDefault().post(
            ChangeNavViewVisibilityEvent(
                isVisible = false
            )
        )
    }

    override fun onViewModelReady(viewModel: AuthViewModel) {
        super.onViewModelReady(viewModel)

        userSettingsViewModel.setUserSettingsEvent.observe(this, ::observeUserSettings)
    }

    private fun login(email: String, password: String) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {

                    val user = auth.currentUser
                    App.preferences.uid = user?.uid

                    selectedAuthType = AuthType.Login
                    userSettingsViewModel.getUserSettings()

                } else {
                    Toast.makeText(
                        requireContext(), getString(R.string.authentication_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun register(email: String, password: String, repeatPassword: String) {
        if (password != repeatPassword) {
            EventBus.getDefault().post(ShowFailEvent(getString(R.string.passwords_not_match)))
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    App.preferences.uid = user?.uid

                    selectedAuthType = AuthType.Register
                    EventBus.getDefault().post(ShowSuccessEvent(getString(R.string.signup_success)))
                    initNewUserData(user!!.uid, user.email!!.substringBefore("@"))
                } else {
                    Toast.makeText(
                        requireContext(), getString(R.string.authentication_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
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

        Navigator.authToWelcome(this@AuthFragment)
    }

    private fun observeUserSettings(userSettings: UserSettings) {
        if (selectedAuthType == AuthType.Login) {

            EventBus.getDefault().post(ShowSuccessEvent(getString(R.string.signin_success)))
            if (userSettings.isInterestsInitialized!!) {
                EventBus.getDefault().post(
                    LoadMainEvent(
                        isAuthSuccess = true,
                        this@AuthFragment
                    )
                )
            } else {
                Navigator.authToWelcome(this@AuthFragment)
            }
        }
    }

    inner class Handler {
        fun signInClicked(v: View) {
            if (binding.signInBlock.alpha == 0f) return

            when {
                binding.email.text.isNullOrEmpty() -> EventBus.getDefault()
                    .post(ShowFailEvent(getString(R.string.entry_email)))
                binding.password.text.isNullOrEmpty() -> EventBus.getDefault()
                    .post(ShowFailEvent(getString(R.string.entry_passwords)))
                else -> {
                    Keyboard.hide(requireActivity())
                    login(
                        binding.email.text.toString(),
                        binding.password.text.toString()
                    )
                }
            }
        }

        fun signUpClicked(v: View) {
            if (binding.signUpBlock.alpha == 0f) return

            when {
                binding.emailSignUp.text.isNullOrEmpty() -> EventBus.getDefault()
                    .post(ShowFailEvent(getString(R.string.entry_email)))
                binding.passwordSignUp.text.isNullOrEmpty() -> EventBus.getDefault()
                    .post(ShowFailEvent(getString(R.string.entry_passwords)))
                binding.passwordSignUp.text.toString().length < 6 -> EventBus.getDefault()
                    .post(ShowFailEvent(getString(R.string.password_too_short)))
                binding.passwordRepeatSignUp.text.isNullOrEmpty() -> EventBus.getDefault()
                    .post(ShowFailEvent(getString(R.string.repeat_password)))
                else -> {
                    Keyboard.hide(requireActivity())
                    register(
                        binding.emailSignUp.text.toString(),
                        binding.passwordSignUp.text.toString(),
                        binding.passwordRepeatSignUp.text.toString()
                    )
                }
            }

        }

        fun openSignInBlock(v: View) {
            binding.signUpBlock.animate()
                .alpha(0.0f)
                .setDuration(250)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        binding.signUpBlock.isVisible = false

                        binding.signInBlock.animate()
                            .alpha(1f)
                            .setDuration(250)
                            .setListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationStart(animation: Animator?) {
                                    super.onAnimationStart(animation)
                                    binding.signInBlock.isVisible = true
                                }
                            })
                    }
                })
        }

        fun openSignUpBlock(v: View) {
            binding.signInBlock.animate()
                .alpha(0.0f)
                .setDuration(250)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        binding.signInBlock.isVisible = false

                        binding.signUpBlock.animate()
                            .alpha(1f)
                            .setDuration(250)
                            .setListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationStart(animation: Animator?) {
                                    super.onAnimationStart(animation)
                                    binding.signUpBlock.isVisible = true
                                }
                            })
                    }
                })
        }
    }

    override fun updateThemeAndLocale() {

        binding.emailTitle.text = App.resourcesProvider.getStringLocale(R.string.email)
        binding.email.hint = App.resourcesProvider.getStringLocale(R.string.email_example)

        binding.passwordTitle.text = App.resourcesProvider.getStringLocale(R.string.password)
        binding.password.hint = App.resourcesProvider.getStringLocale(R.string.password)

        binding.signInButton.text = App.resourcesProvider.getStringLocale(R.string.enter)

        binding.emailTitleSignUp.text = App.resourcesProvider.getStringLocale(R.string.email)
        binding.emailSignUp.hint = App.resourcesProvider.getStringLocale(R.string.email_example)

        binding.passwordTitleSignUp.text = App.resourcesProvider.getStringLocale(R.string.password)
        binding.passwordSignUp.hint = App.resourcesProvider.getStringLocale(R.string.password_example)

        binding.passwordRepeatTitleSignUp.text = App.resourcesProvider.getStringLocale(R.string.repeat_password)
        binding.passwordRepeatSignUp.hint = App.resourcesProvider.getStringLocale(R.string.password_example)

        binding.signUpButton.text = App.resourcesProvider.getStringLocale(R.string.register)

        binding.signInTitle.text = App.resourcesProvider.getStringLocale(R.string.enter)
        binding.signUpTitle.text = App.resourcesProvider.getStringLocale(R.string.register)

        binding.email.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_edittext_dark
            else R.drawable.bg_edittext_light
        )

        binding.password.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_edittext_dark
            else R.drawable.bg_edittext_light
        )

        binding.emailSignUp.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_edittext_dark
            else R.drawable.bg_edittext_light
        )

        binding.passwordSignUp.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_edittext_dark
            else R.drawable.bg_edittext_light
        )

        binding.passwordRepeatSignUp.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.bg_edittext_dark
            else R.drawable.bg_edittext_light
        )



        binding.authContaienr.setBackgroundColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAuthBackground
            else R.color.colorLightFragmentAuthBackground
        ))
//
//        binding.signInBlock.setBackgroundColor(ContextCompat.getColor(
//            requireContext(),
//            if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAuthSignInBlockBackground
//            else R.color.colorLightFragmentAuthSignInBlockBackground
//        ))

        binding.signInBlock.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAuthSignInBlockBackgroundTint
            else R.color.colorLightFragmentAuthSignInBlockBackgroundTint
        ))

        binding.emailTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAuthEmailTitleText
            else R.color.colorLightFragmentAuthEmailTitleText
        ))

        binding.email.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAuthEmailText
            else R.color.colorLightFragmentAuthEmailText
        ))

        binding.email.setHintTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAuthEmailHint
            else R.color.colorLightFragmentAuthEmailHint
        ))

        binding.passwordTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAuthPasswordTitleText
            else R.color.colorLightFragmentAuthPasswordTitleText
        ))

        binding.password.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAuthPasswordText
            else R.color.colorLightFragmentAuthPasswordText
        ))

        binding.password.setHintTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAuthPasswordHint
            else R.color.colorLightFragmentAuthPasswordHint
        ))

        binding.signInButton.setBackgroundColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAuthSignInButtonBackground
            else R.color.colorLightFragmentAuthSignInButtonBackground
        ))

        binding.signInButton.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAuthSignInButtonText
            else R.color.colorLightFragmentAuthSignInButtonText
        ))

//        binding.signUpBlock.setBackgroundColor(ContextCompat.getColor(
//            requireContext(),
//            if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAuthSignUpBlockBackground
//            else R.color.colorLightFragmentAuthSignUpBlockBackground
//        ))

        binding.signUpBlock.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAuthSignUpBlockBackgroundTint
            else R.color.colorLightFragmentAuthSignUpBlockBackgroundTint
        ))

        binding.emailTitleSignUp.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAuthEmailTitleSignUpText
            else R.color.colorLightFragmentAuthEmailTitleSignUpText
        ))

        binding.emailSignUp.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAuthEmailSignUpText
            else R.color.colorLightFragmentAuthEmailSignUpText
        ))

        binding.emailSignUp.setHintTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAuthEmailSignUpHint
            else R.color.colorLightFragmentAuthEmailSignUpHint
        ))

        binding.passwordTitleSignUp.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAuthPasswordTitleSignUpText
            else R.color.colorLightFragmentAuthPasswordTitleSignUpText
        ))

        binding.passwordSignUp.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAuthPasswordSignUpText
            else R.color.colorLightFragmentAuthPasswordSignUpText
        ))

        binding.passwordSignUp.setHintTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAuthPasswordSignUpHint
            else R.color.colorLightFragmentAuthPasswordSignUpHint
        ))

        binding.passwordRepeatTitleSignUp.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAuthRepeatPasswordTitleSignUpText
            else R.color.colorLightFragmentAuthRepeatPasswordTitleSignUpText
        ))

        binding.passwordRepeatSignUp.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAuthRepeatPasswordSignUpText
            else R.color.colorLightFragmentAuthRepeatPasswordSignUpText
        ))

        binding.passwordRepeatSignUp.setHintTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAuthRepeatPasswordSignUpHint
            else R.color.colorLightFragmentAuthRepeatPasswordSignUpHint
        ))

        binding.signUpButton.setBackgroundColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAuthSignUpButtonBackground
            else R.color.colorLightFragmentAuthSignUpButtonBackground
        ))

        binding.signUpButton.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAuthSignUpButtonText
            else R.color.colorLightFragmentAuthSignUpButtonText
        ))

//        binding.signIn.setBackgroundColor(ContextCompat.getColor(
//            requireContext(),
//            if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAuthSignInBackground
//            else R.color.colorLightFragmentAuthSignInBackground
//        ))

        binding.signIn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAuthSignInBackgroundTint
            else R.color.colorLightFragmentAuthSignInBackgroundTint
        ))



        binding.signInTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAuthSignInText
            else R.color.colorLightFragmentAuthSignInText
        ))

//        binding.signUp.setBackgroundColor(ContextCompat.getColor(
//            requireContext(),
//            if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAuthSignUpBackground
//            else R.color.colorLightFragmentAuthSignUpBackground
//        ))

        binding.signUp.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAuthSignUpBackgroundTint
            else R.color.colorLightFragmentAuthSignUpBackgroundTint
        ))

        binding.signUpTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.colorDarkFragmentAuthSignUpText
            else R.color.colorLightFragmentAuthSignUpText
        ))
    }


//    private fun onSignUpSuccess() {
//        SimpleCustomSnackbar.make(
//            binding.coordinator,
//            getString(R.string.signup_success),
//            Snackbar.LENGTH_SHORT,
//            null,
//            null,
//            null,
//            null,
//            R.drawable.snack_success_gradient,
//        )?.show()
//    }
//
//    private fun onSignInSuccess() {
//        SimpleCustomSnackbar.make(
//            binding.coordinator,
//            getString(R.string.signin_success),
//            Snackbar.LENGTH_SHORT,
//            null,
//            null,
//            null,
//            null,
//            R.drawable.snack_success_gradient,
//            R.drawable.snack_success_gradient,
//        )?.show()
//    }
//
//    private fun onAuthFailed() {
//        SimpleCustomSnackbar.make(
//            binding.coordinator,
//            getString(R.string.signin_error),
//            Snackbar.LENGTH_SHORT,
//            null,
//            null,
//            null,
//            null,
//            R.drawable.snack_warning_gradient,
//            R.drawable.snack_warning_gradient,
//        )?.show()
//    }

}

enum class AuthType {
    Login,
    Register
}