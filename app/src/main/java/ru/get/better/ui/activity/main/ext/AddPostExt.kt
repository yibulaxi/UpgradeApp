package ru.get.better.ui.activity.main.ext

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Build
import android.os.CountDownTimer
import android.text.Html
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.onegravity.rteditor.RTManager
import com.onegravity.rteditor.api.RTApi
import com.onegravity.rteditor.api.RTMediaFactoryImpl
import com.onegravity.rteditor.api.RTProxyImpl
import com.onegravity.rteditor.api.format.RTFormat
import com.onegravity.rteditor.toolbar.RTToolbarImageButton
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import ru.get.better.App
import ru.get.better.R
import ru.get.better.event.ChangeProgressStateEvent
import ru.get.better.model.*
import ru.get.better.ui.activity.main.MainActivity
import ru.get.better.ui.activity.main.adapter.AddPostMediaAdapter
import ru.get.better.ui.activity.main.ext.adapter.TagsAdapter
import ru.get.better.ui.view.CustomWheelPickerView
import ru.get.better.ui.view.LockableBottomSheetBehavior
import ru.get.better.ui.view.OwnHorizontalRTToolbar
import ru.get.better.util.Keyboard
import sh.tyy.wheelpicker.core.BaseWheelPickerView
import java.text.SimpleDateFormat
import java.util.*


fun MainActivity.showSelectNoteTypeView() {
    val animationDuration = 500L

    with(binding.selectNoteTypeBottomSheet) {
        noteType.animate()
            .alpha(1f)
            .translationX(0f)
            .translationY(0f).duration = animationDuration

        trackerType.animate()
            .alpha(1f)
            .translationX(0f)
            .translationY(0f).duration = animationDuration

        habbitType.animate()
            .alpha(1f)
            .translationX(0f)
            .translationY(0f).duration = animationDuration

        goalType.animate()
            .alpha(1f)
            .translationX(0f)
            .translationY(0f).duration = animationDuration

        title.animate()
            .alpha(1f)
            .translationX(0f)
            .translationY(0f).duration = animationDuration

        ending.animate()
            .alpha(1f)
            .translationX(0f)
            .translationY(0f)
            .setDuration(animationDuration)
    }

    binding.backgroundImage.isClickable = true
    binding.backgroundImage.isEnabled = true
    binding.backgroundImage.isVisible = true

    binding.backgroundImage.animate()
        .alpha(1f)
        .setDuration(animationDuration)
        .withEndAction {
            binding.selectNoteTypeBottomSheet.selectNoteTypeHorizontalSeparator.isVisible = true
            binding.selectNoteTypeBottomSheet.selectNoteTypeVerticalSeparator.isVisible = true
        }
}

fun MainActivity.hideSelectNoteTypeView(
    hideBackgroundImage: Boolean = true
) {
    val animationDuration = 500L

    with(binding.selectNoteTypeBottomSheet) {
        noteType.animate()
            .alpha(0f)
            .translationX(-500f)
            .translationY(-500f).duration = animationDuration

        trackerType.animate()
            .alpha(0f)
            .translationX(500f)
            .translationY(-500f).duration = animationDuration

        habbitType.animate()
            .alpha(0f)
            .translationX(500f)
            .translationY(500f).duration = animationDuration

        goalType.animate()
            .alpha(0f)
            .translationX(-500f)
            .translationY(500f).duration = animationDuration

        title.animate()
            .alpha(0f)
            .translationX(0f)
            .translationY(-500f).duration = animationDuration

        ending.animate()
            .alpha(0f)
            .translationX(0f)
            .translationY(500f)
            .setDuration(animationDuration)
    }

    binding.selectNoteTypeBottomSheet.selectNoteTypeHorizontalSeparator.isVisible = false
    binding.selectNoteTypeBottomSheet.selectNoteTypeVerticalSeparator.isVisible = false

    binding.backgroundImage.isClickable = false
    binding.backgroundImage.isEnabled = false
    if (hideBackgroundImage) {
        binding.backgroundImage.animate()
            .alpha(0f)
            .setDuration(animationDuration)
            .withEndAction {
                binding.backgroundImage.isVisible = false
            }
    }
}


fun MainActivity.setupBottomSheets() {

    val addPostCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            binding.backgroundImage.alpha = slideOffset
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_COLLAPSED) {

                binding.backgroundImage.alpha = 0f
                binding.navView.isVisible = true

                Keyboard.hide(this@setupBottomSheets)
            } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                binding.navView.isVisible = false
                binding.backgroundImage.alpha = 1f
                binding.addPostBottomSheet.editText.requestFocus()
            }
        }
    }

    kotlin.runCatching { addPostBehavior.removeBottomSheetCallback(addPostCallback) }
    addPostBehavior.addBottomSheetCallback(addPostCallback)

    val addGoalCallback = object :
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

                Keyboard.hide(this@setupBottomSheets)
            } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                binding.navView.isVisible = false

                binding.backgroundImage.alpha = 1f
                binding.backgroundImage.isClickable = true
                binding.backgroundImage.isEnabled = true
            }
        }
    }

    kotlin.runCatching { addGoalBehavior.removeBottomSheetCallback(addGoalCallback) }
    addGoalBehavior.addBottomSheetCallback(addGoalCallback)

    val addTrackerCallback = object :
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

                Keyboard.hide(this@setupBottomSheets)
            } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                binding.navView.isVisible = false

                binding.backgroundImage.alpha = 1f
                binding.backgroundImage.isClickable = true
                binding.backgroundImage.isEnabled = true
            }
        }
    }

    kotlin.runCatching { addTrackerBehavior.removeBottomSheetCallback(addTrackerCallback) }
    addTrackerBehavior.addBottomSheetCallback(addTrackerCallback)

    val addHabitCallback = object :
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

                Keyboard.hide(this@setupBottomSheets)
            } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                binding.navView.isVisible = false

                binding.backgroundImage.alpha = 1f
                binding.backgroundImage.isClickable = true
                binding.backgroundImage.isEnabled = true
            }
        }
    }

    kotlin.runCatching { addHabitBehavior.removeBottomSheetCallback(addHabitCallback) }
    addHabitBehavior.addBottomSheetCallback(addHabitCallback)

    if (!binding.backgroundImage.hasOnClickListeners()) {
        binding.backgroundImage.setOnClickListener {
            if (binding.selectNoteTypeBottomSheet.title.translationY == 0f)
                hideSelectNoteTypeView()
        }
    }
}

