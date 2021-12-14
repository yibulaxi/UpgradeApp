package ru.get.better.ui.auth

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
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

                    // Sign in success, update UI with the signed-in user's information
//                    Log.d(TAG, "signInWithEmail:success")
//                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("keke", "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        requireContext(), "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
//                    updateUI(null)
                }
            }
    }

    private fun register(email: String, password: String, repeatPassword: String) {
        if (password != repeatPassword) {
            EventBus.getDefault().post(ShowFailEvent("Пароли не совпадают"))
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
                    Log.w("keke", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        requireContext(), "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
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
                    .post(ShowFailEvent("Укажите email"))
                binding.password.text.isNullOrEmpty() -> EventBus.getDefault()
                    .post(ShowFailEvent("Укажите пароль"))
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
                    .post(ShowFailEvent("Укажите email"))
                binding.passwordSignUp.text.isNullOrEmpty() -> EventBus.getDefault()
                    .post(ShowFailEvent("Укажите пароль"))
                binding.passwordSignUp.text.toString().length < 6 -> EventBus.getDefault()
                    .post(ShowFailEvent("Пароль слишком короткий"))
                binding.passwordRepeatSignUp.text.isNullOrEmpty() -> EventBus.getDefault()
                    .post(ShowFailEvent("Укажите пароль повторно"))
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