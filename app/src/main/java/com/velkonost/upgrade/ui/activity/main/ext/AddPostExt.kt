package com.velkonost.upgrade.ui.activity.main.ext

import android.animation.ArgbEvaluator
import android.os.Build
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.velkonost.upgrade.R
import com.velkonost.upgrade.ui.activity.main.MainActivity
import com.velkonost.upgrade.ui.activity.main.adapter.AddPostMediaAdapter
import com.velkonost.upgrade.util.ext.observeOnce
import sh.tyy.wheelpicker.core.BaseWheelPickerView
import java.text.SimpleDateFormat
import android.animation.ValueAnimator

import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.res.ColorStateList
import android.view.animation.DecelerateInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.view.ViewCompat.setBackgroundTintList
import android.animation.ObjectAnimator
import android.graphics.Color
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import com.velkonost.upgrade.model.AllLogo
import java.util.*


fun MainActivity.setupBottomSheets() {
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

    selectNoteTypeBehavior.addBottomSheetCallback(object :
        BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            binding.backgroundImage.alpha = slideOffset

            if (!binding.backgroundImage.isVisible) {
                binding.backgroundImage.isVisible = true
            }
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                binding.navView.isVisible = true

                binding.backgroundImage.alpha = 0f
                binding.backgroundImage.isClickable = false
                binding.backgroundImage.isEnabled = false
            } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                binding.navView.isVisible = false

                binding.backgroundImage.alpha = 1f
                binding.backgroundImage.isClickable = true
                binding.backgroundImage.isEnabled = true
            }
        }
    })

    addGoalBehavior.addBottomSheetCallback(object :
        BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            binding.backgroundImage.alpha = slideOffset

            if (!binding.backgroundImage.isVisible) {
                binding.backgroundImage.isVisible = true
            }
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                binding.navView.isVisible = true

                binding.backgroundImage.alpha = 0f
                binding.backgroundImage.isClickable = false
                binding.backgroundImage.isEnabled = false
            } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                binding.navView.isVisible = false

                binding.backgroundImage.alpha = 1f
                binding.backgroundImage.isClickable = true
                binding.backgroundImage.isEnabled = true
            }
        }
    })

    addTrackerBehavior.addBottomSheetCallback(object :
        BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            binding.backgroundImage.alpha = slideOffset

            if (!binding.backgroundImage.isVisible) {
                binding.backgroundImage.isVisible = true
            }
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                binding.navView.isVisible = true

                binding.backgroundImage.alpha = 0f
                binding.backgroundImage.isClickable = false
                binding.backgroundImage.isEnabled = false
            } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                binding.navView.isVisible = false

                binding.backgroundImage.alpha = 1f
                binding.backgroundImage.isClickable = true
                binding.backgroundImage.isEnabled = true
            }
        }
    })

    addHabitBehavior.addBottomSheetCallback(object :
        BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            binding.backgroundImage.alpha = slideOffset

            if (!binding.backgroundImage.isVisible) {
                binding.backgroundImage.isVisible = true
            }
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                binding.navView.isVisible = true

                binding.backgroundImage.alpha = 0f
                binding.backgroundImage.isClickable = false
                binding.backgroundImage.isEnabled = false
            } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                binding.navView.isVisible = false

                binding.backgroundImage.alpha = 1f
                binding.backgroundImage.isClickable = true
                binding.backgroundImage.isEnabled = true
            }
        }
    })

    binding.backgroundImage.setOnClickListener {
        if (selectNoteTypeBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            selectNoteTypeBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }
}

fun MainActivity.setupSelectNoteTypeBottomSheet() {
    val context = this
    with(binding.selectNoteTypeBottomSheet) {

        noteType.setOnClickListener {
            selectNoteTypeBehavior.state =
                BottomSheetBehavior.STATE_COLLAPSED
            addPostBehavior.state =
                BottomSheetBehavior.STATE_EXPANDED

            binding.addPostBottomSheet.noteId = null

            binding.addPostBottomSheet.editText.setText("")
            binding.addPostBottomSheet.editText.requestFocus()

            mediaAdapter = AddPostMediaAdapter(context, arrayListOf())
            binding.addPostBottomSheet.mediaRecycler.adapter = mediaAdapter
        }

        trackerType.setOnClickListener {
            selectNoteTypeBehavior.state =
                BottomSheetBehavior.STATE_COLLAPSED
            addTrackerBehavior.state =
                BottomSheetBehavior.STATE_EXPANDED

            binding.addTrackerBottomSheet.noteId = null

            binding.addTrackerBottomSheet.editText.setText("")
            binding.addTrackerBottomSheet.editText.requestFocus()
        }

        goalType.setOnClickListener {
            selectNoteTypeBehavior.state =
                BottomSheetBehavior.STATE_COLLAPSED
            addGoalBehavior.state =
                BottomSheetBehavior.STATE_EXPANDED

            binding.addGoalBottomSheet.noteId = null

            binding.addGoalBottomSheet.editText.setText("")
            binding.addGoalBottomSheet.editText.requestFocus()
        }

        habbitType.setOnClickListener {

        }
    }
}

