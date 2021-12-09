package com.velkonost.upgrade.ui.activity.main

import android.Manifest
import android.R.attr
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.net.Uri
import android.opengl.Visibility
import android.os.AsyncTask
import android.os.Bundle
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
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
import com.takusemba.spotlight.shape.RoundedRectangle
import com.velkonost.upgrade.App
import com.velkonost.upgrade.App.Companion.READ_EXTERNAL_STORAGE_REQUEST_CODE
import com.velkonost.upgrade.R
import com.velkonost.upgrade.databinding.ActivityMainBinding
import com.velkonost.upgrade.event.*
import com.velkonost.upgrade.model.*
import com.velkonost.upgrade.navigation.Navigator
import com.velkonost.upgrade.rest.UserSettingsFields
import com.velkonost.upgrade.ui.activity.main.adapter.AddPostMediaAdapter
import com.velkonost.upgrade.ui.activity.main.ext.*
import com.velkonost.upgrade.ui.base.BaseActivity
import com.velkonost.upgrade.ui.view.SimpleCustomSnackbar
import com.velkonost.upgrade.util.ext.observeOnce
import com.velkonost.upgrade.vm.BaseViewModel
import com.velkonost.upgrade.vm.UserDiaryViewModel
import com.velkonost.upgrade.vm.UserInterestsViewModel
import com.velkonost.upgrade.vm.UserSettingsViewModel
import kotlinx.android.synthetic.main.item_adapter_pager_notes.*
import kotlinx.android.synthetic.main.layout_simple_custom_snackbar.*
import kotlinx.android.synthetic.main.view_post_add.*
import kotlinx.android.synthetic.main.view_select_note_type.*
import lv.chi.photopicker.PhotoPickerFragment
import org.greenrobot.eventbus.Subscribe
import java.util.*
import kotlinx.android.synthetic.main.activity_main.view.*
import org.greenrobot.eventbus.EventBus
import java.text.SimpleDateFormat


class MainActivity : BaseActivity<BaseViewModel, ActivityMainBinding>(
    R.layout.activity_main,
    BaseViewModel::class,
    Handler::class
), PhotoPickerFragment.Callback {

    val userSettingsViewModel: UserSettingsViewModel by viewModels { viewModelFactory }
    val userInterestsViewModel: UserInterestsViewModel by viewModels { viewModelFactory }
    val userDiaryViewModel: UserDiaryViewModel by viewModels { viewModelFactory }

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

        StatusBarUtil.setColor(this, resources.getColor(R.color.colorTgPrimary))
        StatusBarUtil.setDarkMode(this)

        window.navigationBarColor = resources.getColor(R.color.colorTgPrimaryDark)
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

        subscribePushTopic()
        setupBottomSheets()



    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return super.onCreateView(name, context, attrs)


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
                    } else { /* Handle failures .. */ }
                }
            }
        }
    }

    private fun changeProgressState(isActive: Boolean) {
        ProgressTask(binding.progress,this, isActive).execute()
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
                showFail("Нет доступа к галерее")
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

                when {
                    e.note.changeOfPoints.toFloat() < 0f -> {
                        pointsStateControlGroup.setSelectedIndex(2, true)
                    }
                    e.note.changeOfPoints.toFloat() > 0f -> {
                        pointsStateControlGroup.setSelectedIndex(0, true)
                    }
                    else -> pointsStateControlGroup.setSelectedIndex(1, true)
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

                when {
                    e.note.changeOfPoints.toFloat() < 0f -> {
                        pointsStateControlGroup.setSelectedIndex(2, true)
                    }
                    e.note.changeOfPoints.toFloat() > 0f -> {
                        pointsStateControlGroup.setSelectedIndex(0, true)
                    }
                    else -> pointsStateControlGroup.setSelectedIndex(1, true)
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

                when {
                    e.note.changeOfPoints.toFloat() < 0f -> {
                        pointsStateControlGroup.setSelectedIndex(2, true)
                    }
                    e.note.changeOfPoints.toFloat() > 0f -> {
                        pointsStateControlGroup.setSelectedIndex(0, true)
                    }
                    else -> pointsStateControlGroup.setSelectedIndex(1, true)
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


                regularityControlGroup.setSelectedIndex(
                    index = when(e.note.regularity) {
                        Regularity.Daily.id -> 0
                        else -> 1
                    },
                    shouldAnimate = true
                )

                dailyPoint.setTextColor(
                    ContextCompat.getColor(
                        this@MainActivity,
                        if (e.note.regularity == Regularity.Daily.id) R.color.colorTgWhite else R.color.colorTgText
                    )
                )

                weeklyPoint.setTextColor(
                    ContextCompat.getColor(
                        this@MainActivity,
                        if (e.note.regularity == Regularity.Weekly.id) R.color.colorTgWhite else R.color.colorTgText
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
                        pointsStateControlGroup.setSelectedIndex(2, true)
                    }
                    e.note.changeOfPoints.toFloat() > 0f -> {
                        pointsStateControlGroup.setSelectedIndex(0, true)
                    }
                    else -> pointsStateControlGroup.setSelectedIndex(1, true)
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
        }.withBackgroundColor(ContextCompat.getColor(this, R.color.colorTgPrimary))
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

    private fun showAddPostSpotlight() {
        val addPostTargetLayout = layoutInflater.inflate(R.layout.target_menu_addpost, FrameLayout(this))
        val addPostTarget = com.takusemba.spotlight.Target.Builder()
            .setAnchor(binding.targetAddPost)
            .setShape(Circle( 16f))
            .setEffect(RippleEffect(100f, 200f, ContextCompat.getColor(this, R.color.colorTgPrimary)))
            .setOverlay(addPostTargetLayout)
            .build()

        val spotlight = Spotlight.Builder(this)
            .setTargets(addPostTarget)
            .setBackgroundColor(ContextCompat.getColor(this, R.color.colorTgPrimaryDark))
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

    fun showFail(msg: String) {
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