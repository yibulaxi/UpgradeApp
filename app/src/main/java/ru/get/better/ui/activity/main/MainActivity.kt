package ru.get.better.ui.activity.main

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Rect
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.auth.AuthUI
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import com.jaeger.library.StatusBarUtil
import com.squareup.picasso.Picasso
import com.stfalcon.imageviewer.StfalconImageViewer
import com.takusemba.spotlight.Spotlight
import com.takusemba.spotlight.effet.RippleEffect
import com.takusemba.spotlight.shape.Circle
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.item_adapter_pager_notes.*
import kotlinx.android.synthetic.main.layout_simple_custom_snackbar.*
import kotlinx.android.synthetic.main.view_post_add.*
import kotlinx.android.synthetic.main.view_select_note_type.*
import lv.chi.photopicker.PhotoPickerFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import ru.get.better.App
import ru.get.better.App.Companion.READ_EXTERNAL_STORAGE_REQUEST_CODE
import ru.get.better.R
import ru.get.better.databinding.ActivityMainBinding
import ru.get.better.event.*
import ru.get.better.model.*
import ru.get.better.navigation.Navigator
import ru.get.better.rest.UserSettingsFields
import ru.get.better.ui.activity.main.adapter.AddPostMediaAdapter
import ru.get.better.ui.activity.main.ext.*
import ru.get.better.ui.base.BaseActivity
import ru.get.better.ui.view.SimpleCustomSnackbar
import ru.get.better.util.ext.observeOnce
import ru.get.better.vm.*
import java.util.*
import android.app.AlarmManager

import android.app.PendingIntent
import android.content.Context

import android.content.Intent
import android.content.res.ColorStateList
import androidx.core.view.isVisible
import androidx.navigation.ui.setupWithNavController
import ru.get.better.push.NotificationReceiver
import android.R.color
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.target_menu_addpost.view.*


class MainActivity : BaseActivity<BaseViewModel, ActivityMainBinding>(
    R.layout.activity_main,
    BaseViewModel::class,
    Handler::class
), PhotoPickerFragment.Callback {

    val userSettingsViewModel: UserSettingsViewModel by viewModels { viewModelFactory }
    val userInterestsViewModel: UserInterestsViewModel by viewModels { viewModelFactory }
    val userDiaryViewModel: UserDiaryViewModel by viewModels { viewModelFactory }
    val userAchievementsViewModel: UserAchievementsViewModel by viewModels { viewModelFactory }

    private var navController: NavController? = null

    val addPostBehavior: BottomSheetBehavior<ConstraintLayout> by lazy {
        BottomSheetBehavior.from(binding.addPostBottomSheet.bottomSheetContainer)
    }

    val addTrackerBehavior: BottomSheetBehavior<ConstraintLayout> by lazy {
        BottomSheetBehavior.from(binding.addTrackerBottomSheet.bottomSheetContainer)
    }

    val addGoalBehavior: BottomSheetBehavior<ConstraintLayout> by lazy {
        BottomSheetBehavior.from(binding.addGoalBottomSheet.bottomSheetContainer)
    }

    val addHabitBehavior: BottomSheetBehavior<ConstraintLayout> by lazy {
        BottomSheetBehavior.from(binding.addHabitBottomSheet.bottomSheetContainer)
    }

    val selectNoteTypeBehavior: BottomSheetBehavior<ConstraintLayout> by lazy {
        BottomSheetBehavior.from(binding.selectNoteTypeBottomSheet.bottomSheetContainer)
    }

    private var isAnySpotlightActiveNow: Boolean = false

    var selectedInterestIdToAddPost: String = ""
    var selectedDiffPointToAddPost: Int = 0
    var selectedRegularityToAddHabit: Regularity = Regularity.Daily

    private lateinit var cloudStorage: FirebaseStorage
    private var isFirebaseAvailable: Boolean = false

    lateinit var mediaAdapter: AddPostMediaAdapter
    fun isMediaAdapterInitialized() = ::mediaAdapter.isInitialized

    lateinit var activeTrackerTimer: CountDownTimer
    fun isActiveTrackerTimerInitialized() = ::activeTrackerTimer.isInitialized

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)
        StatusBarUtil.setDarkMode(this)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR

        navController = findNavController(R.id.nav_host_fragment)
        binding.navView.setupWithNavController(navController!!)

        binding.navView.setOnNavigationItemReselectedListener {
            if (binding.navView.selectedItemId == it.itemId) {
                val navGraph = Navigation.findNavController(this, R.id.nav_host_fragment)
                navGraph.popBackStack(it.itemId, false)

                return@setOnNavigationItemReselectedListener
            }
        }

