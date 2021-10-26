package com.velkonost.upgrade.util

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

class ProgressDelegate (
    private val fragmentManager: FragmentManager
) {

    fun showProgress() {
        (fragmentManager.findFragmentByTag("progress_dialog") as? DialogFragment)?.dismiss()
        ProgressDialog.newInstance().show(fragmentManager, "progress_dialog")
    }

    fun hideProgress() {
        (fragmentManager.findFragmentByTag("progress_dialog") as? DialogFragment)?.dismiss()
    }

}

fun lazyProgressDelegate(fragmentManager: () -> FragmentManager) =
    lazy(LazyThreadSafetyMode.NONE) { ProgressDelegate(fragmentManager()) }