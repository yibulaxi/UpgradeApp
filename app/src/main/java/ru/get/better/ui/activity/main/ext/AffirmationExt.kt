package ru.get.better.ui.activity.main.ext

import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.view_affirmation.view.*
import ru.get.better.App
import ru.get.better.model.getTodayAffirmation
import ru.get.better.ui.activity.main.MainActivity
import ru.get.better.util.ViewState
import ru.get.better.util.doOn
import ru.get.better.util.ext.scaleXY
import ru.get.better.vm.AffirmationsViewModel

fun MainActivity.observeNasaData(
    nasaDataViewStateData: ViewState<AffirmationsViewModel.NasaDataViewStateData>
) {
    nasaDataViewStateData.doOn(
        showProgress = {},
        hideProgress = {},
        data = { nasaData ->
            affirmationIconUrl = nasaData.imgUrl
            setupAffirmationIcon()
        }
    )
}

private fun MainActivity.setupAffirmationIcon() {
    if (isAffirmationIconUrlInitialized())
        glideRequestManager.load(affirmationIconUrl).into(binding.affirmationView.affirmationIcon)
}

private fun MainActivity.showAffirmation() {
    binding.affirmationView.affirmationTitle.text = this.getTodayAffirmation().title

    binding.affirmationView.affirmationDesc.isVisible = !this.getTodayAffirmation().desc.isNullOrEmpty()
    binding.affirmationView.affirmationDesc.text = this.getTodayAffirmation().desc

    binding.affirmationView.affirmationContainer.isVisible = true
    binding.affirmationView.affirmationContainer.scaleXY(1f, 1f, 500) {
        binding.affirmationView.affirmationBlur.isVisible = true
    }

    binding.affirmationView.affirmationCross.setOnClickListener {
        binding.affirmationView.affirmationBlur.isVisible = false
        binding.affirmationView.affirmationContainer.scaleXY(0f, 0f, 500) {
            binding.affirmationView.affirmationContainer.isVisible = false
        }
    }

    binding.affirmationView.affirmationBlur.setOnClickListener {
        binding.affirmationView.affirmationBlur.isVisible = false
        binding.affirmationView.affirmationContainer.scaleXY(0f, 0f, 500) {
            binding.affirmationView.affirmationContainer.isVisible = false
        }
    }
}

fun MainActivity.setupAffirmation(isIncreaseNumber: Boolean) {
    if (isIncreaseNumber) {
        if (App.preferences.lastDayShownAffirmationMills == 0L) {
            App.preferences.lastDayShownAffirmationMills = System.currentTimeMillis() / 86400000
            showAffirmation()

            return
        }

        val todayMills = System.currentTimeMillis() / 86400000

        if (App.preferences.lastDayShownAffirmationMills != todayMills) {
            App.preferences.currentAffirmationNumber ++
            App.preferences.lastDayShownAffirmationMills = todayMills
            showAffirmation()
        }
    } else {
        showAffirmation()
    }

}