//        subscribePushTopic()
//        initNotificationReceiver()
    }

    override fun updateThemeAndLocale() {
        StatusBarUtil.setColor(this, resources.getColor(
            if (App.preferences.isDarkTheme) R.color.colorDarkStatusBar
            else R.color.colorLightStatusBar
        ))

        window.navigationBarColor = resources.getColor(
            if (App.preferences.isDarkTheme) R.color.colorDarkNavigationBar
            else R.color.colorLightNavigationBar
        )

        setupBottomSheets()

        setupSelectNoteTypeBottomSheet()
        setupAddPostBottomSheet()
        setupAddGoalBottomSheet()
        setupAddTrackerBottomSheet()
        setupAddHabitBottomSheet()
        setupTrackerSheet()

        binding.dateTitle.text = App.resourcesProvider.getStringLocale(R.string.tracker_date_title, App.preferences.locale)
        binding.stopTracker.text = App.resourcesProvider.getStringLocale(R.string.stop_tracker, App.preferences.locale)

        val states = arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf())
        val colors = intArrayOf(
            ContextCompat.getColor(
                this,
                if (App.preferences.isDarkTheme) R.color.colorDarkBottomNavSelectorChecked
                else R.color.colorLightBottomNavSelectorChecked
            ),
            ContextCompat.getColor(
                this,
                if (App.preferences.isDarkTheme) R.color.colorDarkBottomNavSelectorUnchecked
                else R.color.colorLightBottomNavSelectorUnchecked
            )
        )
        binding.navView.itemIconTintList = ColorStateList(states, colors)

        binding.progressBar.background = ContextCompat.getDrawable(
            this,
            if (App.preferences.isDarkTheme) R.drawable.bg_progress_dark
            else R.drawable.bg_progress_light
        )

        binding.navViewContainer.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(this,
                if (App.preferences.isDarkTheme) R.color.colorDarkNavViewContainerBackgroundTint
                else R.color.colorLightNavViewContainerBackgroundTint
            )
        )

        binding.navView.setBackgroundColor(ContextCompat.getColor(
            this,
            if (App.preferences.isDarkTheme) R.color.colorDarkNavViewBackground
            else R.color.colorLightNavViewBackground
        ))

        binding.trackerFab.setImageResource(
            if (App.preferences.isDarkTheme) R.drawable.ic_tracker_dark
            else R.drawable.ic_tracker_light
        )

        binding.trackerFab.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
            this,
            if (App.preferences.isDarkTheme) R.color.colorDarkTrackerFabTint
            else R.color.colorLightTrackerFabTint
        ))

        binding.trackerContainer.setBackgroundColor(ContextCompat.getColor(
            this,
            if (App.preferences.isDarkTheme) R.color.colorDarkTrackerContainerBackground
            else R.color.colorLightTrackerContainerBackground
        ))

        binding.trackerContainer.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(
            this,
            if (App.preferences.isDarkTheme) R.color.colorDarkTrackerContainerBackground
            else R.color.colorLightTrackerContainerBackground
        ))

        binding.trackerInterestName.setTextColor(ContextCompat.getColor(
            this,
            if (App.preferences.isDarkTheme) R.color.colorDarkTrackerInterestNameText
            else R.color.colorLightTrackerInterestNameText
        ))

        binding.dateTitle.setTextColor(ContextCompat.getColor(
            this,
            if (App.preferences.isDarkTheme) R.color.colorDarkTrackerDateTitleText
            else R.color.colorLightTrackerDateTitleText
        ))

        binding.trackerDate.setTextColor(ContextCompat.getColor(
            this,
            if (App.preferences.isDarkTheme) R.color.colorDarkTrackerDateText
            else R.color.colorLightTrackerDateText
        ))

        binding.timer.setTextColor(ContextCompat.getColor(
            this,
            if (App.preferences.isDarkTheme) R.color.colorDarkTrackerTimerText
            else R.color.colorLightTrackerTimerText
        ))

        binding.trackerTitle.setTextColor(ContextCompat.getColor(
            this,
            if (App.preferences.isDarkTheme) R.color.colorDarkTrackerTitleText
            else R.color.colorLightTrackerTitleText
        ))

        binding.stopTrackerContainer.background = ContextCompat.getDrawable(
            this,
            if (App.preferences.isDarkTheme) R.drawable.bg_view_active_tracker_btn_dark
            else R.drawable.bg_view_active_tracker_btn_light
        )

        binding.stopTracker.setTextColor(ContextCompat.getColor(
            this,
            if (App.preferences.isDarkTheme) R.color.colorDarkTrackerStopText
            else R.color.colorLightTrackerStopText
        ))

        binding.backgroundImage.setBackgroundColor(ContextCompat.getColor(
            this,
            if (App.preferences.isDarkTheme) R.color.colorDarkBlur
            else R.color.colorLightBlur
        ))

        binding.progressBar.indeterminateTintList = ColorStateList.valueOf(ContextCompat.getColor(
            this,
            if (App.preferences.isDarkTheme) R.color.colorDarkProgressBarIndeterminateTint
            else R.color.colorLightProgressBarIndeterminateTint
        ))
    }

    var isTrackerTimerRunning = false

    override fun onViewModelReady(viewModel: BaseViewModel) {
        super.onViewModelReady(viewModel)

        viewModel.errorEvent.observe(this, ::showFail)
        viewModel.successEvent.observe(this, ::showSuccess)
        userInterestsViewModel.setupNavMenuEvent.observe(this, ::setupNavMenu)

        userDiaryViewModel.setDiaryNoteEvent.observe(this, ::observeSetDiaryNote)

        userSettingsViewModel.setUserSettingsEvent.observe(this, ::observeUserSettings)
    }

    override fun onBackPressed() {
        if (addPostBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
            addPostBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        if (selectNoteTypeBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
            selectNoteTypeBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        if (addGoalBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
            addGoalBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        if (addTrackerBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
            addTrackerBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        if (addHabitBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
            addHabitBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        if (binding.trackerSheet.isFabExpanded)
            binding.trackerSheet.contractFab()

        EventBus.getDefault().post(BackPressedEvent(true))
//        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()

        if (isTrackerTimerRunning)
            activeTrackerTimer.cancel()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (addPostBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                val outRect = Rect()

                binding.addPostBottomSheet.container.getGlobalVisibleRect(outRect)

                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    binding.addPostBottomSheet.container.post {
                        addPostBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    }
                    return false
                }
            } else if (selectNoteTypeBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                val outRect = Rect()

                binding.selectNoteTypeBottomSheet.container.getGlobalVisibleRect(outRect)

                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    binding.selectNoteTypeBottomSheet.container.post {
                        selectNoteTypeBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    }
                    return false
                }
            } else if (addGoalBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                val outRect = Rect()

                binding.addGoalBottomSheet.container.getGlobalVisibleRect(outRect)

                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    binding.addGoalBottomSheet.container.post {
                        addGoalBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    }
                    return false
                }
            } else if (addTrackerBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                val outRect = Rect()

                binding.addTrackerBottomSheet.container.getGlobalVisibleRect(outRect)

                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    binding.addTrackerBottomSheet.container.post {
                        addTrackerBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    }
                    return false
                }
            } else if (addHabitBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                val outRect = Rect()

                binding.addHabitBottomSheet.container.getGlobalVisibleRect(outRect)

                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    binding.addHabitBottomSheet.container.post {
                        addHabitBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    }
                    return false
                }
            }

            if (binding.trackerSheet.isFabExpanded) {
                val outRect = Rect()

                binding.trackerContainer.getGlobalVisibleRect(outRect)

                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    binding.trackerSheet.contractFab()
                    return false
                }
            }
        }

        return super.dispatchTouchEvent(event)
    }

    private fun initNotificationReceiver() {
        val notifyIntent = Intent(this, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            2,
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), (
                    1000 * 60 * 60 * 24).toLong(), pendingIntent
        )
    }

    override fun onImagesPicked(photos: ArrayList<Uri>) {
        val media = arrayListOf<Media>()
        photos.map { media.add(Media(it)) }

        mediaAdapter = AddPostMediaAdapter(this, media)
        (binding.addPostBottomSheet.mediaRecycler.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.HORIZONTAL
        binding.addPostBottomSheet.mediaRecycler.adapter = mediaAdapter
    }

    fun uploadMedia(
        noteId: String? = null,
        text: String,
        date: String
    ) {
        val storageRef = cloudStorage.reference
        val uploadedUrls = arrayListOf<String>()

        for (media in mediaAdapter.getMedia()) {
            if (media.uri == null) {
                uploadedUrls.add(media.url!!)

                if (uploadedUrls.size == mediaAdapter.getMedia().size) {
                    setDiaryNote(
                        noteId = noteId,
                        noteType = NoteType.Note.id,
                        mediaUrls = uploadedUrls,
                        text = text,
                        date = date
                    )
                } else continue
            } else {

                val ext = media.uri.toString().substring(media.uri.toString().lastIndexOf(".") + 1)
                val mediaRef = storageRef.child(
                    "notes_media/" + App.preferences.uid!!.toString() + System.currentTimeMillis()
                        .toString() + "." + ext
                )

                val file = media.uri
                val uploadTask = mediaRef.putFile(file)

                val urlTask = uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    mediaRef.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        uploadedUrls.add(task.result.toString())

                        if (uploadedUrls.size == mediaAdapter.getMedia().size) {
                            setDiaryNote(
                                noteId = noteId,
                                noteType = NoteType.Note.id,
                                mediaUrls = uploadedUrls,
                                text = text,
                                date = date
                            )
                        }
                    } else { /* Handle failures .. */
                    }
                }
            }
        }
    }

    private fun changeProgressState(isActive: Boolean) {
        runOnUiThread {
            ProgressTask(binding.progress, this, isActive).execute()
        }
    }

    @Subscribe
    fun onChangeProgressStateEvent(e: ChangeProgressStateEvent) {
        changeProgressState(e.isActive)
    }

    fun checkPermissionForReadExternalStorage(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_EXTERNAL_STORAGE_REQUEST_CODE
            )
        }

        val result = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                showFail(getString(R.string.warning_gallery_disabled))
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun openGallery() {
        PhotoPickerFragment.newInstance(
            multiple = true,
            allowCamera = true,
            maxSelection = 5,
            theme = R.style.ChiliPhotoPicker_Light
        ).show(supportFragmentManager, "picker")
    }

    @Subscribe
    fun onLoadMainEvent(e: LoadMainEvent) {
        userDiaryViewModel.getDiary()
        userSettingsViewModel.getUserSettings()
        userInterestsViewModel.getInterests { Navigator.toMetric(navController!!) }
    }

    @Subscribe
    fun onGoAuthEvent(e: GoAuthEvent) {
        showFail(getString(R.string.go_auth))
        AuthUI.getInstance()
            .signOut(this@MainActivity)
            .addOnCompleteListener {
                App.preferences.uid = ""

                Navigator.toSplash(navController!!)
            }
    }

    @Subscribe
    fun onChangeNavViewVisibilityEvent(e: ChangeNavViewVisibilityEvent) {
        binding.navView.isVisible = e.isVisible
    }

    private fun subscribePushTopic() {
        try {
//            val topic =
//                if (BuildConfig.DEBUG) "general_dev" else "general_prom"
//            FirebaseMessaging.getInstance().subscribeToTopic(topic)
//                .addOnCompleteListener { task ->
//                    if (!task.isSuccessful) {
//                        Timber.d("Subscribe to topic failed.")
//                    } else {
//                        Timber.d("Subscribe to topic completed.")
//                    }
//                }
        } catch (e: Exception) {
            isFirebaseAvailable = false
        }

        try {
            cloudStorage = FirebaseStorage.getInstance()
        } catch (e: Exception) {
            isFirebaseAvailable = false
        }
    }

    @Subscribe
    fun onChangeIsAnySpotlightActiveNow(e: ChangeIsAnySpotlightActiveNowEvent) {
        isAnySpotlightActiveNow = e.isActive
        showSpotlights()
    }

    @Subscribe
    fun onTryShowSpotlightEvent(e: TryShowSpotlightEvent) {
        showSpotlights()
    }

    private fun setupNavMenu(msg: String) {
        if (binding.navView.menu.size() != 0) return

        binding.navView.inflateMenu(R.menu.bottom_nav_menu)

        binding.navViewContainer.isVisible = true
        binding.navView.isVisible = true

        binding.navView.menu.getItem(2).setOnMenuItemClickListener {
            selectNoteTypeBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            return@setOnMenuItemClickListener true
        }

        setupSelectNoteTypeBottomSheet()
        setupAddPostBottomSheet()
        setupAddGoalBottomSheet()
        setupAddTrackerBottomSheet()
        setupAddHabitBottomSheet()
        setupTrackerSheet()

        showSpotlights()
    }

    private fun showSpotlights() {
        if (!isAnySpotlightActiveNow) {
            userSettingsViewModel.getUserSettingsById(App.preferences.uid!!)
                .observeOnce(this) {
                    if (!App.preferences.isMetricWheelSpotlightShown) {
                        EventBus.getDefault().post(ShowSpotlightEvent(SpotlightType.MetricWheel))
                    } else if (!App.preferences.isMainAddPostSpotlightShown) {
                        showAddPostSpotlight()
                    } else if (!App.preferences.isDiaryHabitsSpotlightShown) {
                        EventBus.getDefault().post(ShowSpotlightEvent(SpotlightType.DiaryHabits))
                    }
                }
        }
    }

    @Subscribe
    fun onChangeTabEvent(e: ChangeTabEvent) {
        binding.navView.selectedItemId = e.itemId
    }

    @Subscribe
    fun onEditDiaryNoteEvent(e: EditDiaryNoteEvent) {
        if (e.note.noteType == NoteType.Note.id) {
            addPostBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            binding.addPostBottomSheet.editText.requestFocus()

            with(binding.addPostBottomSheet) {
                noteId = e.note.diaryNoteId

                editText.setText(e.note.text)
                editText.setSelection(editText.length())


                var selectedIndex = 0
                for (i in icon.adapter.values.indices) {
                    if (icon.adapter.values[i].id == e.note.interest!!.interestId) {
                        selectedIndex = i
                        break
                    }
                }

                icon.getRecycler().scrollToPosition(position = 5)
                icon.getRecycler().post {
                    icon.setSelectedIndex(
                        index = selectedIndex,
                        animated = true
                    )
                }

                date.text = e.note.date

                pointsStateControlGroupLight.isVisible = !App.preferences.isDarkTheme
                pointsStateControlGroupDark.isVisible = App.preferences.isDarkTheme

                when {
                    e.note.changeOfPoints.toFloat() < 0f -> {
                        pointsStateControlGroupLight.setSelectedIndex(2, true)
                        pointsStateControlGroupDark.setSelectedIndex(2, true)
                    }
                    e.note.changeOfPoints.toFloat() > 0f -> {
                        pointsStateControlGroupLight.setSelectedIndex(0, true)
                        pointsStateControlGroupDark.setSelectedIndex(0, true)
                    }
                    else -> {
                        pointsStateControlGroupLight.setSelectedIndex(1, true)
                        pointsStateControlGroupDark.setSelectedIndex(1, true)
                    }
                }

                val urls = arrayListOf<Media>()
                for (url in e.note.media ?: arrayListOf()) {
                    urls.add(Media(url = url))
                }
                mediaAdapter = AddPostMediaAdapter(this@MainActivity, urls)
                mediaRecycler.adapter = mediaAdapter
            }
        } else if (e.note.noteType == NoteType.Goal.id) {
            addGoalBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            binding.addGoalBottomSheet.editText.requestFocus()

            with(binding.addGoalBottomSheet) {
                noteId = e.note.diaryNoteId

                editText.setText(e.note.text)
                editText.setSelection(editText.length())

                var selectedIndex = 0
                for (i in icon.adapter.values.indices) {
                    if (icon.adapter.values[i].id == e.note.interest!!.interestId) {
                        selectedIndex = i
                        break
                    }
                }

                icon.getRecycler().scrollToPosition(position = 5)
                icon.getRecycler().post {
                    icon.setSelectedIndex(
                        index = selectedIndex,
                        animated = true
                    )
                }

                date.text = e.note.date

                pointsStateControlGroupLight.isVisible = !App.preferences.isDarkTheme
                pointsStateControlGroupDark.isVisible = App.preferences.isDarkTheme

                when {
                    e.note.changeOfPoints.toFloat() < 0f -> {
                        pointsStateControlGroupLight.setSelectedIndex(2, true)
                        pointsStateControlGroupDark.setSelectedIndex(2, true)
                    }
                    e.note.changeOfPoints.toFloat() > 0f -> {
                        pointsStateControlGroupLight.setSelectedIndex(0, true)
                        pointsStateControlGroupDark.setSelectedIndex(0, true)
                    }
                    else -> {
                        pointsStateControlGroupLight.setSelectedIndex(1, true)
                        pointsStateControlGroupDark.setSelectedIndex(1, true)
                    }
                }
            }
        } else if (e.note.noteType == NoteType.Tracker.id) {
            addTrackerBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            binding.addTrackerBottomSheet.editText.requestFocus()

            with(binding.addTrackerBottomSheet) {
                noteId = e.note.diaryNoteId

                editText.setText(e.note.text)
                editText.setSelection(editText.length())

                var selectedIndex = 0
                for (i in icon.adapter.values.indices) {
                    if (icon.adapter.values[i].id == e.note.interest!!.interestId) {
                        selectedIndex = i
                        break
                    }
                }

                icon.getRecycler().scrollToPosition(position = 5)
                icon.getRecycler().post {
                    icon.setSelectedIndex(
                        index = selectedIndex,
                        animated = true
                    )
                }

                date.text = e.note.date

                pointsStateControlGroupLight.isVisible = !App.preferences.isDarkTheme
                pointsStateControlGroupDark.isVisible = App.preferences.isDarkTheme

                when {
                    e.note.changeOfPoints.toFloat() < 0f -> {
                        pointsStateControlGroupLight.setSelectedIndex(2, true)
                        pointsStateControlGroupDark.setSelectedIndex(2, true)
                    }
                    e.note.changeOfPoints.toFloat() > 0f -> {
                        pointsStateControlGroupLight.setSelectedIndex(0, true)
                        pointsStateControlGroupDark.setSelectedIndex(0, true)
                    }
                    else -> {
                        pointsStateControlGroupLight.setSelectedIndex(1, true)
                        pointsStateControlGroupDark.setSelectedIndex(1, true)
                    }
                }
            }
        } else if (e.note.noteType == NoteType.HabitRealization.id) {
            addHabitBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            binding.addHabitBottomSheet.editText.requestFocus()

            with(binding.addHabitBottomSheet) {
                noteId = e.note.diaryNoteId

                editText.setText(e.note.text)
                editText.setSelection(editText.length())

                editAmount.setText(e.note.initialAmount.toString())

                pointsStateControlGroupLight.isVisible = !App.preferences.isDarkTheme
                pointsStateControlGroupDark.isVisible = App.preferences.isDarkTheme

                regularityControlGroupLight.isVisible = !App.preferences.isDarkTheme
                regularityControlGroupDark.isVisible = App.preferences.isDarkTheme

                regularityControlGroupLight.setSelectedIndex(
                    index = when (e.note.regularity) {
                        Regularity.Daily.id -> 0
                        else -> 1
                    },
                    shouldAnimate = true
                )

                regularityControlGroupDark.setSelectedIndex(
                    index = when (e.note.regularity) {
                        Regularity.Daily.id -> 0
                        else -> 1
                    },
                    shouldAnimate = true
                )

                dailyPointLight.setTextColor(
                    ContextCompat.getColor(
                        this@MainActivity,
                        if (e.note.regularity == Regularity.Daily.id) {
                            if (App.preferences.isDarkTheme) R.color.colorDarkAddHabitRegularityPointActiveText
                            else R.color.colorLightAddHabitRegularityPointActiveText
                        } else {
                            if (App.preferences.isDarkTheme) R.color.colorDarkAddHabitRegularityPointInactiveText
                            else R.color.colorLightAddHabitRegularityPointInactiveText
                        }
                    )
                )

                dailyPointDark.setTextColor(
                    ContextCompat.getColor(
                        this@MainActivity,
                        if (e.note.regularity == Regularity.Daily.id) {
                            if (App.preferences.isDarkTheme) R.color.colorDarkAddHabitRegularityPointActiveText
                            else R.color.colorLightAddHabitRegularityPointActiveText
                        } else {
                            if (App.preferences.isDarkTheme) R.color.colorDarkAddHabitRegularityPointInactiveText
                            else R.color.colorLightAddHabitRegularityPointInactiveText
                        }
                    )
                )

                weeklyPointLight.setTextColor(
                    ContextCompat.getColor(
                        this@MainActivity,
                        if (e.note.regularity == Regularity.Weekly.id) {
                            if (App.preferences.isDarkTheme) R.color.colorDarkAddHabitRegularityPointActiveText
                            else R.color.colorLightAddHabitRegularityPointActiveText
                        }
                        else {
                            if (App.preferences.isDarkTheme) R.color.colorDarkAddHabitRegularityPointInactiveText
                            else R.color.colorLightAddHabitRegularityPointInactiveText
                        }
                    )
                )

                weeklyPointDark.setTextColor(
                    ContextCompat.getColor(
                        this@MainActivity,
                        if (e.note.regularity == Regularity.Weekly.id) {
                            if (App.preferences.isDarkTheme) R.color.colorDarkAddHabitRegularityPointActiveText
                            else R.color.colorLightAddHabitRegularityPointActiveText
                        }
                        else {
                            if (App.preferences.isDarkTheme) R.color.colorDarkAddHabitRegularityPointInactiveText
                            else R.color.colorLightAddHabitRegularityPointInactiveText
                        }
                    )
                )

                var selectedIndex = 0
                for (i in icon.adapter.values.indices) {
                    if (icon.adapter.values[i].id == e.note.interest!!.interestId) {
                        selectedIndex = i
                        break
                    }
                }

                icon.getRecycler().scrollToPosition(position = 5)
                icon.getRecycler().post {
                    icon.setSelectedIndex(
                        index = selectedIndex,
                        animated = true
                    )
                }

                date.text = e.note.datetimeStart

                when {
                    e.note.changeOfPoints.toFloat() < 0f -> {
                        pointsStateControlGroupLight.setSelectedIndex(2, true)
                        pointsStateControlGroupDark.setSelectedIndex(2, true)
                    }
                    e.note.changeOfPoints.toFloat() > 0f -> {
                        pointsStateControlGroupLight.setSelectedIndex(0, true)
                        pointsStateControlGroupDark.setSelectedIndex(0, true)
                    }
                    else -> {
                        pointsStateControlGroupLight.setSelectedIndex(1, true)
                        pointsStateControlGroupDark.setSelectedIndex(1, true)
                    }
                }
            }
        }
    }

    fun setDiaryNote(
        noteId: String? = null,
        noteType: Int,
        mediaUrls: ArrayList<String>? = arrayListOf(),
        text: String,
        date: String,
        datetimeStart: String? = null,
        datetimeEnd: String? = null,
        isActiveNow: Boolean? = null,
        initialAmount: Int? = null,
        currentAmount: Int? = null,
        regularity: Int? = null,
        isPushAvailable: Boolean = false,
        color: String? = null,
        datesCompletion: ArrayList<DiaryNoteDatesCompletion>? = arrayListOf(),
        tags: ArrayList<String>? = arrayListOf()
    ) {

        val noteInterest =
            userInterestsViewModel.getInterestById(selectedInterestIdToAddPost)
        userDiaryViewModel.setNote(
            DiaryNote(
                diaryNoteId = (noteId ?: System.currentTimeMillis()).toString(),
                noteType = noteType,
                text = text,
                date = date,
                media = mediaUrls,
                changeOfPoints = selectedDiffPointToAddPost,
                interest = DiaryNoteInterest(
                    interestId = noteInterest.id,
                    interestName = noteInterest.name!!,
                    interestIcon = noteInterest.logoId!!
                ),
                datetimeStart = datetimeStart,
                datetimeEnd = datetimeEnd,
                isActiveNow = isActiveNow,
                isPushAvailable = isPushAvailable,
                initialAmount = initialAmount,
                currentAmount = currentAmount,
                regularity = regularity,
                color = color,
                datesCompletion = datesCompletion,
                tags = tags
            ),
        )
    }

    @Subscribe
    fun onOpenFullScreenMediaEvent(e: OpenFullScreenMediaEvent) {
        StfalconImageViewer.Builder<Media>(this, e.media) { view, image ->
            if (image?.url != null)
                Picasso.with(this@MainActivity).load(image.url).into(view)
        }.withBackgroundColor(ContextCompat.getColor(this,
            if (App.preferences.isDarkTheme) R.color.colorDarkFullScreenMediaBackground
            else R.color.colorLightFullScreenMediaBackground
        ))
            .withTransitionFrom(e.imageView).show().setCurrentPosition(e.position)
    }

    @Subscribe
    fun onShowSuccessEvent(e: ShowSuccessEvent) {
        showSuccess(e.msg)
    }

    @Subscribe
    fun onShowFailEvent(e: ShowFailEvent) {
        showFail(e.msg)
    }

