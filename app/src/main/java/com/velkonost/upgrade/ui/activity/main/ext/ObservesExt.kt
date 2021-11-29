package com.velkonost.upgrade.ui.activity.main.ext

import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.velkonost.upgrade.R
import com.velkonost.upgrade.event.UserSettingsReadyEvent
import com.velkonost.upgrade.ui.activity.main.MainActivity
import org.greenrobot.eventbus.EventBus

fun MainActivity.observeUserSettings(isUserSettingsReady: Boolean) {
    EventBus.getDefault().post(UserSettingsReadyEvent(isUserSettingsReady))
}

fun MainActivity.observeSetDiaryNote(isSuccess: Boolean) {
    if (isSuccess) {
        showSuccess(getString(R.string.note_created))
        binding.addPostBottomSheet.editText.text?.clear()

        if (addPostBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
            addPostBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        if (addGoalBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
            addGoalBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    } else showFail(getString(com.velkonost.upgrade.R.string.error_happened))
}