fun MainActivity.setupSelectNoteTypeBottomSheet() {
    val context = this
    with(binding.selectNoteTypeBottomSheet) {

        titleText.text = App.resourcesProvider.getStringLocale(
            R.string.what_add_in_diary,
            App.preferences.locale
        )
        endingText.text = App.resourcesProvider.getStringLocale(
            R.string.cancel,
            App.preferences.locale
        )

        diaryTitle.text =
            App.resourcesProvider.getStringLocale(R.string.note, App.preferences.locale)
        trackerTitle.text =
            App.resourcesProvider.getStringLocale(R.string.tracker, App.preferences.locale)
        goalTitle.text =
            App.resourcesProvider.getStringLocale(R.string.goal, App.preferences.locale)
        habitTitle.text =
            App.resourcesProvider.getStringLocale(R.string.habit, App.preferences.locale)

        selectNoteTypeVerticalSeparator.setBackgroundColor(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkViewSelectNoteTypeBackgroundTint
                else R.color.colorLightViewSelectNoteTypeBackgroundTint
            )
        )

        selectNoteTypeHorizontalSeparator.setBackgroundColor(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkViewSelectNoteTypeBackgroundTint
                else R.color.colorLightViewSelectNoteTypeBackgroundTint
            )
        )

        title.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkViewSelectNoteTypeBackgroundTint
                else R.color.colorLightViewSelectNoteTypeBackgroundTint
            )
        )
        ending.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkViewSelectNoteTypeBackgroundTint
                else R.color.colorLightViewSelectNoteTypeBackgroundTint
            )
        )

        titleText.setTextColor(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkViewSelectNoteTypeTitleText
                else R.color.colorLightViewSelectNoteTypeTitleText
            )
        )
        endingText.setTextColor(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkViewSelectNoteTypeTitleText
                else R.color.colorLightViewSelectNoteTypeTitleText
            )
        )

        noteType.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkViewSelectNoteTypeNoteTypeBackgroundTint
                else R.color.colorLightViewSelectNoteTypeNoteTypeBackgroundTint
            )
        )

        icDiary.imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkViewSelectNoteTypeNoteTypeTint
                else R.color.colorLightViewSelectNoteTypeNoteTypeTint
            )
        )

        diaryTitle.setTextColor(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkViewSelectNoteTypeNoteTypeText
                else R.color.colorLightViewSelectNoteTypeNoteTypeText
            )
        )

        trackerType.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkViewSelectNoteTypeTrackerTypeBackgroundTint
                else R.color.colorLightViewSelectNoteTypeTrackerTypeBackgroundTint
            )
        )

        icTracker.imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkViewSelectNoteTypeTrackerTypeTint
                else R.color.colorLightViewSelectNoteTypeTrackerTypeTint
            )
        )

        trackerTitle.setTextColor(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkViewSelectNoteTypeTrackerTypeText
                else R.color.colorLightViewSelectNoteTypeTrackerTypeText
            )
        )

        goalType.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkViewSelectNoteTypeGoalTypeBackgroundTint
                else R.color.colorLightViewSelectNoteTypeGoalTypeBackgroundTint
            )
        )

        icGoal.imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkViewSelectNoteTypeGoalTypeTint
                else R.color.colorLightViewSelectNoteTypeGoalTypeTint
            )
        )

        goalTitle.setTextColor(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkViewSelectNoteTypeGoalTypeText
                else R.color.colorLightViewSelectNoteTypeGoalTypeText
            )
        )

        habbitType.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkViewSelectNoteTypeHabitTypeBackgroundTint
                else R.color.colorLightViewSelectNoteTypeHabitTypeBackgroundTint
            )
        )

        icHabit.imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkViewSelectNoteTypeHabitTypeTint
                else R.color.colorLightViewSelectNoteTypeHabitTypeTint
            )
        )

        habitTitle.setTextColor(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkViewSelectNoteTypeHabitTypeText
                else R.color.colorLightViewSelectNoteTypeHabitTypeText
            )
        )

        ending.setOnClickListener {
            hideSelectNoteTypeView()
        }

        noteType.setOnClickListener {
            hideSelectNoteTypeView(hideBackgroundImage = false)

            addPostBehavior.state =
                BottomSheetBehavior.STATE_EXPANDED

            binding.addPostBottomSheet.noteId = null

            binding.addPostBottomSheet.editText.setText("")
            binding.addPostBottomSheet.editText.requestFocus()

            selectedDiffPointToAddPost = 0
            binding.addPostBottomSheet.pointsDark.setSelectedIndex(1, false)
            binding.addPostBottomSheet.pointsLight.setSelectedIndex(1, false)

            binding.addPostBottomSheet.negativePointDark.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkMetricControlGroupInactiveText
                else R.color.colorLightMetricControlGroupInactiveText
            ))

            binding.addPostBottomSheet.neutralPointDark.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkMetricControlGroupActiveText
                else R.color.colorLightMetricControlGroupActiveText
            ))

            binding.addPostBottomSheet.positivePointDark.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkMetricControlGroupInactiveText
                else R.color.colorLightMetricControlGroupInactiveText
            ))

            binding.addPostBottomSheet.negativePointLight.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkMetricControlGroupInactiveText
                else R.color.colorLightMetricControlGroupInactiveText
            ))

            binding.addPostBottomSheet.neutralPointLight.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkMetricControlGroupActiveText
                else R.color.colorLightMetricControlGroupActiveText
            ))

            binding.addPostBottomSheet.positivePointLight.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkMetricControlGroupInactiveText
                else R.color.colorLightMetricControlGroupInactiveText
            ))

            mediaAdapter = AddPostMediaAdapter(context, arrayListOf(), glideRequestManager)
            binding.addPostBottomSheet.mediaRecycler.adapter = mediaAdapter

            tagsAdapter.createList(mutableListOf())
        }

        trackerType.setOnClickListener {
            hideSelectNoteTypeView(hideBackgroundImage = false)

            addTrackerBehavior.state =
                BottomSheetBehavior.STATE_EXPANDED

            binding.addTrackerBottomSheet.noteId = null

            binding.addTrackerBottomSheet.editText.setText("")
            binding.addTrackerBottomSheet.editText.requestFocus()

            selectedDiffPointToAddPost = 1

            tagsAdapter.createList(mutableListOf())
        }

        goalType.setOnClickListener {
            hideSelectNoteTypeView(hideBackgroundImage = false)

            addGoalBehavior.state =
                BottomSheetBehavior.STATE_EXPANDED

            binding.addGoalBottomSheet.noteId = null

            binding.addGoalBottomSheet.editText.setText("")
            binding.addGoalBottomSheet.editText.requestFocus()

            selectedDiffPointToAddPost = 1

            tagsAdapter.createList(mutableListOf())
        }

        habbitType.setOnClickListener {
            hideSelectNoteTypeView(hideBackgroundImage = false)

            addHabitBehavior.state =
                BottomSheetBehavior.STATE_EXPANDED

            binding.addHabitBottomSheet.noteId = null

            binding.addHabitBottomSheet.editText.setText("")
            binding.addHabitBottomSheet.editAmount.setText("")
            binding.addHabitBottomSheet.editText.requestFocus()

            selectedDiffPointToAddPost = 1

            tagsAdapter.createList(mutableListOf())
        }
    }
}

