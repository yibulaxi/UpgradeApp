package com.velkonost.upgrade.ui.auth

import android.os.Bundle
import androidx.core.content.ContextCompat
import com.firebase.ui.auth.AuthUI
import com.jaeger.library.StatusBarUtil
import com.velkonost.upgrade.R
import com.velkonost.upgrade.databinding.FragmentSplashBinding
import com.velkonost.upgrade.ui.base.BaseFragment

class AuthFragment : BaseFragment<AuthViewModel, FragmentSplashBinding>(
    R.layout.fragment_auth,
    AuthViewModel::class,
    Handler::class
) {


    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)
        StatusBarUtil.setColor(
            requireActivity(),
            ContextCompat.getColor(requireContext(), R.color.colorWhite),
            0
        )
        StatusBarUtil.setLightMode(requireActivity())


    }

    // delete account
    private fun delete() {
        // [START auth_fui_delete]
        AuthUI.getInstance()
            .delete(context!!)
            .addOnCompleteListener {
                // ...
            }
        // [END auth_fui_delete]
    }

    inner class Handler

}