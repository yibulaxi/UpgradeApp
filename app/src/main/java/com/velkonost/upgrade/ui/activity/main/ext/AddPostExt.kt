package com.velkonost.upgrade.ui.activity.main.ext

import android.os.Build
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.velkonost.upgrade.R
import com.velkonost.upgrade.model.NoteType
import com.velkonost.upgrade.ui.activity.main.MainActivity
import com.velkonost.upgrade.ui.activity.main.adapter.AddPostMediaAdapter
import com.velkonost.upgrade.ui.view.CustomWheelPickerView
import com.velkonost.upgrade.util.ext.observeOnce
import sh.tyy.wheelpicker.core.BaseWheelPickerView
import java.text.SimpleDateFormat
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
            selectNoteTypeBehavior.state = com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
            addPostBehavior.state = com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED

            binding.addPostBottomSheet.noteId = null

            binding.addPostBottomSheet.editText.setText("")
            binding.addPostBottomSheet.editText.requestFocus()

            mediaAdapter = AddPostMediaAdapter(context, arrayListOf())
            binding.addPostBottomSheet.mediaRecycler.adapter = mediaAdapter
        }

        trackerType.setOnClickListener {

        }

        goalType.setOnClickListener {
            selectNoteTypeBehavior.state = com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
            addGoalBehavior.state = com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED

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
            } else if (!isMediaAdapterInitialized() || mediaAdapter.getMedia().size == 0)
                userDiaryViewModel.getNoteMediaById(noteId?: "").observeOnce(context) {
                    setDiaryNote(
                        noteId = noteId,
                        noteType = com.velkonost.upgrade.model.NoteType.Note.id,
                        mediaUrls = it?.media,
                        text = editText.text.toString(),
                        date = date.text.toString()
                    )
//                        userDiaryViewModel.getNoteMediaById(noteId?: "").removeObserver()
                }

            else uploadMedia(
                noteId = noteId,
                text = editText.text.toString(),
                date = date.text.toString()
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
                showFail(getString(com.velkonost.upgrade.R.string.enter_note_text))
            } else
                userDiaryViewModel
                    .getNoteMediaById(noteId?: "")
                    .observeOnce(context) {
                        setDiaryNote(
                            noteId = noteId,
                            noteType = com.velkonost.upgrade.model.NoteType.Goal.id,
                            text = editText.text.toString(),
                            date = date.text.toString()
                        )
                    }
        }
    }
}