package com.velkonost.upgrade.ui.view

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible

import com.google.android.material.snackbar.BaseTransientBottomBar
import com.velkonost.upgrade.R
import timber.log.Timber

class SimpleCustomSnackbar(
    parent: ViewGroup,
    content: SimpleCustomSnackbarView
) : BaseTransientBottomBar<SimpleCustomSnackbar>(parent, content, content) {


    init {
        getView().setBackgroundColor(
            ContextCompat.getColor(
                view.context,
                android.R.color.transparent
            )
        )
        getView().setPadding(0, 0, 0, 0)
    }

    companion object {
        fun make(
            view: View,
            message: String,
            duration: Int,
            actionListener: View.OnClickListener? = null,
            messageClickListener: View.OnClickListener? = null,
            icon: Int? = null,
            action_label: String?,
            bg_drawable: Int,
            bg_gradient_drawable: Int? = null
        ): SimpleCustomSnackbar? {

            // First we find a suitable parent for our custom view
            val parent = view.findSuitableParent() ?: throw IllegalArgumentException(
                "No suitable parent found from the given view. Please provide a valid view."
            )

            // We inflate our custom view
            try {
                val customView = LayoutInflater.from(view.context).inflate(
                    R.layout.layout_simple_custom_snackbar,
                    parent,
                    false
                ) as SimpleCustomSnackbarView
                // We create and return our Snackbar
                customView.tvMsg.text = message
//                textSize?.let { customView.tvMsg.setTextSize(TypedValue.COMPLEX_UNIT_SP, it) }
                action_label?.let {
                    customView.tvAction.text = action_label
                    customView.tvAction.setOnClickListener {
                        actionListener?.onClick(customView.tvAction)
                    }
                }
                icon?.let {
                    customView.imLeft.isVisible = true
                    customView.imLeft.setImageResource(it)
                } ?: run {
                    customView.imLeft.isVisible = false
                }
                customView.layRoot.background =
                    customView.context.resources.getDrawable(bg_drawable)
                bg_gradient_drawable?.let {
                    customView.backgroundRoot.background =
                        customView.context.resources.getDrawable(it)
                }

                val snackbar = SimpleCustomSnackbar(
                    parent,
                    customView
                ).setDuration(duration)

                customView.tvMsg.setOnClickListener {
                    snackbar.dismiss()
                    messageClickListener?.onClick(customView.tvMsg)
                }


                return snackbar
            } catch (e: Exception) {
                Timber.v("exception ${e.message}")
            }

            return null
        }

    }
}

internal fun View?.findSuitableParent(): ViewGroup? {
    var view = this
    var fallback: ViewGroup? = null
    do {
        if (view is CoordinatorLayout) {
            // We've found a CoordinatorLayout, use it
            return view
        } else if (view is FrameLayout) {
            if (view.id == android.R.id.content) {
                // If we've hit the decor content view, then we didn't find a CoL in the
                // hierarchy, so use it.
                return view
            } else {
                // It's not the content view but we'll use it as our fallback
                fallback = view
            }
        }

        if (view != null) {
            // Else, we will loop and crawl up the view hierarchy and try to find a parent
            val parent = view.parent
            view = if (parent is View) parent else null
        }
    } while (view != null)

    // If we reach here then we didn't find a CoL or a suitable content view so we'll fallback
    return fallback
}