@SuppressLint("ClickableViewAccessibility")
fun MainActivity.setupAddPostBottomSheet() {
    val context = this

    with(binding.addPostBottomSheet) {

        runOnUiThread {

            val rtToolbarCharacter =
                rteToolbarContainer.findViewById<OwnHorizontalRTToolbar>(R.id.rte_toolbar)

            rtToolbarCharacter.findViewById<RTToolbarImageButton>(R.id.toolbar_bold).background =
                ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.ic_rte_bold_dark
                    else R.drawable.ic_rte_bold_light
                )

            rtToolbarCharacter.findViewById<RTToolbarImageButton>(R.id.toolbar_italic).background =
                ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.ic_rte_italic_dark
                    else R.drawable.ic_rte_italic_light
                )

            rtToolbarCharacter.findViewById<RTToolbarImageButton>(R.id.toolbar_underline).background =
                ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.ic_rte_underline_dark
                    else R.drawable.ic_rte_underline_light
                )

            rtToolbarCharacter.findViewById<RTToolbarImageButton>(R.id.toolbar_strikethrough).background =
                ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.ic_rte_strike_dark
                    else R.drawable.ic_rte_strike_light
                )

            rtToolbarCharacter.findViewById<RTToolbarImageButton>(R.id.toolbar_superscript).background =
                ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.ic_rte_superscript_dark
                    else R.drawable.ic_rte_superscript_light
                )

            rtToolbarCharacter.findViewById<RTToolbarImageButton>(R.id.toolbar_subscript).background =
                ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.ic_rte_subscript_dark
                    else R.drawable.ic_rte_subscript_light
                )

            rtToolbarCharacter.findViewById<RTToolbarImageButton>(R.id.toolbar_bullet).background =
                ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.ic_rte_list_bullet_dark
                    else R.drawable.ic_rte_list_bullet_light
                )

            rtToolbarCharacter.findViewById<RTToolbarImageButton>(R.id.toolbar_undo).background =
                ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.ic_rte_undo_dark
                    else R.drawable.ic_rte_undo_light
                )

            rtToolbarCharacter.findViewById<RTToolbarImageButton>(R.id.toolbar_redo).background =
                ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.ic_rte_redo_dark
                    else R.drawable.ic_rte_redo_light
                )

            rtToolbarCharacter.findViewById<RTToolbarImageButton>(R.id.toolbar_clear).background =
                ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.ic_rte_clear_dark
                    else R.drawable.ic_rte_clear_light
                )

            title.text = App.resourcesProvider.getStringLocale(R.string.add_post)
            editText.hint = App.resourcesProvider.getStringLocale(R.string.add_post_example)
            tvMessage.text = App.resourcesProvider.getStringLocale(R.string.save)

            container.background = ContextCompat.getDrawable(
                context,
                if (App.preferences.isDarkTheme) R.drawable.background_bottom_sheet_dark
                else R.drawable.background_bottom_sheet_light
            )

            viewHeader.background = ContextCompat.getDrawable(
                context,
                if (App.preferences.isDarkTheme) R.drawable.snack_neutral_gradient_dark
                else R.drawable.snack_neutral_gradient_light
            )

            editText.background = ContextCompat.getDrawable(
                context,
                if (App.preferences.isDarkTheme) R.drawable.bg_edittext_dark
                else R.drawable.bg_edittext_light
            )

            rteToolbarContainer.background = ContextCompat.getDrawable(
                context,
                if (App.preferences.isDarkTheme) R.drawable.bg_edittext_dark
                else R.drawable.bg_edittext_light
            )

            tagsContainer.background = ContextCompat.getDrawable(
                context,
                if (App.preferences.isDarkTheme) R.drawable.bg_edittext_dark
                else R.drawable.bg_edittext_light
            )

            background.background = ContextCompat.getDrawable(
                context,
                if (App.preferences.isDarkTheme) R.drawable.snack_neutral_gradient_dark
                else R.drawable.snack_neutral_gradient_light
            )

            title.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewPostAddTitleText
                    else R.color.colorLightViewPostAddTitleText
                )
            )

            interestName.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewPostAddTitleText
                    else R.color.colorLightViewPostAddTitleText
                )
            )

            editText.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewPostAddEditTextText
                    else R.color.colorLightViewPostAddEditTextText
                )
            )

            editText.setHintTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewPostAddEditTextHint
                    else R.color.colorLightViewPostAddEditTextHint
                )
            )

            addMedia.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewPostAddAddMediaBackgroundTint
                    else R.color.colorLightViewPostAddAddMediaBackgroundTint
                )
            )

            icAddMedia.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewPostAddAddMediaTint
                    else R.color.colorLightViewPostAddAddMediaTint
                )
            )

            date.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewPostAddDateText
                    else R.color.colorLightViewPostAddDateText
                )
            )

            length.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewPostAddLengthText
                    else R.color.colorLightViewPostAddLengthText
                )
            )

            tvMessage.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewPostAddTvMessageText
                    else R.color.colorLightViewPostAddTvMessageText
                )
            )

            negativePointDark.textSize = 12f
            neutralPointDark.textSize = 12f
            positivePointDark.textSize = 12f
            negativePointLight.textSize = 12f
            neutralPointLight.textSize = 12f
            positivePointLight.textSize = 12f

            pointsDark.isVisible = App.preferences.isDarkTheme
            pointsLight.isVisible = !App.preferences.isDarkTheme

            negativePointDark.setTextColor(ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.colorDarkMetricControlGroupInactiveText
                    else R.color.colorLightMetricControlGroupInactiveText
                ))

            neutralPointDark.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkMetricControlGroupActiveText
                else R.color.colorLightMetricControlGroupActiveText
            ))

            positivePointDark.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkMetricControlGroupInactiveText
                else R.color.colorLightMetricControlGroupInactiveText
            ))

            negativePointLight.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkMetricControlGroupInactiveText
                else R.color.colorLightMetricControlGroupInactiveText
            ))

            neutralPointLight.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkMetricControlGroupActiveText
                else R.color.colorLightMetricControlGroupActiveText
            ))

            positivePointLight.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkMetricControlGroupInactiveText
                else R.color.colorLightMetricControlGroupInactiveText
            ))

            pointsDark.setOnSelectedOptionChangeCallback {
                negativePointDark.setTextColor(ContextCompat.getColor(
                    context,
                    if (it == 0) {
                        if (App.preferences.isDarkTheme) R.color.colorDarkMetricControlGroupActiveText
                        else R.color.colorLightMetricControlGroupActiveText
                    } else {
                        if (App.preferences.isDarkTheme) R.color.colorDarkMetricControlGroupInactiveText
                        else R.color.colorLightMetricControlGroupInactiveText
                    }
                ))

                neutralPointDark.setTextColor(ContextCompat.getColor(
                    context,
                    if (it == 1) {
                        if (App.preferences.isDarkTheme) R.color.colorDarkMetricControlGroupActiveText
                        else R.color.colorLightMetricControlGroupActiveText
                    } else {
                        if (App.preferences.isDarkTheme) R.color.colorDarkMetricControlGroupInactiveText
                        else R.color.colorLightMetricControlGroupInactiveText
                    }
                ))

                positivePointDark.setTextColor(ContextCompat.getColor(
                    context,
                    if (it == 2) {
                        if (App.preferences.isDarkTheme) R.color.colorDarkMetricControlGroupActiveText
                        else R.color.colorLightMetricControlGroupActiveText
                    } else {
                        if (App.preferences.isDarkTheme) R.color.colorDarkMetricControlGroupInactiveText
                        else R.color.colorLightMetricControlGroupInactiveText
                    }
                ))

                selectedDiffPointToAddPost =
                    when(it) {
                        0 -> -1
                        1 -> 0
                        else -> 1
                    }
            }

            pointsLight.setOnSelectedOptionChangeCallback {
                negativePointLight.setTextColor(ContextCompat.getColor(
                    context,
                    if (it == 0) {
                        if (App.preferences.isDarkTheme) R.color.colorDarkMetricControlGroupActiveText
                        else R.color.colorLightMetricControlGroupActiveText
                    } else {
                        if (App.preferences.isDarkTheme) R.color.colorDarkMetricControlGroupInactiveText
                        else R.color.colorLightMetricControlGroupInactiveText
                    }
                ))

                neutralPointLight.setTextColor(ContextCompat.getColor(
                    context,
                    if (it == 1) {
                        if (App.preferences.isDarkTheme) R.color.colorDarkMetricControlGroupActiveText
                        else R.color.colorLightMetricControlGroupActiveText
                    } else {
                        if (App.preferences.isDarkTheme) R.color.colorDarkMetricControlGroupInactiveText
                        else R.color.colorLightMetricControlGroupInactiveText
                    }
                ))

                positivePointLight.setTextColor(ContextCompat.getColor(
                    context,
                    if (it == 2) {
                        if (App.preferences.isDarkTheme) R.color.colorDarkMetricControlGroupActiveText
                        else R.color.colorLightMetricControlGroupActiveText
                    } else {
                        if (App.preferences.isDarkTheme) R.color.colorDarkMetricControlGroupInactiveText
                        else R.color.colorLightMetricControlGroupInactiveText
                    }
                ))

                selectedDiffPointToAddPost =
                    when(it) {
                        0 -> -1
                        1 -> 0
                        else -> 1
                    }
            }

            binding.addPostBottomSheet.pointsDark.setSelectedIndex(1, false)
            binding.addPostBottomSheet.pointsLight.setSelectedIndex(1, false)

            GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
                userInterestsViewModel.getInterestsLiveData()
                    .observe(this@setupAddPostBottomSheet) { interests ->
                        if (!interests.isNullOrEmpty()) {
                            val itemCount = interests.size

                            icon.getRecycler().setItemViewCacheSize(interests.size)
                            icon.adapter.values = (0 until itemCount).map {
                                CustomWheelPickerView.Item(
                                    interests[it].interestId,
                                    ContextCompat.getDrawable(
                                        context,
                                        AllLogo().getLogoById(interests[it].logoId.toString())
                                    )
                                )
                            }

                            icon.getRecycler()
                                .post { icon.getRecycler().scrollToPosition(interests.size / 2) }
                            kotlin.runCatching { icon.adapter.notifyDataSetChanged() }
                        }
                    }

                icon.isHapticFeedbackEnabled = true
                icon.getRecycler().setOnTouchListener { v, event ->
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        (addPostBehavior as LockableBottomSheetBehavior).swipeEnabled =
                            false
                    } else if (event.action == MotionEvent.ACTION_UP) {
                        (addPostBehavior as LockableBottomSheetBehavior).swipeEnabled = true
                    }

                    false
                }

                icon.setWheelListener(object : BaseWheelPickerView.WheelPickerViewListener {
                    override fun didSelectItem(picker: BaseWheelPickerView, index: Int) {
                        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
                            interestName.text =
                                userInterestsViewModel.getInterestsByUserId().firstOrNull {
                                    it.interestId == icon.adapter.values.getOrNull(index)?.id
                                }?.name

                            selectedInterestIdToAddPost =
                                icon.adapter.values.getOrNull(index)?.id.toString()
                        }
                    }
                })
            }

            val currentDate = SimpleDateFormat(
                "dd MMMM, EEEE",
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) resources.configuration.locales[0]
                else resources.configuration.locale
            ).format(System.currentTimeMillis())
            date.text = currentDate

            editText.addTextChangedListener {
                length.text = editText.text?.toString()?.length.toString() + "/2000"
            }

            val lm = FlexboxLayoutManager(context)
            lm.flexDirection = FlexDirection.ROW
            lm.justifyContent = JustifyContent.FLEX_START

            tagsRecycler.layoutManager = lm

            tagsRecycler.adapter = tagsAdapter
            tagsAdapter.createList(mutableListOf())

            addPost.setOnClickListener {
                if (editText.text?.length == 0) {
                    showFail(getString(R.string.enter_note_text))
                } else if (!isMediaAdapterInitialized() || mediaAdapter.getMedia().size == 0) {
                    GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
                        val diaryNote = userDiaryViewModel.getNoteMediaById(noteId ?: "")
                        setDiaryNote(
                            noteId = noteId,
                            noteType = NoteType.Note.id,
                            mediaUrls = diaryNote?.media,
                            text = editText.getText(RTFormat.HTML),
                            date =
                            if (diaryNote?.date == null) System.currentTimeMillis()
                            else diaryNote.date
                        )
                    }
                } else uploadMedia(
                    noteId = noteId,
                    text = editText.getText(RTFormat.HTML),
                    date =
                    System.currentTimeMillis()

                )
            }

            addMedia.setOnClickListener {
                if (checkPermissionForReadExternalStorage()) {
                    openGallery()
                }
            }
        }
    }
}

