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

fun Context.toastTodo() = Toast.makeText(
    this,
    "Coming Soon! \uD83D\uDE18\uD83D\uDC49\uD83D\uDCF1\uD83D\uDC49\uD83D\uDCAA",
    Toast.LENGTH_SHORT
).show()

fun Context.toast(msg: String?) {
    if (msg != null)
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Fragment.toast(msg: String?) {
    lifecycleScope.launch(Dispatchers.Main) {
        if (msg != null)
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }
}

fun Context.errorResponseAlertDialog(title: String, description: String) {

    val builder = android.app.AlertDialog.Builder(this)
    builder.setTitle(title)
    builder.setMessage(description)
    builder.setCancelable(false)
    builder.setPositiveButton(
        "Ok"
    ) { dialog, _ -> dialog.cancel() }
    val alert = builder.create()
    alert.show()

}


fun Context.getColorAttr(colorResourceId: Int): Int {
    theme.resolveAttribute(colorResourceId, TypedValue(), true)
    val arr = obtainStyledAttributes(TypedValue().data, intArrayOf(colorResourceId))
    val color = arr.getColorOrThrow(0)
    arr.recycle()
    return color
}

fun getVersionText() =
    " ${BuildConfig.VERSION_NAME}.${BuildConfig.VERSION_CODE}"

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

fun Context.getFont(@FontRes resId: Int): Typeface? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        resources.getFont(resId)
    } else {
        ResourcesCompat.getFont(this, resId)
    }
}

fun CharSequence.selectText(
    selectedText: String?,
    vararg spans: ParcelableSpan
): SpannableString {
    if (selectedText.isNullOrBlank()) return SpannableString(this)
    val source = SpannableString(this)
    return source.selectText(selectedText, *spans)
}

fun SpannableString.selectText(
    selectedText: String?,
    vararg spans: ParcelableSpan
): SpannableString {
    if (selectedText.isNullOrBlank()) return this
    if (contains(selectedText, ignoreCase = true)) {
        var startIndex = indexOf(selectedText, ignoreCase = true)
        while (startIndex >= 0) {
            val endIndex = startIndex + selectedText.length
            applySpans(
                *spans,
                start = startIndex,
                end = endIndex
            )
            startIndex = indexOf(selectedText, endIndex, ignoreCase = true)
        }
    }
    return this
}

fun SpannableString.applySpans(vararg spans: ParcelableSpan, start: Int, end: Int) {
    spans.forEach { span -> setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) }
}
