package ru.get.better.vm

import ru.get.better.App
import ru.get.better.BuildConfig
import ru.get.better.repo.nasa.AffirmationsRepoImpl
import ru.get.better.util.ViewState
import ru.get.better.util.ext.mutableLiveDataOf
import javax.inject.Inject

class AffirmationsViewModel @Inject constructor(
    private val affirmationRepo: AffirmationsRepoImpl,
) : BaseViewModel() {

    val nasaDataViewState =
        mutableLiveDataOf<ViewState<NasaDataViewStateData>>(ViewState.Idle)

    fun loadTodayNasaData() {
        affirmationRepo.getAffirmations()
            .manageLoading(nasaDataViewState)
            .subscribe({
                val affirmation = it[(it.indices).random()]

                nasaDataViewState.postValue(
                    ViewState.Data(
                        NasaDataViewStateData(
                            imgUrl = BuildConfig.AFFIRMATIONS_BASE_URL + "/affirmations/" +
                                    if (App.preferences.isDarkTheme) affirmation.imageD
                                    else affirmation.imageL,
                            justLoaded = true
                        )
                    )
                )
            }, {

            }).disposeOnCleared()

    }

    data class NasaDataViewStateData(
        val imgUrl: String,
        var justLoaded: Boolean
    )
}