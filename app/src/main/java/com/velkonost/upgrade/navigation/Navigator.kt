package com.velkonost.upgrade.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import com.velkonost.upgrade.R
import com.velkonost.upgrade.ui.NavigationResult
import kotlin.properties.Delegates

object Navigator {

    fun goBack(f: Fragment) = f.findNavController().popBackStack()

    fun navigateBackWithResult(f: Fragment, result: Bundle) {
        val childFragmentManager =
            f.requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.childFragmentManager
        var backStackListener: FragmentManager.OnBackStackChangedListener by Delegates.notNull()
        backStackListener = FragmentManager.OnBackStackChangedListener {
            (childFragmentManager?.fragments?.get(0) as NavigationResult).onNavigationResult(result)
            childFragmentManager.removeOnBackStackChangedListener(backStackListener)
        }
        childFragmentManager?.addOnBackStackChangedListener(backStackListener)
        goBack(f)
    }

    fun refresh(f: Fragment) {
        val destId = f.findNavController().currentDestination!!.id

        f.findNavController().popBackStack()
//        f.findNavController().navigate(destId, lastUsedFragmentArgs)
    }

}