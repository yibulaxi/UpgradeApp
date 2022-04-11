package ru.get.better.ui.activity.main

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Rect
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.ConfigurationCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import com.jaeger.library.StatusBarUtil
import com.stfalcon.imageviewer.StfalconImageViewer
import com.takusemba.spotlight.Spotlight
import com.takusemba.spotlight.effet.RippleEffect
import com.takusemba.spotlight.shape.Circle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.dialog_alert_add_interest.view.*
import kotlinx.android.synthetic.main.dialog_alert_edit_interest.view.*
import kotlinx.android.synthetic.main.dialog_rate.view.*
import kotlinx.android.synthetic.main.layout_simple_custom_snackbar.*
import kotlinx.android.synthetic.main.target_menu_addpost.view.*
import kotlinx.android.synthetic.main.view_affirmation.view.*
import kotlinx.android.synthetic.main.view_post_add.*
import kotlinx.android.synthetic.main.view_select_note_type.*
import kotlinx.coroutines.*
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
import ru.get.better.push.NotificationReceiver
import ru.get.better.ui.activity.main.adapter.AddPostMediaAdapter
import ru.get.better.ui.activity.main.ext.*
import ru.get.better.ui.base.BaseActivity
import ru.get.better.ui.view.SimpleCustomSnackbar
import ru.get.better.vm.*
import java.util.*


class MainActivity : BaseActivity<BaseViewModel, ActivityMainBinding>(
    R.layout.activity_main,
    BaseViewModel::class,
    Handler::class
), PhotoPickerFragment.Callback {

    val userSettingsViewModel: UserSettingsViewModel by viewModels { viewModelFactory }
    val userInterestsViewModel: UserInterestsViewModel by viewModels { viewModelFactory }
    val userDiaryViewModel: UserDiaryViewModel by viewModels { viewModelFactory }
    val userAchievementsViewModel: UserAchievementsViewModel by viewModels { viewModelFactory }
    val affirmationsViewModel: AffirmationsViewModel by viewModels { viewModelFactory }

    private var navController: NavController? = null

    lateinit var addPostBehavior: BottomSheetBehavior<ConstraintLayout>
    lateinit var addTrackerBehavior: BottomSheetBehavior<ConstraintLayout>
    lateinit var addGoalBehavior: BottomSheetBehavior<ConstraintLayout>
    lateinit var addHabitBehavior: BottomSheetBehavior<ConstraintLayout>

    lateinit var cloudStorage: FirebaseStorage

    private var isAnySpotlightActiveNow: Boolean = false
    var selectedInterestIdToAddPost: String = ""
    var selectedDiffPointToAddPost: Int = 0
    var selectedRegularityToAddHabit: Regularity = Regularity.Daily

    lateinit var mediaAdapter: AddPostMediaAdapter
    fun isMediaAdapterInitialized() = ::mediaAdapter.isInitialized

    lateinit var activeTrackerTimer: CountDownTimer
    fun isActiveTrackerTimerInitialized() = ::activeTrackerTimer.isInitialized

    lateinit var affirmationIconUrl: String
    fun isAffirmationIconUrlInitialized() = ::affirmationIconUrl.isInitialized

    var currentSecondaryView = SecondaryViews.Empty

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)

        lifecycleScope.launch(Dispatchers.IO) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }
