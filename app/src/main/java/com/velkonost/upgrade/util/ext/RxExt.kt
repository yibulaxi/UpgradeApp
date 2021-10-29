package com.velkonost.upgrade.util.ext

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

fun <T> Observable<T>.subscribeIoObserveMain(): Observable<T> {
    return subscribeOnIo()
        .observeOnMainThread()
}

fun <T> Observable<T>.subscribeOnIo(): Observable<T> = subscribeOn(Schedulers.io())

fun <T> Observable<T>.observeOnMainThread(): Observable<T> =
    observeOn(AndroidSchedulers.mainThread())

fun <T> Single<T>.subscribeIoObserveMain(): Single<T> {
    return subscribeOnIo()
        .observeOnMainThread()
}

fun <T> Single<T>.subscribeComputationObserveMain(): Single<T> {
    return subscribeOnComputation()
        .observeOnMainThread()
}

fun <T> Single<T>.subscribeOnIo(): Single<T> = subscribeOn(Schedulers.io())

fun <T> Single<T>.subscribeOnComputation(): Single<T> = subscribeOn(Schedulers.computation())

fun <T> Single<T>.observeOnMainThread(): Single<T> = observeOn(AndroidSchedulers.mainThread())

fun Completable.subscribeIoObserveMain(): Completable {
    return subscribeOnIo()
        .observeOnMainThread()
}

fun Completable.subscribeOnIo(): Completable = subscribeOn(Schedulers.io())

fun Completable.observeOnMainThread(): Completable = observeOn(AndroidSchedulers.mainThread())
fun Completable.observeOnIo(): Completable = observeOn(Schedulers.io())

fun createDelay(value: Long = 1, timeUnit: TimeUnit = TimeUnit.MINUTES) =
    Observable.just(Unit)
        .delay(value, timeUnit)!!

fun Disposable.addTo(disposable: CompositeDisposable) {
    disposable.add(this)
}

fun <T> Single<T>.doBeforeTerminate(doBefore: () -> Unit) =
    doOnSuccess { doBefore.invoke() }.doOnError { doBefore.invoke() }
        .doOnDispose { doBefore.invoke() }

fun <T> Observable<T>.doBeforeTerminate(doBefore: () -> Unit) =
    doOnComplete { doBefore.invoke() }.doOnError { doBefore.invoke() }
        .doOnDispose { doBefore.invoke() }!!

fun Completable.doBeforeTerminate(doBefore: () -> Unit) =
    doOnComplete { doBefore.invoke() }.doOnError { doBefore.invoke() }
        .doOnDispose { doBefore.invoke() }!!


fun Disposable.disposeWhen(lifecycleOwner: LifecycleOwner, disposeEvent: Lifecycle.Event) {
    val observer = object : DisposeObserver {
        override fun onPause() {
            checkDispose(Lifecycle.Event.ON_PAUSE)
        }

        override fun onStop() {
            checkDispose(Lifecycle.Event.ON_STOP)
        }

        override fun onDestroy() {
            checkDispose(Lifecycle.Event.ON_DESTROY)
        }

        override fun checkDispose(currentEvent: Lifecycle.Event) {
            if (currentEvent == disposeEvent) {
                lifecycleOwner.lifecycle.removeObserver(this)
                this@disposeWhen.dispose()
            }
        }

    }
    lifecycleOwner.lifecycle.addObserver(observer)
}

fun Disposable.disposeWhenDestroy(lifecycleOwner: LifecycleOwner) {
    disposeWhen(lifecycleOwner, Lifecycle.Event.ON_DESTROY)
}

interface DisposeObserver : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause()

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop()

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy()

    fun checkDispose(currentEvent: Lifecycle.Event)
}

fun <T> Observable<T>.disposeWhen(
    targetFragment: Fragment,
    disposeEvent: LifecycleEvent
): Observable<T> {
    val composite = CompositeDisposable()

    targetFragment.fragmentManager?.registerFragmentLifecycleCallbacks(object :
        FragmentManager.FragmentLifecycleCallbacks() {

        override fun onFragmentPaused(fm: FragmentManager, f: Fragment) {
            super.onFragmentPaused(fm, f)
            checkFragmentLifecycleCallbacks(fm, LifecycleEvent.PAUSED, f)
        }

        override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
            super.onFragmentStopped(fm, f)
            checkFragmentLifecycleCallbacks(fm, LifecycleEvent.STOPPED, f)
        }

        override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
            super.onFragmentViewDestroyed(fm, f)
            checkFragmentLifecycleCallbacks(fm, LifecycleEvent.VIEW_DESTROYED, f)
        }

        override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
            super.onFragmentDestroyed(fm, f)
            checkFragmentLifecycleCallbacks(fm, LifecycleEvent.DESTROYED, f)
        }

        fun checkFragmentLifecycleCallbacks(
            fm: FragmentManager, callbackEvent: LifecycleEvent, callbackFragment: Fragment
        ) {
            if (targetFragment === callbackFragment && callbackEvent === disposeEvent) {
                composite.dispose()
                fm.unregisterFragmentLifecycleCallbacks(this)
            }
        }
    }, false)

    return doOnSubscribe { composite.add(it) }
}

fun <T> Single<T>.disposeWhen(targetFragment: Fragment, disposeEvent: LifecycleEvent): Single<T> {
    val composite = CompositeDisposable()

    targetFragment.fragmentManager?.registerFragmentLifecycleCallbacks(object :
        FragmentManager.FragmentLifecycleCallbacks() {

        override fun onFragmentPaused(fm: FragmentManager, f: Fragment) {
            super.onFragmentPaused(fm, f)
            checkFragmentLifecycleCallbacks(fm, LifecycleEvent.PAUSED, f)
        }

        override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
            super.onFragmentStopped(fm, f)
            checkFragmentLifecycleCallbacks(fm, LifecycleEvent.STOPPED, f)
        }

        override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
            super.onFragmentViewDestroyed(fm, f)
            checkFragmentLifecycleCallbacks(fm, LifecycleEvent.VIEW_DESTROYED, f)
        }

        override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
            super.onFragmentDestroyed(fm, f)
            checkFragmentLifecycleCallbacks(fm, LifecycleEvent.DESTROYED, f)
        }

        fun checkFragmentLifecycleCallbacks(
            fm: FragmentManager, callbackEvent: LifecycleEvent, callbackFragment: Fragment
        ) {
            if (targetFragment === callbackFragment && callbackEvent === disposeEvent) {
                composite.dispose()
                fm.unregisterFragmentLifecycleCallbacks(this)
            }
        }
    }, false)

    return doOnSubscribe { composite.add(it) }
}

enum class LifecycleEvent {
    PAUSED, STOPPED, VIEW_DESTROYED, DESTROYED
}