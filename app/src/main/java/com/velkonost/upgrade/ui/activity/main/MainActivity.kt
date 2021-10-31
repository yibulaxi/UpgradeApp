package com.velkonost.upgrade.ui.activity.main

import android.Manifest
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
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
import com.firebase.ui.auth.AuthUI
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.jaeger.library.StatusBarUtil
import com.skydoves.balloon.iconForm
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
import kotlinx.android.synthetic.main.layout_simple_custom_snackbar.*
import kotlinx.android.synthetic.main.view_post_add.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import sh.tyy.wheelpicker.core.BaseWheelPickerView
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import kotlin.coroutines.suspendCoroutine
import android.view.MotionEvent
import com.velkonost.upgrade.model.Media
import com.velkonost.upgrade.ui.activity.main.adapter.AddPostMediaAdapter
import lv.chi.photopicker.PhotoPickerFragment
import android.content.pm.PackageManager

import com.velkonost.upgrade.di.AppModule_ContextFactory.context
import com.velkonost.upgrade.di.AppModule_ContextFactory.context

import android.app.Activity
import android.util.Log

import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.lang.Exception
import kotlin.collections.ArrayList


class MainActivity : BaseActivity<HomeViewModel, ActivityMainBinding>(
    R.layout.activity_main,
    HomeViewModel::class,
    Handler::class
), PhotoPickerFragment.Callback {
    private var navController: NavController? = null

    private val addPostBehavior: BottomSheetBehavior<ConstraintLayout> by lazy {
        BottomSheetBehavior.from(binding.addPostBottomSheet.bottomSheetContainer)
    }

    private var selectedInterestIdToAddPost: String = ""
    private var selectedDiffPointToAddPost: Int = 0

    private lateinit var cloudFirestoreDatabase: FirebaseFirestore
    private lateinit var cloudStorage: FirebaseStorage
    private var isFirebaseAvailable: Boolean = false

    private lateinit var mediaAdapter: AddPostMediaAdapter

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

        addPostBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.backgroundImage.alpha = slideOffset
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    binding.backgroundImage.alpha = 0f
                    binding.navView.isVisible = true
//                    binding.backgroundImage.isVisible = false
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    binding.navView.isVisible = false
                    binding.backgroundImage.alpha = 1f
//                    binding.backgroundImage.isVisible = true
                    binding.addPostBottomSheet.editText.requestFocus()
                }
            }
        })
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (addPostBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                val outRect = Rect()

                binding.addPostBottomSheet.container.getGlobalVisibleRect(outRect)

                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    binding.addPostBottomSheet.container.post { addPostBehavior.state = BottomSheetBehavior.STATE_COLLAPSED }
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
        (binding.addPostBottomSheet.mediaRecycler.layoutManager as LinearLayoutManager).orientation = LinearLayoutManager.HORIZONTAL
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

                val ext = media.uri.toString().substring(media.uri.toString().lastIndexOf(".") + 1);
                val mediaRef = storageRef.child(
                    "notes_media/" + App.preferences.uid!!.toString() + System.currentTimeMillis()
                        .toString() + "." + ext
                )

                var file = media.uri
                val uploadTask = mediaRef.putFile(file!!)

                val urlTask = uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    mediaRef.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
//                    val downloadUri = task.result
                        uploadedUrls.add(task.result.toString())

                        if (uploadedUrls.size == mediaAdapter.getMedia().size) {
                            setDiaryNote(noteId, uploadedUrls)
                        }
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            }
        }
    }

    private fun checkPermissionForReadExternalStorage(): Boolean {
        val result = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun setupAddPostBottomSheet() {
        with(binding.addPostBottomSheet) {

            val itemCount = binding.viewModel!!.getCurrentInterests().size
            if (itemCount == 0) {
                EventBus.getDefault().post(GoAuthEvent(true))
                return@with
            }

            icon.getRecycler().setItemViewCacheSize(binding.viewModel!!.getCurrentInterests().size)
            icon.adapter.values = (0 until itemCount).map {
                CustomWheelPickerView.Item(
                    binding.viewModel!!.getCurrentInterests()[it].id.toString(),
                    ContextCompat.getDrawable(
                        this@MainActivity,
                        binding.viewModel!!.getCurrentInterests()[it].logo
                    )
                )
            }

            icon.getRecycler().post { icon.getRecycler().scrollToPosition(5) }

            icon.adapter.notifyDataSetChanged()

//            icon.isCircular = true
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
                    showFail("Введите текст записи")
                } else if (!::mediaAdapter.isInitialized || mediaAdapter.getMedia().size == 0)
                    setDiaryNote(noteId, binding.viewModel!!.getNoteMediaUrlsById(noteId))
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
    fun onGoAuthEvent(e: GoAuthEvent) {
        showFail("Сперва авторизуйтесь")
        AuthUI.getInstance()
            .signOut(this@MainActivity)
            .addOnCompleteListener {
                App.preferences.uid = ""
                App.preferences.userName = ""

                Navigator.toSplash(navController!!)
            }
    }

    @Subscribe
    fun onLoadMainEvent(e: LoadMainEvent) {
        getDiary()
        getInterests { Navigator.splashToMetric(e.f) }
    }

    @Subscribe
    fun onChangeNavViewVisibilityEvent(e: ChangeNavViewVisibilityEvent) {
        binding.navView.isVisible = e.isVisible
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
        } catch (e: Exception) {
            isFirebaseAvailable = false
        }

        try {
            cloudFirestoreDatabase = Firebase.firestore
            cloudStorage = FirebaseStorage.getInstance()
        } catch (e: Exception) {
            isFirebaseAvailable = false
        }
    }

    private fun setupNavMenu() {
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
                        getDiary()
                        getInterests { Navigator.welcomeToMetric(e.f) }
//                        Navigator.welcomeToMetric(e.f)
                    }
            }
            .addOnFailureListener {

            }
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
                if (icon.adapter.values[i].id == e.note.interestId) {
                    selectedIndex = i
                    break
                }
            }

            icon.getRecycler().scrollToPosition(5)
            icon.getRecycler().post { icon.setSelectedIndex(selectedIndex , animated = true) }

            date.text = e.note.date

            when {
                e.note.amount.toFloat() < 0f -> {
                    pointsStateControlGroup.setSelectedIndex(2, true)
                }
                e.note.amount.toFloat() > 0f -> {
                    pointsStateControlGroup.setSelectedIndex(0, true)
                }
                else -> pointsStateControlGroup.setSelectedIndex(1, true)
            }

            val urls = arrayListOf<Media>()
            for (url in e.note.mediaUrls) {
                urls.add(Media(url = url))
            }
            mediaAdapter = AddPostMediaAdapter(this@MainActivity, urls)
            mediaRecycler.adapter = mediaAdapter
        }
    }

    @Subscribe
    fun onDeleteDiaryNoteEvent(e: DeleteDiaryNoteEvent) {
        deleteDiaryNote(e.noteId)
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
                getDiary()
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
                EventBus.getDefault().post(UpdateDiaryEvent(true))
            }
            .addOnFailureListener {

            }
    }

    private fun deleteDiaryNote(noteId: String) {
        val deleteNote = hashMapOf(
            noteId to FieldValue.delete()
        )

        cloudFirestoreDatabase.collection("users_diary").document(App.preferences.uid!!)
            .update(deleteNote as Map<String, Any>)
            .addOnSuccessListener { }
            .addOnFailureListener {  }
    }

    private fun setDiaryNote(noteId: String? = null, mediaUrls: ArrayList<String>? = arrayListOf()) {
        with(binding.addPostBottomSheet) {


            val amount: Float = when (selectedDiffPointToAddPost) {
                0 -> 0.1f
                1 -> 0f
                else -> -0.1f
            }

            val diaryId = noteId?: System.currentTimeMillis()
            val data = hashMapOf(
                "id" to diaryId,
                "text" to editText.text.toString(),
                "date" to date.text.toString(),
                "interest" to selectedInterestIdToAddPost,
                "amount" to String.format("%.1f", amount),
                "media" to mediaUrls
            )

            val megaData = hashMapOf(
                diaryId.toString() to data
            )

            cloudFirestoreDatabase.collection("users_diary").document(App.preferences.uid!!)
                .update(megaData as Map<String, Any>)
                .addOnSuccessListener {
                    showSuccess("Запись добавлена")
                    editText.text?.clear()

                    EventBus.getDefault()
                        .post(UpdateUserInterestEvent(selectedInterestIdToAddPost, amount = amount))

                    addPostBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
                .addOnFailureListener {
                    if (it is FirebaseFirestoreException && it.code.value() == 5) {
                        cloudFirestoreDatabase.collection("users_diary").document(App.preferences.uid!!)
                            .set(megaData as Map<String, Any>)
                            .addOnSuccessListener {
                                showSuccess("Запись добавлена")
                                editText.text?.clear()

                                EventBus.getDefault()
                                    .post(UpdateUserInterestEvent(selectedInterestIdToAddPost, amount = amount))

                                addPostBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                            }
                            .addOnFailureListener {
                                showFail("Произошла ошибка")
                            }
                    } else showFail("Произошла ошибка")
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

    inner class Handler
}