//            navController = findNavController(R.id.nav_host_fragment)

        }
        initStart()
        lifecycleScope.async {
            addPostBehavior =
                BottomSheetBehavior.from(binding.addPostBottomSheet.bottomSheetContainer)
            addTrackerBehavior =
                BottomSheetBehavior.from(binding.addTrackerBottomSheet.bottomSheetContainer)
            addGoalBehavior =
                BottomSheetBehavior.from(binding.addGoalBottomSheet.bottomSheetContainer)
            addHabitBehavior =
                BottomSheetBehavior.from(binding.addHabitBottomSheet.bottomSheetContainer)

            cloudStorage = FirebaseStorage.getInstance()
        }

        initNotificationReceiver()
    }

    override fun onViewModelReady(viewModel: BaseViewModel) {
        super.onViewModelReady(viewModel)

        affirmationsViewModel.loadTodayNasaData()
        userAchievementsViewModel.getAchievements()
        userInterestsViewModel.init()
        affirmationsViewModel.nasaDataViewState.observe(this, ::observeNasaData)
        userDiaryViewModel.setDiaryNoteEvent.observe(this, ::observeSetDiaryNote)
    }

    private fun initStart() {
        setupInitTheme()
        setupLocale()

        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
            navController =
                (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
            val navGraph = navController!!.navInflater.inflate(R.navigation.mobile_navigation)

            navGraph.startDestination = if (App.preferences.uid.isNullOrEmpty()) {
                App.preferences.uid = System.currentTimeMillis().toString()

                GlobalScope.launch(Dispatchers.IO) {
                    val locale = ConfigurationCompat.getLocales(resources.configuration)[0].language

                    EventBus.getDefault()
                        .post(
                            InitUserSettingsEvent(
                                userId = App.preferences.uid!!,
                                login = App.preferences.uid!!,
                                locale =
                                if (
                                    locale == "ru"
                                    || locale == "ua"
                                    || locale == "kz"
                                    || locale == "be"
                                    || locale == "uk"
                                ) "ru" else "en"
                            )
                        )
                }

                R.id.navigation_welcome
            } else {
                if (App.preferences.isInterestsInitialized) {
//                    setupNavMenu()
                    R.id.navigation_metric
                } else {
                    R.id.navigation_welcome
                }
            }
            navController!!.graph = navGraph

            if (!App.preferences.uid.isNullOrEmpty() && App.preferences.isInterestsInitialized)
                setupNavMenu()
        }
    }

    private fun setupLocale() {
        GlobalScope.launch(Dispatchers.IO) {
            if (App.preferences.isFirstLaunch || App.preferences.locale.isNullOrEmpty()) {
                App.preferences.isFirstLaunch = false

                val locale = ConfigurationCompat.getLocales(resources.configuration)[0].language
                App.preferences.locale =
                    if (locale == "ru" || locale == "ua" || locale == "kz" || locale == "be" || locale == "uk") "ru"
                    else "en"
            }

            Locale.setDefault(Locale(App.preferences.locale))
            val config = resources.configuration
            config.setLocale(Locale(App.preferences.locale))
            resources.updateConfiguration(config, resources.displayMetrics)
        }
    }

    private fun setupInitTheme() {
        GlobalScope.launch(Dispatchers.IO) {
            if (App.preferences.isFirstLaunch) {
                EventBus.getDefault().post(
                    UpdateThemeEvent(
                        when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
                            Configuration.UI_MODE_NIGHT_NO -> false
                            Configuration.UI_MODE_NIGHT_YES -> true
                            Configuration.UI_MODE_NIGHT_UNDEFINED -> false
                            else -> false
                        }
                    )
                )
            }
        }
    }

    @Subscribe
    fun onShowRateDialogEvent(e: ShowRateDialogEvent) {
        if (
            App.preferences.launchCount != 0
            && App.preferences.launchCount % App.DAYS_UNTIL_RATE == 0
            && !App.preferences.isAppRated
            && currentSecondaryView == SecondaryViews.Empty
        ) setupRateDialog()
    }

    @Subscribe
    fun onShowAffirmationEvent(e: ShowAffirmationEvent) {
        setupAffirmation(isIncreaseNumber = e.increase)
    }

    override fun updateThemeAndLocale() {
        StatusBarUtil.setColor(
            this, resources.getColor(
                if (App.preferences.isDarkTheme) R.color.colorDarkStatusBar
                else R.color.colorLightStatusBar
            )
        )

        window.navigationBarColor = resources.getColor(
            if (App.preferences.isDarkTheme) R.color.colorDarkNavigationBar
            else R.color.colorLightNavigationBar
        )

        GlobalScope.async {
            if (!App.preferences.uid.isNullOrEmpty()) {
                setupBottomSheets()
                setupSelectNoteTypeBottomSheet()
                setupAddPostBottomSheet()
                setupAddGoalBottomSheet()
                setupAddTrackerBottomSheet()
                setupTrackerSheet()
                setupAddHabitBottomSheet()
            }
        }

        GlobalScope.launch(Dispatchers.Main) {
            binding.dateTitle.text = App.resourcesProvider.getStringLocale(
                R.string.tracker_date_title,
                App.preferences.locale
            )
            binding.stopTracker.text =
                App.resourcesProvider.getStringLocale(R.string.stop_tracker, App.preferences.locale)

            val states = arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf())
            val colors = intArrayOf(
                ContextCompat.getColor(
                    this@MainActivity,
                    if (App.preferences.isDarkTheme) R.color.colorDarkBottomNavSelectorChecked
                    else R.color.colorLightBottomNavSelectorChecked
                ),
                ContextCompat.getColor(
                    this@MainActivity,
                    if (App.preferences.isDarkTheme) R.color.colorDarkBottomNavSelectorUnchecked
                    else R.color.colorLightBottomNavSelectorUnchecked
                )
            )
            binding.navView.itemIconTintList = ColorStateList(states, colors)

            binding.progressBar.background = ContextCompat.getDrawable(
                this@MainActivity,
                if (App.preferences.isDarkTheme) R.drawable.bg_progress_dark
                else R.drawable.bg_progress_light
            )

            binding.navViewContainer.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    this@MainActivity,
                    if (App.preferences.isDarkTheme) R.color.colorDarkNavViewContainerBackgroundTint
                    else R.color.colorLightNavViewContainerBackgroundTint
                )
            )

            binding.navView.setBackgroundColor(
                ContextCompat.getColor(
                    this@MainActivity,
                    if (App.preferences.isDarkTheme) R.color.colorDarkNavViewBackground
                    else R.color.colorLightNavViewBackground
                )
            )

            binding.trackerFab.setImageResource(
                if (App.preferences.isDarkTheme) R.drawable.ic_tracker_dark
                else R.drawable.ic_tracker_light
            )

            binding.trackerFab.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    this@MainActivity,
                    if (App.preferences.isDarkTheme) R.color.colorDarkTrackerFabTint
                    else R.color.colorLightTrackerFabTint
                )
            )

            binding.trackerContainerBg.setBackgroundColor(
                ContextCompat.getColor(
                    this@MainActivity,
                    if (!App.preferences.isDarkTheme) R.color.colorDarkTrackerContainerBackground
                    else R.color.colorLightTrackerContainerBackground
                )
            )

            binding.trackerContainer.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    this@MainActivity,
                    if (!App.preferences.isDarkTheme) R.color.colorDarkTrackerContainerBackground
                    else R.color.colorLightTrackerContainerBackground
                )
            )

            binding.trackerInterestName.setTextColor(
                ContextCompat.getColor(
                    this@MainActivity,
                    if (App.preferences.isDarkTheme) R.color.colorDarkTrackerInterestNameText
                    else R.color.colorLightTrackerInterestNameText
                )
            )

            binding.dateTitle.setTextColor(
                ContextCompat.getColor(
                    this@MainActivity,
                    if (App.preferences.isDarkTheme) R.color.colorDarkTrackerDateTitleText
                    else R.color.colorLightTrackerDateTitleText
                )
            )

            binding.trackerDate.setTextColor(
                ContextCompat.getColor(
                    this@MainActivity,
                    if (App.preferences.isDarkTheme) R.color.colorDarkTrackerDateText
                    else R.color.colorLightTrackerDateText
                )
            )

            binding.timer.setTextColor(
                ContextCompat.getColor(
                    this@MainActivity,
                    if (App.preferences.isDarkTheme) R.color.colorDarkTrackerTimerText
                    else R.color.colorLightTrackerTimerText
                )
            )

            binding.trackerTitle.setTextColor(
                ContextCompat.getColor(
                    this@MainActivity,
                    if (App.preferences.isDarkTheme) R.color.colorDarkTrackerTitleText
                    else R.color.colorLightTrackerTitleText
                )
            )

            binding.stopTrackerContainer.background = ContextCompat.getDrawable(
                this@MainActivity,
                if (App.preferences.isDarkTheme) R.drawable.bg_view_active_tracker_btn_dark
                else R.drawable.bg_view_active_tracker_btn_light
            )

            binding.stopTracker.setTextColor(
                ContextCompat.getColor(
                    this@MainActivity,
                    if (App.preferences.isDarkTheme) R.color.colorDarkTrackerStopText
                    else R.color.colorLightTrackerStopText
                )
            )

            binding.backgroundImage.setBackgroundColor(
                ContextCompat.getColor(
                    this@MainActivity,
                    if (App.preferences.isDarkTheme) R.color.colorDarkBlur
                    else R.color.colorLightBlur
                )
            )

            binding.progressBar.indeterminateTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    this@MainActivity,
                    if (App.preferences.isDarkTheme) R.color.colorDarkProgressBarIndeterminateTint
                    else R.color.colorLightProgressBarIndeterminateTint
                )
            )
        }
    }

    var isTrackerTimerRunning = false

    override fun onBackPressed() {
        if (addPostBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
            addPostBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        if (binding.selectNoteTypeBottomSheet.title.translationY == 0f)
            hideSelectNoteTypeView()

        if (addGoalBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
            addGoalBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        if (addTrackerBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
            addTrackerBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        if (addHabitBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
            addHabitBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        if (binding.trackerSheet.isFabExpanded)
            binding.trackerSheet.contractFab()

        EventBus.getDefault().post(BackPressedEvent(true))
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
            } else if (binding.selectNoteTypeBottomSheet.title.translationY == 0f) {
                val outRect = Rect()

                binding.selectNoteTypeBottomSheet.container.getGlobalVisibleRect(outRect)

                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    binding.selectNoteTypeBottomSheet.container.post {
                        hideSelectNoteTypeView()
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
        lifecycleScope.launch(Dispatchers.IO) {
            val notifyIntent = Intent(this@MainActivity, NotificationReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                this@MainActivity,
                2,
                notifyIntent,
                PendingIntent.FLAG_IMMUTABLE + PendingIntent.FLAG_CANCEL_CURRENT
            )
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000 * 60 * 60 * 2, (
                        1000 * 60 * 60 * 24).toLong(), pendingIntent
            )
        }
    }

    override fun onImagesPicked(photos: ArrayList<Uri>) {
        val media = arrayListOf<Media>()
        photos.map { media.add(Media(it)) }

        mediaAdapter = AddPostMediaAdapter(this, media, glideRequestManager)
        (binding.addPostBottomSheet.mediaRecycler.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.HORIZONTAL
        binding.addPostBottomSheet.mediaRecycler.adapter = mediaAdapter
    }

    fun uploadMedia(
        noteId: String? = null,
        text: String,
        date: String
    ) {
        EventBus.getDefault().post(ChangeProgressStateEvent(true))
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

                uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let { throw it }
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
            allowCamera = false,
            maxSelection = 5,
            theme =
            if (App.preferences.isDarkTheme) R.style.ChiliPhotoPicker_Dark
            else R.style.ChiliPhotoPicker_Light
        ).show(supportFragmentManager, "picker")
    }

    @Subscribe
    fun onLoadMainEvent(e: LoadMainEvent) =
        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
            setupNavMenu()
            Navigator.toMetric(navController!!)
        }
//        userInterestsViewModel.getInterests { Navigator.toMetric(navController!!) }

    @Subscribe
    fun onChangeNavViewVisibilityEvent(e: ChangeNavViewVisibilityEvent) {
        binding.navView.isVisible = e.isVisible
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

    private fun setupNavMenu() {
        if (binding.navView.menu.size() != 0) return

        binding.navView.setupWithNavController(navController!!)
        binding.navView.inflateMenu(R.menu.bottom_nav_menu)
        binding.navViewContainer.isVisible = true
        binding.navView.isVisible = true
        lifecycleScope.launch(Dispatchers.IO) {
            val options = NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setEnterAnim(R.anim.open_from_top)
                .setExitAnim(R.anim.activity_close_translate_to_bottom)
                .setPopEnterAnim(R.anim.open_from_top)
                .setPopExitAnim(R.anim.activity_close_translate_to_bottom)
                .setPopUpTo(navController!!.graph.startDestination, false)
                .build()

            binding.navView.menu.getItem(0).setOnMenuItemClickListener {
                navController!!.navigate(R.id.navigation_metric, null, options)
                return@setOnMenuItemClickListener true
            }

            binding.navView.menu.getItem(1).setOnMenuItemClickListener {
                navController!!.navigate(R.id.navigation_diary, null, options)
                return@setOnMenuItemClickListener true
            }

            binding.navView.menu.getItem(2).setOnMenuItemClickListener {
                showSelectNoteTypeView()
                return@setOnMenuItemClickListener true
            }

            binding.navView.menu.getItem(3).setOnMenuItemClickListener {
                navController!!.navigate(R.id.navigation_achievements, null, options)
                return@setOnMenuItemClickListener true
            }

            binding.navView.menu.getItem(4).setOnMenuItemClickListener {
                navController!!.navigate(R.id.navigation_settings, null, options)
                return@setOnMenuItemClickListener true
            }

            binding.navView.setOnNavigationItemReselectedListener {
                if (binding.navView.selectedItemId == it.itemId) {
                    val navGraph =
                        Navigation.findNavController(this@MainActivity, R.id.nav_host_fragment)
                    navGraph.popBackStack(it.itemId, false)

                    return@setOnNavigationItemReselectedListener
                }
            }
        }

        GlobalScope.async {
            Log.d("keke", "setup1")
            setupBottomSheets()
            setupSelectNoteTypeBottomSheet()
            setupAddPostBottomSheet()
            setupAddGoalBottomSheet()
            setupAddTrackerBottomSheet()
            setupTrackerSheet()
            setupAddHabitBottomSheet()
        }

        android.os.Handler().postDelayed({
            showSpotlights()
        }, 1500)

    }

    private fun showSpotlights() {
        if (!isAnySpotlightActiveNow) {
            if (
                !App.preferences.isMetricWheelSpotlightShown
                && currentSecondaryView == SecondaryViews.Empty
            ) {
                EventBus.getDefault().post(ShowSpotlightEvent(SpotlightType.MetricWheel))
            } else if (
                !App.preferences.isMainAddPostSpotlightShown
                && currentSecondaryView == SecondaryViews.Empty
            ) {
                showAddPostSpotlight()
            } else if (
                !App.preferences.isDiaryHabitsSpotlightShown
                && currentSecondaryView == SecondaryViews.Empty
            ) {
                EventBus.getDefault().post(ShowSpotlightEvent(SpotlightType.DiaryHabits))
            }
        }
    }

    @Subscribe
    fun onEditDiaryNoteEvent(e: EditDiaryNoteEvent) {
        if (e.note.noteType == NoteType.Note.id) {
            addPostBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            binding.addPostBottomSheet.editText.requestFocus()

            with(binding.addPostBottomSheet) {
                noteId = e.note.diaryNoteId

                editText.setRichTextEditing(true, e.note.text)
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

                val urls = arrayListOf<Media>()
                for (url in e.note.media ?: arrayListOf()) {
                    urls.add(Media(url = url))
                }
                mediaAdapter = AddPostMediaAdapter(this@MainActivity, urls, glideRequestManager)
                mediaRecycler.adapter = mediaAdapter
            }
        } else if (e.note.noteType == NoteType.Goal.id) {
            addGoalBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            binding.addGoalBottomSheet.editText.requestFocus()

            with(binding.addGoalBottomSheet) {
                noteId = e.note.diaryNoteId

                editText.setRichTextEditing(true, e.note.text)
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
            }
        } else if (e.note.noteType == NoteType.Tracker.id) {
            addTrackerBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            binding.addTrackerBottomSheet.editText.requestFocus()

            with(binding.addTrackerBottomSheet) {
                noteId = e.note.diaryNoteId

                editText.setRichTextEditing(true, e.note.text)
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
            }
        } else if (e.note.noteType == NoteType.HabitRealization.id) {
            addHabitBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            binding.addHabitBottomSheet.editText.requestFocus()

            with(binding.addHabitBottomSheet) {
                noteId = e.note.diaryNoteId

                editText.setRichTextEditing(true, e.note.text)
                editText.setSelection(editText.length())

                editAmount.setText(e.note.initialAmount.toString())

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
                        } else {
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
                        } else {
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

        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
            val noteInterest =
                userInterestsViewModel.getInterestById(selectedInterestIdToAddPost)

            if (noteInterest != null) {
                userDiaryViewModel.setNote(
                    DiaryNote(
                        diaryNoteId = (noteId ?: System.currentTimeMillis()).toString(),
                        noteType = noteType,
                        text = text,
                        date = date,
                        media = mediaUrls,
                        changeOfPoints = selectedDiffPointToAddPost,
                        interest = DiaryNoteInterest(
                            interestId = noteInterest.interestId,
                            interestName = noteInterest.name!!,
                            interestIcon = noteInterest.logoId.toString()
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
        }

    }

    @Subscribe
    fun onOpenFullScreenMediaEvent(e: OpenFullScreenMediaEvent) {
        StfalconImageViewer.Builder<Media>(this, e.media) { view, image ->
            if (image?.url != null) {
                glideRequestManager.load(image.url).into(view)
            }
        }.withBackgroundColor(
            ContextCompat.getColor(
                this,
                if (App.preferences.isDarkTheme) R.color.colorDarkFullScreenMediaBackground
                else R.color.colorLightFullScreenMediaBackground
            )
        )
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

    @Subscribe
    fun onSecondaryViewUpdateStateEvent(e: SecondaryViewUpdateStateEvent) {
        currentSecondaryView = e.newState
    }

    private fun showAddPostSpotlight() {
        val addPostTargetLayout =
            layoutInflater.inflate(R.layout.target_menu_addpost, FrameLayout(this))

        addPostTargetLayout.title.text =
            App.resourcesProvider.getStringLocale(R.string.addpost_spotlight)

        addPostTargetLayout.title.setTextColor(
            ContextCompat.getColor(
                this,
                if (App.preferences.isDarkTheme) R.color.colorDarkTargetMenuAddpostTitleText
                else R.color.colorLightTargetMenuAddpostTitleText
            )
        )
        addPostTargetLayout.icArrow.imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                this,
                if (App.preferences.isDarkTheme) R.color.colorDarkTargetMenuAddpostIcArrowTint
                else R.color.colorLightTargetMenuAddpostIcArrowTint
            )
        )

        val addPostTarget = com.takusemba.spotlight.Target.Builder()
            .setAnchor(binding.targetAddPost)
            .setShape(Circle(16f))
            .setEffect(
                RippleEffect(
                    100f,
                    200f,
                    ContextCompat.getColor(
                        this,
                        if (App.preferences.isDarkTheme) R.color.colorDarkSpotlightTarget
                        else R.color.colorLightSpotlightTarget
                    )
                )
            )
            .setOverlay(addPostTargetLayout)
            .build()

        val spotlight = Spotlight.Builder(this)
            .setTargets(addPostTarget)
            .setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    if (App.preferences.isDarkTheme) R.color.colorDarkSpotlightBackground
                    else R.color.colorLightSpotlightBackground
                )
            )
            .setDuration(1000L)
            .setAnimation(DecelerateInterpolator(2f))
            .setContainer(binding.container)
            .build()
        spotlight.start()

        currentSecondaryView = SecondaryViews.AddPostSpotlight
        isAnySpotlightActiveNow = true

        addPostTargetLayout.findViewById<ConstraintLayout>(R.id.container).setOnClickListener {
            spotlight.finish()
            currentSecondaryView = SecondaryViews.Empty

            App.preferences.isMainAddPostSpotlightShown = true
            isAnySpotlightActiveNow = false

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
        }

        override fun doInBackground(vararg params: Void?): Int? {
            return null
        }

        override fun onPostExecute(result: Int?) {
            super.onPostExecute(result)
        }
    }
}