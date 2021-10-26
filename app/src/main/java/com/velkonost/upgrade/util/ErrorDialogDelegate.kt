package com.velkonost.upgrade.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.velkonost.upgrade.App
import com.velkonost.upgrade.R

class ErrorDialogDelegate(val context: Context) {

//    private var dialog = AlertDialog.Builder(context)
//
//        .setPositiveButton(R.string.dialog_positive_button) { dialog, _ ->
//            dialog.dismiss()
//        }.create()

//    fun showError(error: Error) {
//        when(error.message) {
//            "" -> App.preferences.defaultErrorMessage?.let { showError(it) }?: showError(error.messageId)
////            "" -> showError(error.messageId)
//            else -> showError(error.message)
//        }
//    }
//
//    fun showError(message: String){
//        dialog.dismiss()
//        dialog.setMessage(message)
//        dialog.show()
//    }
//
//    fun showError(@StringRes message: Int){
//        showError(context.getString(message))
//    }

}

fun lazyErrorDelegate(context: () -> Context) = lazy(LazyThreadSafetyMode.NONE) { ErrorDialogDelegate(context()) }