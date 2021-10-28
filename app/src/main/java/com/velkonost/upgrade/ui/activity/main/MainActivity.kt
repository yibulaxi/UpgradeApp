package com.velkonost.upgrade.ui.activity.main

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
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
import com.velkonost.upgrade.ui.view.CustomWheelPickerView
import com.velkonost.upgrade.ui.view.SimpleCustomSnackbar
import com.velkonost.upgrade.util.ext.toast
import kotlinx.android.synthetic.main.layout_simple_custom_snackbar.*
import kotlinx.android.synthetic.main.view_post_add.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import sh.tyy.wheelpicker.core.BaseWheelPickerView
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : BaseActivity<HomeViewModel, ActivityMainBinding>(
    R.layout.activity_main,
    HomeViewModel::class,
    Handler::class
) {
    private var navController: NavController? = null

    private val addPostBehavior: BottomSheetBehavior<ConstraintLayout> by lazy {
        BottomSheetBehavior.from(binding.addPostBottomSheet.bottomSheetContainer)
    }

    private var selectedInterestIdToAddPost: String = ""
    private var selectedDiffPointToAddPost: Int = 0

    private lateinit var cloudFirestoreDatabase: FirebaseFirestore
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

//        binding.navView.setOnItemSelectedListener {
//            if (it.itemId == R.id.addPostFragment) {
//                addPostBehavior.state = BottomSheetBehavior.STATE_EXPANDED
//            }
//            return@setOnItemSelectedListener true
//        }



        subscribePushTopic()

        addPostBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.backgroundImage.alpha = slideOffset
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    binding.navView.isVisible = true
                    binding.backgroundImage.isVisible = false
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    binding.navView.isVisible = false
                    binding.addPostBottomSheet.editText.requestFocus()
                }
            }
        })
    }

    private fun setupAddPostBottomSheet() {
        with(binding.addPostBottomSheet) {
            icon.adapter.values = (0 until binding.viewModel!!.getCurrentInterests().size).map {
                CustomWheelPickerView.Item(
                    binding.viewModel!!.getCurrentInterests()[it].id.toString(),

                    ContextCompat.getDrawable(
                        this@MainActivity,
                        binding.viewModel!!.getCurrentInterests()[it].logo
                    )
                )
            }
            icon.isCircular = true
            icon.isHapticFeedbackEnabled = true

            icon.setWheelListener(object : BaseWheelPickerView.WheelPickerViewListener {
                override fun didSelectItem(picker: BaseWheelPickerView, index: Int) {
                    selectedInterestIdToAddPost = icon.adapter.values.getOrNull(index)?.id.toString()
                }
            })


            val currentDate = SimpleDateFormat(
                "dd MMMM, EEEE",
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) resources.configuration.locales[0] else resources.configuration.locale
            ).format(Calendar.getInstance().timeInMillis)
            date.text = currentDate

            editText.addTextChangedListener {
                length.text = editText.text?.toString()?.length.toString() + "/2000"
            }

            pointsStateControlGroup.setOnSelectedOptionChangeCallback {
                selectedDiffPointToAddPost = it
            }

            addPost.setOnClickListener { setDiaryNote() }
        }
    }

    @Subscribe
    fun onLoadMainEvent(e: LoadMainEvent) {
        getDiary()
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

        binding.navView.menu.getItem(2).setOnMenuItemClickListener {
            addPostBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            return@setOnMenuItemClickListener true
        }
        setupAddPostBottomSheet()
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
                val interestPrevAmount = (it.get(e.interestId)).toString().toFloat()
                val interestNewAmount = String.format("%.1f", interestPrevAmount + e.amount)

                setInterestAmount(e.interestId, interestNewAmount)
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

    private fun setInterestAmount(interestId: String, amount: String) {
        val data = mutableMapOf(
            interestId to amount
        )

        cloudFirestoreDatabase
            .collection("users_interests").document(App.preferences.uid!!)
            .update(data as Map<String, Any>)
            .addOnSuccessListener {
                getInterests { EventBus.getDefault().post(UpdateMetricsEvent(true)) }
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

                    if (binding.navView.menu.size() == 0) setupNavMenu()
                }

            }
            .addOnFailureListener {

            }
    }

    private fun getDiary() {
        cloudFirestoreDatabase.collection("users_diary").document(App.preferences.uid!!)
            .get()
            .addOnSuccessListener {
//                it.data?.map { it.key to it.value }
                viewModel.setDiary(it)
            }
            .addOnFailureListener {

            }
    }

    private fun setDiaryNote() {
        with(binding.addPostBottomSheet) {
            val amount: Float = when (selectedDiffPointToAddPost) {
                0 -> 0.1f
                1 -> 0f
                else -> -0.1f
            }

            val data = hashMapOf(
                "text" to editText.text.toString(),
                "date" to date.text.toString(),
                "interest" to selectedInterestIdToAddPost,
                "amount" to String.format("%.1f", amount)

            )

            val megaData = hashMapOf(
                System.currentTimeMillis().toString() to data
            )

            cloudFirestoreDatabase.collection("users_diary").document(App.preferences.uid!!)
                .set(megaData)
                .addOnSuccessListener {
                    showSuccess("Запись добавлена")

                    EventBus.getDefault().post(UpdateUserInterestEvent(selectedInterestIdToAddPost, amount = amount))

                    addPostBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
                .addOnFailureListener {
                    showFail("Произошла ошибка")
                }
        }
    }

    private fun showSuccess(msg: String) {
        SimpleCustomSnackbar.make(
            binding.coordinator,
            msg,
            Snackbar.LENGTH_SHORT,
            null,
            null,
            null,
            null,
            R.drawable.snack_success_gradient,
            R.drawable.snack_success_gradient
        )?.show()
    }

    private fun showFail(msg: String) {
        SimpleCustomSnackbar.make(
            binding.coordinator,
            msg,
            Snackbar.LENGTH_SHORT,
            null,
            null,
            null,
            null,
            R.drawable.snack_warning_gradient,
        )?.show()
    }

    inner class Handler {

        fun onAddPostClicked(v: View) {
            setDiaryNote()
        }
    }
}