@SuppressLint("ClickableViewAccessibility")
fun MainActivity.setupAddGoalBottomSheet() {
    val context = this
    with(binding.addGoalBottomSheet) {

        runOnUiThread {
            val rtToolbarCharacter =
                rteToolbarContainer.findViewById<OwnHorizontalRTToolbar>(R.id.rte_toolbar)

            rtToolbarCharacter.findViewById<RTToolbarImageButton>(R.id.toolbar_bold).background =
                ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.ic_rte_bold_dark
                    else R.drawable.ic_rte_bold_light
                )

            rtToolbarCharacter.findViewById<RTToolbarImageButton>(R.id.toolbar_italic).background =
                ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.ic_rte_italic_dark
                    else R.drawable.ic_rte_italic_light
                )

            rtToolbarCharacter.findViewById<RTToolbarImageButton>(R.id.toolbar_underline).background =
                ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.ic_rte_underline_dark
                    else R.drawable.ic_rte_underline_light
                )

            rtToolbarCharacter.findViewById<RTToolbarImageButton>(R.id.toolbar_strikethrough).background =
                ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.ic_rte_strike_dark
                    else R.drawable.ic_rte_strike_light
                )

            rtToolbarCharacter.findViewById<RTToolbarImageButton>(R.id.toolbar_superscript).background =
                ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.ic_rte_superscript_dark
                    else R.drawable.ic_rte_superscript_light
                )

            rtToolbarCharacter.findViewById<RTToolbarImageButton>(R.id.toolbar_subscript).background =
                ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.ic_rte_subscript_dark
                    else R.drawable.ic_rte_subscript_light
                )

            rtToolbarCharacter.findViewById<RTToolbarImageButton>(R.id.toolbar_bullet).background =
                ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.ic_rte_list_bullet_dark
                    else R.drawable.ic_rte_list_bullet_light
                )

            rtToolbarCharacter.findViewById<RTToolbarImageButton>(R.id.toolbar_undo).background =
                ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.ic_rte_undo_dark
                    else R.drawable.ic_rte_undo_light
                )

            rtToolbarCharacter.findViewById<RTToolbarImageButton>(R.id.toolbar_redo).background =
                ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.ic_rte_redo_dark
                    else R.drawable.ic_rte_redo_light
                )

            rtToolbarCharacter.findViewById<RTToolbarImageButton>(R.id.toolbar_clear).background =
                ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.ic_rte_clear_dark
                    else R.drawable.ic_rte_clear_light
                )

            title.text = App.resourcesProvider.getStringLocale(R.string.add_goal)
            editText.hint = App.resourcesProvider.getStringLocale(R.string.goal_title)
            tvMessage.text = App.resourcesProvider.getStringLocale(R.string.save)

            rteToolbarContainer.background = ContextCompat.getDrawable(
                context,
                if (App.preferences.isDarkTheme) R.drawable.bg_edittext_dark
                else R.drawable.bg_edittext_light
            )

            container.background = ContextCompat.getDrawable(
                context,
                if (App.preferences.isDarkTheme) R.drawable.background_bottom_sheet_dark
                else R.drawable.background_bottom_sheet_light
            )

            viewHeader.background = ContextCompat.getDrawable(
                context,
                if (App.preferences.isDarkTheme) R.drawable.snack_neutral_gradient_dark
                else R.drawable.snack_neutral_gradient_light
            )

            editText.background = ContextCompat.getDrawable(
                context,
                if (App.preferences.isDarkTheme) R.drawable.bg_edittext_dark
                else R.drawable.bg_edittext_light
            )

            background.background = ContextCompat.getDrawable(
                context,
                if (App.preferences.isDarkTheme) R.drawable.snack_neutral_gradient_dark
                else R.drawable.snack_neutral_gradient_light
            )

            interestName.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewPostAddTitleText
                    else R.color.colorLightViewPostAddTitleText
                )
            )

            title.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewGoalAddTitleText
                    else R.color.colorLightViewGoalAddTitleText
                )
            )

            editText.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewPostAddEditTextText
                    else R.color.colorLightViewPostAddEditTextText
                )
            )

            editText.setHintTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewPostAddEditTextHint
                    else R.color.colorLightViewPostAddEditTextHint
                )
            )

            date.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewGoalAddDateText
                    else R.color.colorLightViewGoalAddDateText
                )
            )

            length.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewGoalAddLengthText
                    else R.color.colorLightViewGoalAddLengthText
                )
            )

            tvMessage.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewPostAddTvMessageText
                    else R.color.colorLightViewPostAddTvMessageText
                )
            )

            tagsContainer.background = ContextCompat.getDrawable(
                context,
                if (App.preferences.isDarkTheme) R.drawable.bg_edittext_dark
                else R.drawable.bg_edittext_light
            )

            val lm = FlexboxLayoutManager(context)
            lm.flexDirection = FlexDirection.ROW
            lm.justifyContent = JustifyContent.FLEX_START

            tagsRecycler.layoutManager = lm

            tagsRecycler.adapter = tagsAdapter
            tagsAdapter.createList(mutableListOf())

            GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
                userInterestsViewModel.getInterestsLiveData()
                    .observe(this@setupAddGoalBottomSheet) { interests ->
                        if (!interests.isNullOrEmpty()) {
                            val itemCount = interests.size

                            icon.getRecycler().setItemViewCacheSize(interests.size)
                            icon.adapter.values = (0 until itemCount).map {
                                CustomWheelPickerView.Item(
                                    interests[it].interestId,
                                    ContextCompat.getDrawable(
                                        context,
                                        AllLogo().getLogoById(interests[it].logoId.toString())
                                    )
                                )
                            }

                            icon.getRecycler()
                                .post { icon.getRecycler().scrollToPosition(interests.size / 2) }
                            kotlin.runCatching { icon.adapter.notifyDataSetChanged() }
                        }
                    }

                icon.isHapticFeedbackEnabled = true
                icon.getRecycler().setOnTouchListener { v, event ->
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        (addGoalBehavior as LockableBottomSheetBehavior).swipeEnabled =
                            false
                    } else if (event.action == MotionEvent.ACTION_UP) {
                        (addGoalBehavior as LockableBottomSheetBehavior).swipeEnabled = true
                    }

                    false
                }

                icon.setWheelListener(object : BaseWheelPickerView.WheelPickerViewListener {
                    override fun didSelectItem(picker: BaseWheelPickerView, index: Int) {
                        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
                            interestName.text =
                                userInterestsViewModel.getInterestsByUserId().firstOrNull {
                                    it.interestId == icon.adapter.values.getOrNull(index)?.id
                                }?.name

                            selectedInterestIdToAddPost =
                                icon.adapter.values.getOrNull(index)?.id.toString()
                        }
                    }
                })
            }

            icon.setWheelListener(object : BaseWheelPickerView.WheelPickerViewListener {
                override fun didSelectItem(picker: BaseWheelPickerView, index: Int) {
                    GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
                        interestName.text = userInterestsViewModel.getInterestsByUserId()
                            .first { it.interestId == icon.adapter.values.getOrNull(index)?.id }.name

                        selectedInterestIdToAddPost =
                            icon.adapter.values.getOrNull(index)?.id.toString()
                    }
                }
            })

            val currentDate = SimpleDateFormat(
                "dd MMMM, EEEE",
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) resources.configuration.locales[0]
                else resources.configuration.locale
            ).format(System.currentTimeMillis())
            date.text = currentDate

            editText.addTextChangedListener {
                length.text = editText.text?.toString()?.length.toString() + "/30"
            }

            addPost.setOnClickListener {
                if (editText.text?.length == 0) {
                    showFail(getString(R.string.enter_note_text))
                } else
                    setDiaryNote(
                        noteId = noteId,
                        noteType = NoteType.Goal.id,
                        text = editText.getText(RTFormat.HTML),//editText.text.toString(),
                        date = System.currentTimeMillis()
                    )
            }
        }
    }
}

