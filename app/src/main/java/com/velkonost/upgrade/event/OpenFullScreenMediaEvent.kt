package com.velkonost.upgrade.event

import android.widget.ImageView
import com.velkonost.upgrade.model.Media

data class OpenFullScreenMediaEvent(
    val media: MutableList<Media>,
    val position: Int,
    val imageView: ImageView
)