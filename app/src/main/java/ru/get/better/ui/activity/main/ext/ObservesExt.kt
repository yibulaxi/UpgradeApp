package ru.get.better.ui.activity.main.ext

import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.greenrobot.eventbus.EventBus
import ru.get.better.R
import ru.get.better.event.ChangeProgressStateEvent
import ru.get.better.ui.activity.main.MainActivity

fun MainActivity.observeSetDiaryNote(isSuccess: Boolean) {
    if (isSuccess) {
        showSuccess(getString(R.string.note_created))
        binding.addPostBottomSheet.editText.text?.clear()

        runOnUiThread {

            if (addPostBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                addPostBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

            if (addGoalBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                addGoalBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

            if (addTrackerBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                addTrackerBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

            if (addHabitBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                addHabitBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        EventBus.getDefault().post(ChangeProgressStateEvent(isActive = false))
    } else showFail(getString(ru.get.better.R.string.error_happened))
}