@SuppressLint("ClickableViewAccessibility")
fun MainActivity.setupAddTrackerBottomSheet() {
    val context = this
    with(binding.addTrackerBottomSheet) {

        runOnUiThread {
            val rtToolbarCharacter =
                rteToolbarContainer.findViewById<OwnHorizontalRTToolbar>(R.id.rte_toolbar)

            rtToolbarCharacter.findViewById<RTToolbarImageButton>(R.id.toolbar_bold).background =
                ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.ic_rte_bold_dark
                    else R.drawable.ic_rte_bold_light
                )

            rtToolbarCharacter.findViewById<RTToolbarImageButton>(R.id.toolbar_italic).background =
                ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.ic_rte_italic_dark
                    else R.drawable.ic_rte_italic_light
                )

            rtToolbarCharacter.findViewById<RTToolbarImageButton>(R.id.toolbar_underline).background =
                ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.ic_rte_underline_dark
                    else R.drawable.ic_rte_underline_light
                )

            rtToolbarCharacter.findViewById<RTToolbarImageButton>(R.id.toolbar_strikethrough).background =
                ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.ic_rte_strike_dark
                    else R.drawable.ic_rte_strike_light
                )

            rtToolbarCharacter.findViewById<RTToolbarImageButton>(R.id.toolbar_superscript).background =
                ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.ic_rte_superscript_dark
                    else R.drawable.ic_rte_superscript_light
                )

            rtToolbarCharacter.findViewById<RTToolbarImageButton>(R.id.toolbar_subscript).background =
                ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.ic_rte_subscript_dark
                    else R.drawable.ic_rte_subscript_light
                )

            rtToolbarCharacter.findViewById<RTToolbarImageButton>(R.id.toolbar_bullet).background =
                ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.ic_rte_list_bullet_dark
                    else R.drawable.ic_rte_list_bullet_light
                )

            rtToolbarCharacter.findViewById<RTToolbarImageButton>(R.id.toolbar_undo).background =
                ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.ic_rte_undo_dark
                    else R.drawable.ic_rte_undo_light
                )

            rtToolbarCharacter.findViewById<RTToolbarImageButton>(R.id.toolbar_redo).background =
                ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.ic_rte_redo_dark
                    else R.drawable.ic_rte_redo_light
                )

            rtToolbarCharacter.findViewById<RTToolbarImageButton>(R.id.toolbar_clear).background =
                ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.ic_rte_clear_dark
                    else R.drawable.ic_rte_clear_light
                )

            title.text = App.resourcesProvider.getStringLocale(R.string.add_tracker)
            editText.hint = App.resourcesProvider.getStringLocale(R.string.tracker_title)
            tvMessage.text = App.resourcesProvider.getStringLocale(R.string.save)

            rteToolbarContainer.background = ContextCompat.getDrawable(
                context,
                if (App.preferences.isDarkTheme) R.drawable.bg_edittext_dark
                else R.drawable.bg_edittext_light
            )

            container.background = ContextCompat.getDrawable(
                context,
                if (App.preferences.isDarkTheme) R.drawable.background_bottom_sheet_dark
                else R.drawable.background_bottom_sheet_light
            )

            viewHeader.background = ContextCompat.getDrawable(
                context,
                if (App.preferences.isDarkTheme) R.drawable.snack_neutral_gradient_dark
                else R.drawable.snack_neutral_gradient_light
            )

            editText.background = ContextCompat.getDrawable(
                context,
                if (App.preferences.isDarkTheme) R.drawable.bg_edittext_dark
                else R.drawable.bg_edittext_light
            )

            background.background = ContextCompat.getDrawable(
                context,
                if (App.preferences.isDarkTheme) R.drawable.snack_neutral_gradient_dark
                else R.drawable.snack_neutral_gradient_light
            )

            title.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewTrackerAddTitleText
                    else R.color.colorLightViewTrackerAddTitleText
                )
            )

            interestName.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewPostAddTitleText
                    else R.color.colorLightViewPostAddTitleText
                )
            )

            editText.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewPostAddEditTextText
                    else R.color.colorLightViewPostAddEditTextText
                )
            )

            editText.setHintTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewPostAddEditTextHint
                    else R.color.colorLightViewPostAddEditTextHint
                )
            )

            date.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewTrackerAddDateText
                    else R.color.colorLightViewTrackerAddDateText
                )
            )

            length.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewTrackerAddLengthText
                    else R.color.colorLightViewTrackerAddLengthText
                )
            )

            tvMessage.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewPostAddTvMessageText
                    else R.color.colorLightViewPostAddTvMessageText
                )
            )

            tagsContainer.background = ContextCompat.getDrawable(
                context,
                if (App.preferences.isDarkTheme) R.drawable.bg_edittext_dark
                else R.drawable.bg_edittext_light
            )

            val lm = FlexboxLayoutManager(context)
            lm.flexDirection = FlexDirection.ROW
            lm.justifyContent = JustifyContent.FLEX_START

            tagsRecycler.layoutManager = lm

            tagsRecycler.adapter = tagsAdapter
            tagsAdapter.createList(mutableListOf())

            GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
                userInterestsViewModel.getInterestsLiveData()
                    .observe(this@setupAddTrackerBottomSheet) { interests ->
                        if (!interests.isNullOrEmpty()) {
                            val itemCount = interests.size

                            icon.getRecycler().setItemViewCacheSize(interests.size)
                            icon.adapter.values = (0 until itemCount).map {
                                CustomWheelPickerView.Item(
                                    interests[it].interestId,
                                    ContextCompat.getDrawable(
                                        context,
                                        AllLogo().getLogoById(interests[it].logoId.toString())
                                    )
                                )
                            }

                            icon.getRecycler()
                                .post { icon.getRecycler().scrollToPosition(interests.size / 2) }
                            kotlin.runCatching { icon.adapter.notifyDataSetChanged() }
                        }
                    }

                icon.isHapticFeedbackEnabled = true
                icon.getRecycler().setOnTouchListener { v, event ->
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        (addTrackerBehavior as LockableBottomSheetBehavior).swipeEnabled =
                            false
                    } else if (event.action == MotionEvent.ACTION_UP) {
                        (addTrackerBehavior as LockableBottomSheetBehavior).swipeEnabled = true
                    }

                    false
                }

                icon.setWheelListener(object : BaseWheelPickerView.WheelPickerViewListener {
                    override fun didSelectItem(picker: BaseWheelPickerView, index: Int) {
                        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
                            interestName.text =
                                userInterestsViewModel.getInterestsByUserId().firstOrNull {
                                    it.interestId == icon.adapter.values.getOrNull(index)?.id
                                }?.name

                            selectedInterestIdToAddPost =
                                icon.adapter.values.getOrNull(index)?.id.toString()
                        }
                    }
                })
            }

            val currentDate = SimpleDateFormat(
                "dd MMMM, EEEE",
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) resources.configuration.locales[0]
                else resources.configuration.locale
            ).format(System.currentTimeMillis())
            date.text = currentDate

            editText.addTextChangedListener {
                length.text = editText.text?.toString()?.length.toString() + "/30"
            }

            addPost.setOnClickListener {
                if (editText.text?.length == 0) {
                    showFail(getString(R.string.enter_note_text))
                } else {
                    GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
                        val activeTracker = userDiaryViewModel.getActiveTracker()

                        if (activeTracker == null) {
                            setDiaryNote(
                                noteId = noteId,
                                noteType = NoteType.Tracker.id,
                                text = editText.getText(RTFormat.HTML),//editText.text.toString(),
                                date =
                                if (activeTracker?.date == null) System.currentTimeMillis()
                                else activeTracker.date,
                                datetimeStart = System.currentTimeMillis(),
                                isActiveNow = true
                            )
                        } else {
                            showFail(getString(R.string.warning_only_1_active_tracker))
                        }
                    }
                }
            }
        }
    }
}