//    @Subscribe
//    fun onUpdateThemeEvent(e: UpdateThemeEvent) {
//        setAppTheme(e.isDarkTheme)
//    }

    private fun showAddPostSpotlight() {
        val addPostTargetLayout =
            layoutInflater.inflate(R.layout.target_menu_addpost, FrameLayout(this))

        addPostTargetLayout.title.text = App.resourcesProvider.getStringLocale(R.string.addpost_spotlight)

        addPostTargetLayout.title.setTextColor(ContextCompat.getColor(
            this,
            if (App.preferences.isDarkTheme) R.color.colorDarkTargetMenuAddpostTitleText
            else R.color.colorLightTargetMenuAddpostTitleText
        ))
        addPostTargetLayout.icArrow.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
            this,
            if (App.preferences.isDarkTheme) R.color.colorDarkTargetMenuAddpostIcArrowTint
            else R.color.colorLightTargetMenuAddpostIcArrowTint
        ))

        val addPostTarget = com.takusemba.spotlight.Target.Builder()
            .setAnchor(binding.targetAddPost)
            .setShape(Circle(16f))
            .setEffect(
                RippleEffect(
                    100f,
                    200f,
                    ContextCompat.getColor(this,
                        if (App.preferences.isDarkTheme) R.color.colorDarkSpotlightTarget
                        else R.color.colorLightSpotlightTarget
                    )
                )
            )
            .setOverlay(addPostTargetLayout)
            .build()

        val spotlight = Spotlight.Builder(this)
            .setTargets(addPostTarget)
            .setBackgroundColor(ContextCompat.getColor(this,
                if (App.preferences.isDarkTheme) R.color.colorDarkSpotlightBackground
                else R.color.colorLightSpotlightBackground
            ))
            .setDuration(1000L)
            .setAnimation(DecelerateInterpolator(2f))
            .setContainer(binding.container)
            .build()
        spotlight.start()
        isAnySpotlightActiveNow = true

        addPostTargetLayout.findViewById<ConstraintLayout>(R.id.container).setOnClickListener {
            spotlight.finish()

            App.preferences.isMainAddPostSpotlightShown = true
            isAnySpotlightActiveNow = false
            userSettingsViewModel.updateField(UserSettingsFields.IsMainAddPostSpotlightShown, true)
        }
    }

    fun showSuccess(msg: String) {
        kotlin.runCatching {
            SimpleCustomSnackbar.make(
                binding.coordinator,
                msg,
                Snackbar.LENGTH_SHORT,
                null,
                null,
                R.drawable.ic_check,
                null,
                R.drawable.snack_success_gradient_light,
                R.drawable.snack_success_gradient_light,
            )?.show()
        }
    }

    fun showFail(msg: String) {
        kotlin.runCatching {
            SimpleCustomSnackbar.make(
                binding.coordinator,
                msg,
                Snackbar.LENGTH_SHORT,
                null,
                null,
                R.drawable.ic_close,
                null,
                R.drawable.snack_warning_gradient_light,
            )?.show()
        }
    }

    inner class Handler

    internal class ProgressTask(
        var progress: ConstraintLayout,
        var context: Activity,
        var isActive: Boolean
    ) : AsyncTask<Void, Int, Int>() {
        override fun onPreExecute() {
            super.onPreExecute()
            if (isActive)
                progress.visibility = View.VISIBLE
            else progress.visibility = View.GONE

        }

        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)

//            progress.setProgress(values[0]!!)
        }

        override fun doInBackground(vararg params: Void?): Int? {
//            for (i in 0 until max) {
//                doHeavyStuff()
//                publishProgress(i)
//            }
            return null
        }

        override fun onPostExecute(result: Int?) {
            super.onPostExecute(result)
//            progress.visibility = View.GONE
//            Toast.makeText(context, "Finished!", Toast.LENGTH_SHORT).show()
        }
    }
}