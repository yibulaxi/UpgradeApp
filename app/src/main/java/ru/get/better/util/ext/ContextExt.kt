package ru.get.better.util.ext

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.text.ParcelableSpan
import android.text.SpannableString
import android.text.Spanned
import android.util.TypedValue
import android.widget.Toast
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.getColorOrThrow
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.get.better.BuildConfig

fun <T> mutableLiveDataOf(startValue: T) = MutableLiveData<T>().apply { value = startValue }

fun <T : Any, L : LiveData<T>> Fragment.observeOnView(
    liveData: L,
    observer: (T) -> Unit
): Cancelable {
    val liveDataObserver = Observer<T> { observer(it!!) }
    liveData.observe(viewLifecycleOwner, liveDataObserver)
    return object : Cancelable {
        override fun cancel() {
            liveData.removeObserver(liveDataObserver)
        }
    }
}

interface Cancelable {
    fun cancel()
}

inline fun <reified T : ViewModel> LifecycleOwner.lazyViewModel(crossinline factory: () -> ViewModelProvider.Factory) =
    lazy(LazyThreadSafetyMode.NONE) {
        getViewModel(factory(), T::class.java)
    }

fun <T : ViewModel> LifecycleOwner.getViewModel(
    factory: ViewModelProvider.Factory,
    clazz: Class<T>
): T = when (this) {
    is Fragment -> ViewModelProviders.of(this, factory).get(clazz)
    is FragmentActivity -> ViewModelProviders.of(this, factory).get(clazz)
    else -> throw RuntimeException("LifecycleOwner doesn't instance of Fragment or Activity")
}