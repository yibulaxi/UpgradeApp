package ru.get.better.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import ru.get.better.R
import ru.get.better.ui.NavigationResult
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
        f.findNavController().navigate(destId, null)
    }

    fun splashToAuth(f: Fragment) {
        f.findNavController().navigate(
            R.id.action_navigation_splash_to_navigation_auth
        )
    }

    fun splashToMetric(f: Fragment) {
        f.findNavController().navigate(
            R.id.action_navigation_splash_to_navigation_metric
        )
    }

    fun splashToWelcome(f: Fragment) {
        f.findNavController().navigate(
            R.id.action_navigation_splash_to_navigation_welcome
        )
    }

    fun authToWelcome(f: Fragment) {
        f.findNavController().navigate(
            R.id.action_navigation_auth_to_navigation_welcome
        )
    }

    fun welcomeToMetric(f: Fragment) {
        f.findNavController().navigate(
            R.id.action_navigation_welcome_to_navigation_metric
        )
    }

    fun toMetric(f: Fragment) {
        f.findNavController().navigate(
            R.id.navigation_metric
        )
    }

    fun settingsToSplash(f: Fragment) {
        f.findNavController().navigate(
            R.id.action_navigation_settings_to_navigation_splash
        )
    }

    fun toSplash(fNavController: NavController) {
        fNavController.navigate(
            R.id.navigation_splash
        )
    }

    fun fromSettingsToFaq(f: Fragment) {
        f.findNavController().navigate(
            R.id.action_navigation_settings_to_navigation_faq
        )
    }

    fun toMetric(fNavController: NavController) {
        fNavController.navigate(
            R.id.navigation_metric
        )
    }
}