package ru.get.better.util


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import ru.get.better.util.ext.doBeforeTerminate

open class RxViewModel : ViewModel() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun Disposable.disposeOnCleared(): Disposable {
        compositeDisposable.add(this)
        return this
    }

    fun <T, V> Single<T>.manageLoading(liveData: MutableLiveData<ViewState<V>>) =
        this.doOnSubscribe { liveData.value = ViewState.ShowProgress }
            .doBeforeTerminate { liveData.value = ViewState.HideProgress }

    fun <V> Completable.manageLoading(liveData: MutableLiveData<ViewState<V>>) =
        this.doOnSubscribe { liveData.value = ViewState.ShowProgress }
            .doBeforeTerminate { liveData.value = ViewState.HideProgress }

    fun <T, V> Observable<T>.manageLoading(liveData: MutableLiveData<ViewState<V>>) =
        this.doOnSubscribe { liveData.value = ViewState.ShowProgress }
            .doBeforeTerminate { liveData.value = ViewState.HideProgress }

    fun <T> Single<T>.manageProgress(liveData: MutableLiveData<ProgressState>) =
        this.doOnSubscribe { liveData.value = ProgressState.ShowProgress }
            .doBeforeTerminate { liveData.value = ProgressState.HideProgress }

    fun Completable.manageProgress(liveData: MutableLiveData<ProgressState>) =
        this.doOnSubscribe { liveData.value = ProgressState.ShowProgress }
            .doBeforeTerminate { liveData.value = ProgressState.HideProgress }

    fun <T> Observable<T>.manageProgress(liveData: MutableLiveData<ProgressState>) =
        this.doOnSubscribe { liveData.value = ProgressState.ShowProgress }
            .doBeforeTerminate { liveData.value = ProgressState.HideProgress }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

}