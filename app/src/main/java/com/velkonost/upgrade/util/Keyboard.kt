package com.velkonost.upgrade.util

import android.app.Activity
import android.content.Context
import android.os.IBinder
import android.view.View
import android.view.inputmethod.InputMethodManager

object Keyboard {

    fun hide(activity: Activity) {
        val focusedView = activity.currentFocus
        if (focusedView != null) {
            hideInternal(focusedView.context, focusedView.windowToken)
        }
    }

    fun hide(view: View) {
        val focusedView = view.findFocus()
        if (focusedView != null) {
            hideInternal(focusedView.context, focusedView.windowToken)
        }
    }

    private fun hideInternal(ctx: Context, windowToken: IBinder?) {
        if (windowToken != null) {
            ctx.inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
        }
    }

    fun show(view: View) {
        view.requestFocus()
        view.context.inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_FORCED)
    }

}

val Context.inputMethodManager: InputMethodManager
    get() {
        return getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }
