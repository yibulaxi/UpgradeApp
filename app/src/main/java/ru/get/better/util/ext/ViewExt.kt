package ru.get.better.util.ext

import android.text.Editable
import android.widget.TextView
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

fun TextView.afterTextChangeObservable(): Observable<Editable> =
    RxTextView.afterTextChangeEvents(this)
        .takeUntil(RxView.detaches(this))
        .map { it.editable() ?: Editable.Factory().newEditable("") }
        .observeOn(AndroidSchedulers.mainThread())

fun TextView.debounceTextChangeObservable(debounceMillis: Long = 500): Observable<Editable> =
    RxTextView.afterTextChangeEvents(this)
        .debounce(debounceMillis, TimeUnit.MILLISECONDS)
        .takeUntil(RxView.detaches(this))
        .map { it.editable() ?: Editable.Factory().newEditable("") }
        .observeOn(AndroidSchedulers.mainThread())

fun Float.value() = String.format("%.1f", this)