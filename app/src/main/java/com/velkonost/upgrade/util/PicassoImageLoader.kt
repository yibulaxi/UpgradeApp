package com.velkonost.upgrade.util

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.velkonost.upgrade.R
import lv.chi.photopicker.loader.ImageLoader

class PicassoImageLoader : ImageLoader {

    override fun loadImage(context: Context, view: ImageView, uri: Uri) {
        Picasso.with(context)
            .load(uri)
            .placeholder(R.drawable.ic_add_media)
            .fit()
            .centerCrop()
            .into(view)
    }
}
