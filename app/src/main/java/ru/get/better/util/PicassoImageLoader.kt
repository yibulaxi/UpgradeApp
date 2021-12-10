package ru.get.better.util

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.squareup.picasso.Picasso
import lv.chi.photopicker.loader.ImageLoader
import ru.get.better.R

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
