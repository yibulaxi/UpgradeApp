package com.velkonost.upgrade.ui.activity.main

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.jaeger.library.StatusBarUtil
import com.velkonost.upgrade.App
import com.velkonost.upgrade.BuildConfig
import com.velkonost.upgrade.R
import com.velkonost.upgrade.databinding.ActivityMainBinding
import com.velkonost.upgrade.event.*
import com.velkonost.upgrade.model.Interest
import com.velkonost.upgrade.navigation.Navigator
import com.velkonost.upgrade.ui.HomeViewModel
import com.velkonost.upgrade.ui.base.BaseActivity
import org.greenrobot.eventbus.Subscribe
import timber.log.Timber

class MainActivity : BaseActivity<HomeViewModel, ActivityMainBinding>(
    R.layout.activity_main,
    HomeViewModel::class,
    Handler::class
) {
    private var navController: NavController? = null

    private lateinit var cloudFirestoreDatabase: FirebaseFirestore
    private val firebaseDatabase = Firebase.database(BuildConfig.FIREBASE_DATABASE)
    private var isFirebaseAvailable: Boolean = false

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)

        StatusBarUtil.setTransparent(this)

        navController = findNavController(R.id.nav_host_fragment)
        binding.navView.setupWithNavController(navController!!)

        lockDeviceRotation(true)

        binding.navView.setOnNavigationItemReselectedListener {
            if (binding.navView.selectedItemId == it.itemId) {
                val navGraph = Navigation.findNavController(this, R.id.nav_host_fragment)
                navGraph.popBackStack(it.itemId, false)
                return@setOnNavigationItemReselectedListener
            }
        }

        subscribePushTopic()
    }

    @Subscribe
    fun onLoadMainEvent(e: LoadMainEvent) {
        getInterests { Navigator.splashToMetric(e.f)}

    }

    private fun subscribePushTopic() {
        try {
            val topic =
                if (BuildConfig.DEBUG) "general_dev" else "general_prom"
            FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Timber.d("Subscribe to topic failed.")
                    } else {
                        Timber.d("Subscribe to topic completed.")
                    }
                }
        } catch (e: java.lang.Exception) {
            isFirebaseAvailable = false
        }

        try {
            cloudFirestoreDatabase = Firebase.firestore
        } catch (e: java.lang.Exception) {
            isFirebaseAvailable = false
        }
    }

    private fun setupNavMenu() {
        binding.navView.inflateMenu(R.menu.bottom_nav_menu)
        binding.navView.isVisible = true
    }

    private fun lockDeviceRotation(value: Boolean) {
        requestedOrientation = if (value) {
            val currentOrientation = resources.configuration.orientation
            if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
            } else {
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
            }
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            ActivityInfo.SCREEN_ORIENTATION_FULL_USER
        }
    }

    @Subscribe
    fun onChangeTabEvent(e: ChangeTabEvent) {
        binding.navView.selectedItemId = e.itemId
    }

    @Subscribe
    fun onInitUserSettingsEvent(e: InitUserSettingsEvent) {

        val userSettings = hashMapOf(
            "is_push_available" to true,
            "difficulty" to 1,
            "is_interests_initialized" to false
        )

        cloudFirestoreDatabase
            .collection("users_settings").document(e.userId)
            .set(userSettings)
            .addOnSuccessListener {

            }
            .addOnFailureListener {

            }
    }

    @Subscribe
    fun onUpdateUserInterestEvent(e: UpdateUserInterestEvent) {
        cloudFirestoreDatabase
            .collection("users_interests").document(App.preferences.uid!!)
            .get()
            .addOnSuccessListener {
                val interestPrevAmount = (it.get(e.interest.id.toString())).toString().toDouble()
                val interestNewAmount = interestPrevAmount + e.amount

                setInterestAmount(e.interest, interestNewAmount)
            }
            .addOnFailureListener {

            }
    }

    @Subscribe
    fun onInitUserInterestsEvent(e: InitUserInterestsEvent) {
        cloudFirestoreDatabase
            .collection("users_interests").document(App.preferences.uid!!)
            .set(e.data)
            .addOnSuccessListener {
                App.preferences.isInterestsInitialized = true
                cloudFirestoreDatabase
                    .collection("users_settings").document(App.preferences.uid!!)
                    .update(mapOf("is_interests_initialized" to true))
                    .addOnSuccessListener {
                        Navigator.welcomeToMetric(e.f)
                    }
            }
            .addOnFailureListener {

            }
    }

    private fun setInterestAmount(interest: Interest, amount: Double) {
        val data = hashMapOf(
            interest.id.toString() to amount
        )

        cloudFirestoreDatabase
            .collection("users_interests").document(App.preferences.uid!!)
            .set(data)
            .addOnSuccessListener {

            }
            .addOnFailureListener {

            }
    }

    private fun getInterests(f: () -> Unit) {
        cloudFirestoreDatabase.collection("users_interests").document(App.preferences.uid!!)
            .get()
            .addOnSuccessListener {
                viewModel.setInterests(it).run {
                    f()
                    setupNavMenu()
                }

            }
            .addOnFailureListener {

            }
    }

    inner class Handler
}