fun MainActivity.setupTrackerSheet() {

    runOnUiThread { binding.trackerSheet.setFab(binding.trackerFab) }

    binding.trackerFab.setOnClickListener {
        runOnUiThread {
            binding.trackerSheet.expandFab()
            binding.trackerFab.isVisible = false
        }
    }

    val context = this

    runOnUiThread {
        userDiaryViewModel.activeTrackerLiveData.observe(this) { activeTracker ->
            runOnUiThread {
                binding.trackerFab.isVisible = activeTracker != null && activeTracker.isActiveNow!!
            }

            if (isActiveTrackerTimerInitialized()) {
                activeTrackerTimer.cancel()
            }

            if (activeTracker == null && binding.trackerSheet.isFabExpanded) {
                runOnUiThread { binding.trackerSheet.contractFab() }

                EventBus.getDefault().post(ChangeProgressStateEvent(false))
            }

            if (App.preferences.uid.isNullOrEmpty()) {
                runOnUiThread {
                    binding.trackerSheet.contractFab()
                    binding.trackerFab.isVisible = false
                }
            }

            binding.trackerSheet.setFabAnimationEndListener {
                runOnUiThread {
                    binding.trackerFab.isVisible =
                        activeTracker != null && activeTracker.isActiveNow!!
                }
            }

            if (activeTracker != null) {
                binding.stopTracker.setOnClickListener {
                    userDiaryViewModel.changeTrackerState(
                        activeTracker,
                        false
                    )

                    activeTrackerTimer.cancel()
                }

                runOnUiThread {
                    binding.trackerTitle.text = Html.fromHtml(activeTracker.text ?: "")
                    binding.trackerDate.text =
                        SimpleDateFormat("dd MMM, HH:mm", Locale(App.preferences.locale)).format(
                            activeTracker.date.toLong()
                        )
                    binding.trackerInterestName.text = activeTracker.interest!!.interestName

                    binding.trackerIcon.setImageDrawable(
                        AppCompatResources.getDrawable(
                            context,
                            AllLogo().getLogoById(activeTracker.interest!!.interestIcon)
                        )
                    )
                }

                activeTrackerTimer = object : CountDownTimer(20000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        if (activeTracker == null) cancel()

                        val trackerTime =
                            System.currentTimeMillis() - activeTracker.datetimeStart!!.toLong()

                        val hours = (trackerTime / (1000 * 60 * 60)).toInt()
                        val minutes = ((trackerTime / (1000 * 60)) % 60).toInt()
                        val seconds = (trackerTime / 1000) % 60

                        runOnUiThread {
                            binding.timer.text = String.format(
                                "%02d:%02d:%02d",
                                hours, minutes, seconds
                            )
                        }
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
}

@SuppressLint("ClickableViewAccessibility")
fun MainActivity.setupAddHabitBottomSheet() {
    val context = this
    with(binding.addHabitBottomSheet) {

        runOnUiThread {
            val rtToolbarCharacter =
                rteToolbarContainer.findViewById<OwnHorizontalRTToolbar>(R.id.rte_toolbar)

            rtToolbarCharacter.findViewById<RTToolbarImageButton>(R.id.toolbar_bold).background =
                ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.ic_rte_bold_dark
                    else R.drawable.ic_rte_bold_light
                )

            rtToolbarCharacter.findViewById<RTToolbarImageButton>(R.id.toolbar_italic).background =
                ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.ic_rte_italic_dark
                    else R.drawable.ic_rte_italic_light
                )

            rtToolbarCharacter.findViewById<RTToolbarImageButton>(R.id.toolbar_underline).background =
                ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.ic_rte_underline_dark
                    else R.drawable.ic_rte_underline_light
                )

            rtToolbarCharacter.findViewById<RTToolbarImageButton>(R.id.toolbar_strikethrough).background =
                ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.ic_rte_strike_dark
                    else R.drawable.ic_rte_strike_light
                )

            rtToolbarCharacter.findViewById<RTToolbarImageButton>(R.id.toolbar_superscript).background =
                ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.ic_rte_superscript_dark
                    else R.drawable.ic_rte_superscript_light
                )

            rtToolbarCharacter.findViewById<RTToolbarImageButton>(R.id.toolbar_subscript).background =
                ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.ic_rte_subscript_dark
                    else R.drawable.ic_rte_subscript_light
                )

            rtToolbarCharacter.findViewById<RTToolbarImageButton>(R.id.toolbar_bullet).background =
                ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.ic_rte_list_bullet_dark
                    else R.drawable.ic_rte_list_bullet_light
                )

            rtToolbarCharacter.findViewById<RTToolbarImageButton>(R.id.toolbar_undo).background =
                ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.ic_rte_undo_dark
                    else R.drawable.ic_rte_undo_light
                )

            rtToolbarCharacter.findViewById<RTToolbarImageButton>(R.id.toolbar_redo).background =
                ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.ic_rte_redo_dark
                    else R.drawable.ic_rte_redo_light
                )

            rtToolbarCharacter.findViewById<RTToolbarImageButton>(R.id.toolbar_clear).background =
                ContextCompat.getDrawable(
                    context,
                    if (App.preferences.isDarkTheme) R.drawable.ic_rte_clear_dark
                    else R.drawable.ic_rte_clear_light
                )

            title.text = App.resourcesProvider.getStringLocale(R.string.add_habit)
            editText.hint = App.resourcesProvider.getStringLocale(R.string.goal_title)
            editAmount.hint = App.resourcesProvider.getStringLocale(R.string.habit_repeat_amount)

            dailyPointLight.text = App.resourcesProvider.getStringLocale(R.string.habit_every_day)
            dailyPointDark.text = App.resourcesProvider.getStringLocale(R.string.habit_every_day)
            weeklyPointLight.text = App.resourcesProvider.getStringLocale(R.string.habit_every_week)
            weeklyPointDark.text = App.resourcesProvider.getStringLocale(R.string.habit_every_week)

            tvMessage.text = App.resourcesProvider.getStringLocale(R.string.save)

            rteToolbarContainer.background = ContextCompat.getDrawable(
                context,
                if (App.preferences.isDarkTheme) R.drawable.bg_edittext_dark
                else R.drawable.bg_edittext_light
            )

            container.background = ContextCompat.getDrawable(
                context,
                if (App.preferences.isDarkTheme) R.drawable.background_bottom_sheet_dark
                else R.drawable.background_bottom_sheet_light
            )

            viewHeader.background = ContextCompat.getDrawable(
                context,
                if (App.preferences.isDarkTheme) R.drawable.snack_neutral_gradient_dark
                else R.drawable.snack_neutral_gradient_light
            )

            editText.background = ContextCompat.getDrawable(
                context,
                if (App.preferences.isDarkTheme) R.drawable.bg_edittext_dark
                else R.drawable.bg_edittext_light
            )

            editAmount.background = ContextCompat.getDrawable(
                context,
                if (App.preferences.isDarkTheme) R.drawable.bg_edittext_dark
                else R.drawable.bg_edittext_light
            )

            background.background = ContextCompat.getDrawable(
                context,
                if (App.preferences.isDarkTheme) R.drawable.snack_neutral_gradient_dark
                else R.drawable.snack_neutral_gradient_light
            )

            title.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewHabitAddTitleText
                    else R.color.colorLightViewHabitAddTitleText
                )
            )

            interestName.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewPostAddTitleText
                    else R.color.colorLightViewPostAddTitleText
                )
            )

            editText.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewPostAddEditTextText
                    else R.color.colorLightViewPostAddEditTextText
                )
            )

            editText.setHintTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewPostAddEditTextHint
                    else R.color.colorLightViewPostAddEditTextHint
                )
            )

            editAmount.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewPostAddEditTextText
                    else R.color.colorLightViewPostAddEditTextText
                )
            )

            editAmount.setHintTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewPostAddEditTextHint
                    else R.color.colorLightViewPostAddEditTextHint
                )
            )

            dailyPointLight.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewHabitAddDailyPointText
                    else R.color.colorLightViewHabitAddDailyPointText
                )
            )

            dailyPointDark.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewHabitAddDailyPointText
                    else R.color.colorLightViewHabitAddDailyPointText
                )
            )

            weeklyPointLight.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewHabitAddWeeklyPointText
                    else R.color.colorLightViewHabitAddWeeklyPointText
                )
            )

            weeklyPointDark.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewHabitAddWeeklyPointText
                    else R.color.colorLightViewHabitAddWeeklyPointText
                )
            )

            date.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewHabitAddDateText
                    else R.color.colorLightViewHabitAddDateText
                )
            )

            length.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewHabitAddLengthText
                    else R.color.colorLightViewHabitAddLengthText
                )
            )

            tvMessage.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewPostAddTvMessageText
                    else R.color.colorLightViewPostAddTvMessageText
                )
            )

            tagsContainer.background = ContextCompat.getDrawable(
                context,
                if (App.preferences.isDarkTheme) R.drawable.bg_edittext_dark
                else R.drawable.bg_edittext_light
            )

            val lm = FlexboxLayoutManager(context)
            lm.flexDirection = FlexDirection.ROW
            lm.justifyContent = JustifyContent.FLEX_START

            tagsRecycler.layoutManager = lm

            tagsRecycler.adapter = tagsAdapter
            tagsAdapter.createList(mutableListOf())

            regularityControlGroupDark.isVisible = App.preferences.isDarkTheme
            regularityControlGroupLight.isVisible = !App.preferences.isDarkTheme

            GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
                userInterestsViewModel.getInterestsLiveData()
                    .observe(this@setupAddHabitBottomSheet) { interests ->
                        if (!interests.isNullOrEmpty()) {
                            val itemCount = interests.size

                            icon.getRecycler().setItemViewCacheSize(interests.size)
                            icon.adapter.values = (0 until itemCount).map {
                                CustomWheelPickerView.Item(
                                    interests[it].interestId,
                                    ContextCompat.getDrawable(
                                        context,
                                        AllLogo().getLogoById(interests[it].logoId.toString())
                                    )
                                )
                            }

                            icon.getRecycler()
                                .post { icon.getRecycler().scrollToPosition(interests.size / 2) }
                            kotlin.runCatching { icon.adapter.notifyDataSetChanged() }
                        }
                    }

                icon.isHapticFeedbackEnabled = true
                icon.getRecycler().setOnTouchListener { v, event ->
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        (addHabitBehavior as LockableBottomSheetBehavior).swipeEnabled =
                            false
                    } else if (event.action == MotionEvent.ACTION_UP) {
                        (addHabitBehavior as LockableBottomSheetBehavior).swipeEnabled = true
                    }

                    false
                }

                icon.setWheelListener(object : BaseWheelPickerView.WheelPickerViewListener {
                    override fun didSelectItem(picker: BaseWheelPickerView, index: Int) {
                        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
                            interestName.text =
                                userInterestsViewModel.getInterestsByUserId().firstOrNull {
                                    it.interestId == icon.adapter.values.getOrNull(index)?.id
                                }?.name

                            selectedInterestIdToAddPost =
                                icon.adapter.values.getOrNull(index)?.id.toString()
                        }
                    }
                })
            }

            val currentDate = SimpleDateFormat(
                "dd MMMM, EEEE",
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) resources.configuration.locales[0]
                else resources.configuration.locale
            ).format(Calendar.getInstance().timeInMillis)
            date.text = currentDate

            editText.addTextChangedListener {
                length.text = editText.text?.toString()?.length.toString() + "/30"
            }

            regularityControlGroupLight.setOnSelectedOptionChangeCallback {
                selectedRegularityToAddHabit = when (it) {
                    0 -> Regularity.Daily
                    else -> Regularity.Weekly
                }

                dailyPointLight.setTextColor(
                    ContextCompat.getColor(
                        context,
                        if (it == 0)
                            if (App.preferences.isDarkTheme) R.color.colorDarkAddHabitRegularityPointActiveText
                            else R.color.colorLightAddHabitRegularityPointActiveText
                        else
                            if (App.preferences.isDarkTheme) R.color.colorDarkAddHabitRegularityPointInactiveText
                            else R.color.colorLightAddHabitRegularityPointInactiveText
                    )
                )

                weeklyPointLight.setTextColor(
                    ContextCompat.getColor(
                        context,
                        if (it == 1)
                            if (App.preferences.isDarkTheme) R.color.colorDarkAddHabitRegularityPointActiveText
                            else R.color.colorLightAddHabitRegularityPointActiveText
                        else
                            if (App.preferences.isDarkTheme) R.color.colorDarkAddHabitRegularityPointInactiveText
                            else R.color.colorLightAddHabitRegularityPointInactiveText
                    )
                )
            }

            regularityControlGroupDark.setOnSelectedOptionChangeCallback {
                selectedRegularityToAddHabit = when (it) {
                    0 -> Regularity.Daily
                    else -> Regularity.Weekly
                }

                dailyPointDark.setTextColor(
                    ContextCompat.getColor(
                        context,
                        if (it == 0)
                            if (App.preferences.isDarkTheme) R.color.colorDarkAddHabitRegularityPointActiveText
                            else R.color.colorLightAddHabitRegularityPointActiveText
                        else
                            if (App.preferences.isDarkTheme) R.color.colorDarkAddHabitRegularityPointInactiveText
                            else R.color.colorLightAddHabitRegularityPointInactiveText
                    )
                )

                weeklyPointDark.setTextColor(
                    ContextCompat.getColor(
                        context,
                        if (it == 1)
                            if (App.preferences.isDarkTheme) R.color.colorDarkAddHabitRegularityPointActiveText
                            else R.color.colorLightAddHabitRegularityPointActiveText
                        else
                            if (App.preferences.isDarkTheme) R.color.colorDarkAddHabitRegularityPointInactiveText
                            else R.color.colorLightAddHabitRegularityPointInactiveText
                    )
                )
            }


            regularityControlGroupLight.setOnSelectedOptionChangeCallback {
                selectedRegularityToAddHabit = when (it) {
                    0 -> Regularity.Daily
                    else -> Regularity.Weekly
                }

                dailyPointLight.setTextColor(
                    ContextCompat.getColor(
                        context,
                        if (it == 0)
                            if (App.preferences.isDarkTheme) R.color.colorDarkAddHabitRegularityPointActiveText
                            else R.color.colorLightAddHabitRegularityPointActiveText
                        else
                            if (App.preferences.isDarkTheme) R.color.colorDarkAddHabitRegularityPointInactiveText
                            else R.color.colorLightAddHabitRegularityPointInactiveText
                    )
                )

                weeklyPointLight.setTextColor(
                    ContextCompat.getColor(
                        context,
                        if (it == 1)
                            if (App.preferences.isDarkTheme) R.color.colorDarkAddHabitRegularityPointActiveText
                            else R.color.colorLightAddHabitRegularityPointActiveText
                        else
                            if (App.preferences.isDarkTheme) R.color.colorDarkAddHabitRegularityPointInactiveText
                            else R.color.colorLightAddHabitRegularityPointInactiveText
                    )
                )
            }
            dailyPointLight.textSize = 10f
            dailyPointDark.textSize = 10f
            weeklyPointLight.textSize = 10f
            weeklyPointDark.textSize = 10f

            addPost.setOnClickListener {
                when {
                    editText.text?.length == 0 -> {
                        showFail(getString(R.string.enter_note_text))
                    }
                    editAmount.text?.length == 0
                            || editAmount.text.toString().toInt() == 0 -> {
                        showFail(getString(R.string.warning_edit_repeat_amount))
                    }
                    else -> {
                        setDiaryNote(
                            noteId = noteId,
                            noteType = NoteType.Habit.id,
                            text = editText.getText(RTFormat.HTML),
                            date = System.currentTimeMillis(),
                            datetimeStart = System.currentTimeMillis(),
                            regularity = selectedRegularityToAddHabit.id,
                            initialAmount = editAmount.text.toString().toInt(),
                            datesCompletion = generateDatesCompletion(
                                selectedRegularityToAddHabit,
                                editAmount.text.toString().toInt()
                            ),
                            isActiveNow = true
                        )
                    }
                }
            }
        }
    }
}

fun generateDatesCompletion(
    regularity: Regularity,
    amount: Int
): ArrayList<DiaryNoteDatesCompletion> {
    val datesCompletion = arrayListOf<DiaryNoteDatesCompletion>()
    val time = System.currentTimeMillis()

    for (i in 0 until amount) {
        datesCompletion.add(
            DiaryNoteDatesCompletion(
                datesCompletionDatetime = (
                        time + i * when (regularity) {
                            Regularity.Daily -> Milliseconds.Day.mills
                            else -> Milliseconds.Week.mills
                        }
                        ),
                datesCompletionIsCompleted = false
            )
        )
    }

    return datesCompletion
}