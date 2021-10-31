package com.velkonost.upgrade.model

import android.net.Uri

class Media(
    val uri: Uri? = null,
    val url: String? = null
) {
    val type: MEDIA_TYPE = MEDIA_TYPE.PHOTO


    enum class MEDIA_TYPE { PHOTO, VIDEO }
}