fun MainActivity.setupAddPostBottomSheet() {
    val context = this
    with(binding.addPostBottomSheet) {

        val itemCount = userInterestsViewModel.getInterests().size

        icon.getRecycler().setItemViewCacheSize(userInterestsViewModel.getInterests().size)
        icon.adapter.values = (0 until itemCount).map {
            com.velkonost.upgrade.ui.view.CustomWheelPickerView.Item(
                userInterestsViewModel.getInterests()[it].id,
                androidx.core.content.ContextCompat.getDrawable(
                    context,
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) resources.configuration.locales[0]
            else resources.configuration.locale
        ).format(System.currentTimeMillis())
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
            } else if (!isMediaAdapterInitialized() || mediaAdapter.getMedia().size == 0)
                userDiaryViewModel.getNoteMediaById(noteId ?: "").observeOnce(context) { diaryNote ->
                    setDiaryNote(
                        noteId = noteId,
                        noteType = com.velkonost.upgrade.model.NoteType.Note.id,
                        mediaUrls = diaryNote?.media,
                        text = editText.text.toString(),
                        date =
                        if (diaryNote?.date.isNullOrEmpty()) System.currentTimeMillis().toString()
                        else diaryNote?.date!!
                    )
                }
            else uploadMedia(
                noteId = noteId,
                text = editText.text.toString(),
                date =
                System.currentTimeMillis().toString()

            )
        }

        addMedia.setOnClickListener {
            if (checkPermissionForReadExternalStorage()) {
                openGallery()
            }
        }
    }
}

fun MainActivity.setupAddGoalBottomSheet() {
    val context = this
    with(binding.addGoalBottomSheet) {

        val itemCount = userInterestsViewModel.getInterests().size

        icon.getRecycler().setItemViewCacheSize(userInterestsViewModel.getInterests().size)
        icon.adapter.values = (0 until itemCount).map {
            com.velkonost.upgrade.ui.view.CustomWheelPickerView.Item(
                userInterestsViewModel.getInterests()[it].id,
                androidx.core.content.ContextCompat.getDrawable(
                    context,
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) resources.configuration.locales[0]
            else resources.configuration.locale
        ).format(System.currentTimeMillis())
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
            } else
                setDiaryNote(
                    noteId = noteId,
                    noteType = com.velkonost.upgrade.model.NoteType.Goal.id,
                    text = editText.text.toString(),
                    date = System.currentTimeMillis().toString()
                )
        }
    }
}

fun MainActivity.setupAddTrackerBottomSheet() {
    val context = this
    with(binding.addTrackerBottomSheet) {

        val itemCount = userInterestsViewModel.getInterests().size

        icon.getRecycler().setItemViewCacheSize(userInterestsViewModel.getInterests().size)
        icon.adapter.values = (0 until itemCount).map {
            com.velkonost.upgrade.ui.view.CustomWheelPickerView.Item(
                userInterestsViewModel.getInterests()[it].id,
                androidx.core.content.ContextCompat.getDrawable(
                    context,
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) resources.configuration.locales[0]
            else resources.configuration.locale
        ).format(System.currentTimeMillis())
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
            } else {
                userDiaryViewModel.getActiveTracker().observeOnce(context) {
                    if (it == null) {
                        setDiaryNote(
                            noteId = noteId,
                            noteType = com.velkonost.upgrade.model.NoteType.Tracker.id,
                            text = editText.text.toString(),
                            date =
                            if (it?.date.isNullOrEmpty()) System.currentTimeMillis().toString()
                            else it?.date!!,
                            datetimeStart = System.currentTimeMillis().toString(),
                            isActiveNow = true
                        )
                    } else {
                        showFail("Одновременно может быть активен только 1 трекер")
                    }
                }

            }
        }
    }
}

