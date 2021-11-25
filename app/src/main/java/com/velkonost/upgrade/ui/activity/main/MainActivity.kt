package com.velkonost.upgrade.ui.activity.main

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
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
import com.velkonost.upgrade.App
import com.velkonost.upgrade.R
import com.velkonost.upgrade.databinding.ActivityMainBinding
import com.velkonost.upgrade.event.*
import com.velkonost.upgrade.model.Media
import com.velkonost.upgrade.navigation.Navigator
import com.velkonost.upgrade.ui.activity.main.adapter.AddPostMediaAdapter
import com.velkonost.upgrade.ui.base.BaseActivity
import com.velkonost.upgrade.ui.view.CustomWheelPickerView
import com.velkonost.upgrade.ui.view.SimpleCustomSnackbar
import com.velkonost.upgrade.util.ResourcesProvider
import com.velkonost.upgrade.vm.BaseViewModel
import com.velkonost.upgrade.vm.UserDiaryViewModel
import com.velkonost.upgrade.vm.UserInterestsViewModel
import com.velkonost.upgrade.vm.UserSettingsViewModel
import kotlinx.android.synthetic.main.item_adapter_pager_notes.*
import kotlinx.android.synthetic.main.layout_simple_custom_snackbar.*
import kotlinx.android.synthetic.main.view_post_add.*
import lv.chi.photopicker.PhotoPickerFragment
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import sh.tyy.wheelpicker.core.BaseWheelPickerView
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : BaseActivity<BaseViewModel, ActivityMainBinding>(
    R.layout.activity_main,
    BaseViewModel::class,
    Handler::class
), PhotoPickerFragment.Callback {

    private val userSettingsViewModel: UserSettingsViewModel by viewModels { viewModelFactory }
    private val userInterestsViewModel: UserInterestsViewModel by viewModels { viewModelFactory }
    private val userDiaryViewModel: UserDiaryViewModel by viewModels { viewModelFactory }

    private var navController: NavController? = null

    private val addPostBehavior: BottomSheetBehavior<ConstraintLayout> by lazy {
        BottomSheetBehavior.from(binding.addPostBottomSheet.bottomSheetContainer)
    }

    private var selectedInterestIdToAddPost: String = ""
    private var selectedDiffPointToAddPost: Int = 0

    //    private lateinit var cloudFirestoreDatabase: FirebaseFirestore
    private lateinit var cloudStorage: FirebaseStorage
    private var isFirebaseAvailable: Boolean = false

    private lateinit var mediaAdapter: AddPostMediaAdapter

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)

        StatusBarUtil.setTransparent(this)

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

        addPostBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.backgroundImage.alpha = slideOffset
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    binding.backgroundImage.alpha = 0f
                    binding.navView.isVisible = true
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    binding.navView.isVisible = false
                    binding.backgroundImage.alpha = 1f
                    binding.addPostBottomSheet.editText.requestFocus()
                }
            }
        })

    }

    override fun onViewModelReady(viewModel: BaseViewModel) {
        super.onViewModelReady(viewModel)

        viewModel.errorEvent.observe(this, ::showFail)
        viewModel.successEvent.observe(this, ::showSuccess)
        userInterestsViewModel.setupNavMenuEvent.observe(this, ::setupNavMenu)

        userDiaryViewModel.setDiaryNoteEvent.observe(this, ::observeSetDiaryNote)

        userSettingsViewModel.setUserSettingsEvent.observe(this, ::observeUserSettings)

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

    private fun uploadMedia(noteId: String? = null) {
        val storageRef = cloudStorage.reference
        val uploadedUrls = arrayListOf<String>()

        for (media in mediaAdapter.getMedia()) {
            if (media.uri == null) {
                uploadedUrls.add(media.url!!)

                if (uploadedUrls.size == mediaAdapter.getMedia().size) {
                    setDiaryNote(noteId, uploadedUrls)
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
                            setDiaryNote(noteId, uploadedUrls)
                        }
                    } else { /* Handle failures .. */
                    }
                }
            }
        }
    }

    private fun checkPermissionForReadExternalStorage(): Boolean {
        val result = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun observeUserSettings(isUserSettingsReady: Boolean) {
        EventBus.getDefault().post(UserSettingsReadyEvent(isUserSettingsReady))
    }

    private fun setupAddPostBottomSheet() {
        with(binding.addPostBottomSheet) {

            val itemCount = userInterestsViewModel.getInterests().size
            if (itemCount == 0) {
                EventBus.getDefault().post(GoAuthEvent(true))
                return@with
            }

            icon.getRecycler().setItemViewCacheSize(userInterestsViewModel.getInterests().size)
            icon.adapter.values = (0 until itemCount).map {
                CustomWheelPickerView.Item(
                    userInterestsViewModel.getInterests()[it].id,
                    ContextCompat.getDrawable(
                        this@MainActivity,
                        userInterestsViewModel.getInterests()[it].getLogo()
                    )
                )
            }

            icon.getRecycler().post { icon.getRecycler().scrollToPosition(5) }

            icon.adapter.notifyDataSetChanged()

            icon.isHapticFeedbackEnabled = true

            icon.setWheelListener(object : BaseWheelPickerView.WheelPickerViewListener {
                override fun didSelectItem(picker: BaseWheelPickerView, index: Int) {
                    selectedInterestIdToAddPost =
                        icon.adapter.values.getOrNull(index)?.id.toString()
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

            addPost.setOnClickListener {
                if (editText.text?.length == 0) {
                    showFail(getString(R.string.enter_note_text))
                } else if (!::mediaAdapter.isInitialized || mediaAdapter.getMedia().size == 0)
                    setDiaryNote(noteId, userDiaryViewModel.getNoteMediaUrlsById(noteId))
                else uploadMedia(noteId)
            }

            addMedia.setOnClickListener {
                if (checkPermissionForReadExternalStorage()) {
                    PhotoPickerFragment.newInstance(
                        multiple = true,
                        allowCamera = true,
                        maxSelection = 5,
                        theme = R.style.ChiliPhotoPicker_Light
                    ).show(supportFragmentManager, "picker")
                }
            }
        }
    }

    @Subscribe
    fun onLoadMainEvent(e: LoadMainEvent) {
        userDiaryViewModel.getDiary()
        userSettingsViewModel.getUserSettings()
        userInterestsViewModel.getInterests { Navigator.splashToMetric(e.f) }
    }

    @Subscribe
    fun onGoAuthEvent(e: GoAuthEvent) {
        showFail(getString(R.string.go_auth))
        AuthUI.getInstance()
            .signOut(this@MainActivity)
            .addOnCompleteListener {
                App.preferences.uid = ""
                App.preferences.userName = ""

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
//            cloudFirestoreDatabase = Firebase.firestore
            cloudStorage = FirebaseStorage.getInstance()
        } catch (e: Exception) {
            isFirebaseAvailable = false
        }
    }

    private fun setupNavMenu(msg: String) {
        if (binding.navView.menu.size() != 0) return

        binding.navView.inflateMenu(R.menu.bottom_nav_menu)
        binding.navView.isVisible = true

        binding.navView.menu.getItem(2).setOnMenuItemClickListener {
            addPostBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            binding.addPostBottomSheet.noteId = null

            binding.addPostBottomSheet.editText.setText("")
            binding.addPostBottomSheet.editText.requestFocus()

            mediaAdapter = AddPostMediaAdapter(this, arrayListOf())
            binding.addPostBottomSheet.mediaRecycler.adapter = mediaAdapter

            return@setOnMenuItemClickListener true
        }
        setupAddPostBottomSheet()
    }

    @Subscribe
    fun onChangeTabEvent(e: ChangeTabEvent) {
        binding.navView.selectedItemId = e.itemId
    }

    @Subscribe
    fun onEditDiaryNoteEvent(e: EditDiaryNoteEvent) {
        addPostBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        binding.addPostBottomSheet.editText.requestFocus()

        with(binding.addPostBottomSheet) {
            noteId = e.note.id

            editText.setText(e.note.text)
            editText.setSelection(editText.length())


            var selectedIndex = 0
            for (i in icon.adapter.values.indices) {
                if (icon.adapter.values[i].id == e.note.interest.interestId) {
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
            for (url in e.note.media?: arrayListOf()) {
                urls.add(Media(url = url))
            }
            mediaAdapter = AddPostMediaAdapter(this@MainActivity, urls)
            mediaRecycler.adapter = mediaAdapter
        }
    }

    private fun setDiaryNote(
        noteId: String? = null,
        mediaUrls: ArrayList<String>? = arrayListOf()
    ) {
        with(binding.addPostBottomSheet) {
            userDiaryViewModel.setDiaryNote(
                noteId = noteId,
                text = editText.text.toString(),
                date = date.text.toString(),
                selectedInterestIdToAddPost = selectedInterestIdToAddPost,
                selectedDiffPointToAddPost = selectedDiffPointToAddPost,
                mediaUrls = mediaUrls
            )
        }
    }

    private fun observeSetDiaryNote(isSuccess: Boolean) {
        if (isSuccess) {
            showSuccess(getString(R.string.note_created))
            binding.addPostBottomSheet.editText.text?.clear()
            addPostBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } else showFail(getString(com.velkonost.upgrade.R.string.error_happened))
    }

    @Subscribe
    fun onOpenFullScreenMediaEvent(e: OpenFullScreenMediaEvent) {
        StfalconImageViewer.Builder<Media>(this, e.media) { view, image ->
            if (image?.url != null)
                Picasso.with(this@MainActivity).load(image.url).into(view)
        }.withBackgroundColor(ContextCompat.getColor(this, R.color.colorWhite))
            .withTransitionFrom(e.imageView).show().setCurrentPosition(e.position)
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

    inner class Handler
}