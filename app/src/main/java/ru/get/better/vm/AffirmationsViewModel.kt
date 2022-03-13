package ru.get.better.vm

import ru.get.better.repo.nasa.NasaRepoImpl
import ru.get.better.util.ViewState
import ru.get.better.util.ext.mutableLiveDataOf
import javax.inject.Inject

class AffirmationsViewModel @Inject constructor(
    private val nasaRepo: NasaRepoImpl,
) : BaseViewModel() {

    val nasaDataViewState =
        mutableLiveDataOf<ViewState<NasaDataViewStateData>>(ViewState.Idle)

     fun loadTodayNasaData() {
        nasaRepo.getSpaceImg()
            .manageLoading(nasaDataViewState)
            .subscribe({
                nasaDataViewState.postValue(
                    ViewState.Data(
                        NasaDataViewStateData(
                            imgUrl = it.url,
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