fun MainActivity.setupTrackerSheet() {
    binding.trackerSheet.setFab(binding.trackerFab)

    binding.trackerFab.setOnClickListener {
        binding.trackerSheet.expandFab()
        binding.trackerFab.isVisible = false
    }

    userDiaryViewModel.getActiveTracker().observeForever { activeTracker ->
        binding.trackerFab.isVisible = activeTracker != null && activeTracker.isActiveNow!!


        if (isActiveTrackerTimerInitialized()) {
            activeTrackerTimer.cancel()
        }

        if (activeTracker == null && binding.trackerSheet.isFabExpanded) {
            binding.trackerSheet.contractFab()
        }

        if (activeTracker != null) {
            val firstColor = resources.getColor(R.color.colorTgGray)
            val secondColor = resources.getColor(R.color.colorTgPrimary)

            val colorAnimationFromFirstToSecond = ValueAnimator.ofObject(ArgbEvaluator(), firstColor, secondColor)
            val colorAnimationFromSecondToFirst = ValueAnimator.ofObject(ArgbEvaluator(), secondColor, firstColor)

            colorAnimationFromFirstToSecond.duration = 3000
            colorAnimationFromSecondToFirst.duration = 3000

            colorAnimationFromSecondToFirst.startDelay = 1000
            colorAnimationFromFirstToSecond.startDelay = 1000

            colorAnimationFromFirstToSecond.addUpdateListener { animator ->
                setBackgroundTintList(
                    binding.trackerFab,
                    ColorStateList.valueOf(animator.animatedValue as Int)
                )
            }

            colorAnimationFromSecondToFirst.addUpdateListener { animator ->
                setBackgroundTintList(
                    binding.trackerFab,
                    ColorStateList.valueOf(animator.animatedValue as Int)
                )
            }

            colorAnimationFromFirstToSecond.doOnEnd {
                colorAnimationFromSecondToFirst.start()
            }

            colorAnimationFromSecondToFirst.doOnEnd {
                colorAnimationFromFirstToSecond.start()
            }

            colorAnimationFromSecondToFirst.start()

            binding.stopTracker.setOnClickListener {
                userDiaryViewModel.changeTrackerState(
                    activeTracker,
                    false
                )

                activeTrackerTimer.cancel()
            }

            binding.trackerTitle.text = activeTracker.text
            binding.trackerDate.text = SimpleDateFormat("dd MMM, HH:mm", Locale("ru")).format(activeTracker.date.toLong())
            binding.trackerInterestName.text = activeTracker.interest!!.interestName

            binding.trackerIcon.setImageDrawable(
                AppCompatResources.getDrawable(
                    this,
                    AllLogo().getLogoById(activeTracker.interest.interestIcon)
                )
            )


            activeTrackerTimer = object : CountDownTimer(20000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    if (activeTracker == null) cancel()

                    val trackerTime = System.currentTimeMillis() - activeTracker.datetimeStart!!.toLong()

                    val hours = (trackerTime / (1000 * 60 * 60)).toInt()
                    val minutes = ((trackerTime / (1000 * 60)) % 60).toInt()
                    val seconds = (trackerTime / 1000 ) % 60

                    binding.timer.text = String.format(
                        "%02d:%02d:%02d",
                        hours, minutes, seconds
                    )
                }

                override fun onFinish() {
                    start()
                }
            }

            activeTrackerTimer.start()
            isTrackerTimerRunning = true

        }
    }
}


fun MainActivity.setupAddHabitBottomSheet() {
    val context = this
    with(binding.addHabitBottomSheet) {

        val itemCount = userInterestsViewModel.getInterests().size

        icon.getRecycler().setItemViewCacheSize(userInterestsViewModel.getInterests().size)
        icon.adapter.values = (0 until itemCount).map {
            com.velkonost.upgrade.ui.view.CustomWheelPickerView.Item(
                userInterestsViewModel.getInterests()[it].id,
                androidx.core.content.ContextCompat.getDrawable(
                    context,
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
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) resources.configuration.locales[0]
            else resources.configuration.locale
        ).format(java.util.Calendar.getInstance().timeInMillis)
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
            } else
                setDiaryNote(
                    noteId = noteId,
                    noteType = com.velkonost.upgrade.model.NoteType.Habit.id,
                    text = editText.text.toString(),
                    date = date.text.toString(),
                    datetimeStart = System.currentTimeMillis().toString(),
                    isActiveNow = true
                )
        }
    }
}