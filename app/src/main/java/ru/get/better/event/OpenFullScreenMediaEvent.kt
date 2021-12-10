package ru.get.better.event

import android.widget.ImageView
import ru.get.better.model.Media

data class OpenFullScreenMediaEvent(
    val media: MutableList<Media>,
    val position: Int,
    val imageView: ImageView
)