package ru.get.better.ui.activity.main.ext

import androidx.core.view.isVisible
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import kotlinx.android.synthetic.main.view_affirmation.view.*
import ru.get.better.App
import ru.get.better.glide.GlideApp
import ru.get.better.model.getTodayAffirmation
import ru.get.better.ui.activity.main.MainActivity
import ru.get.better.util.ViewState
import ru.get.better.util.doOn
import ru.get.better.util.ext.scaleXY
import ru.get.better.vm.AffirmationsViewModel
import android.graphics.drawable.Drawable

import android.net.Uri
import android.transition.Transition
import android.util.Log
import androidx.annotation.NonNull
import androidx.annotation.Nullable

import com.bumptech.glide.request.target.CustomTarget
import java.io.ByteArrayOutputStream
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import androidx.core.content.ContextCompat.startActivity

import android.content.Intent
import android.graphics.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.protobuf.Empty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import ru.get.better.R
import ru.get.better.event.ChangeProgressStateEvent
import ru.get.better.util.convertDpToPx


fun MainActivity.observeNasaData(
    nasaDataViewStateData: ViewState<AffirmationsViewModel.NasaDataViewStateData>
) {
    nasaDataViewStateData.doOn(
        showProgress = {},
        hideProgress = {},
        data = { nasaData ->
           affirmationIconUrl = nasaData.imgUrl
            setupAffirmationIcon()
        }
    )
}

private fun MainActivity.setupAffirmationIcon() {
    if (isAffirmationIconUrlInitialized()) {
        GlideApp.with(this)
            .asBitmap()
            .load(affirmationIconUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(object : CustomTarget<Bitmap?>() {

                override fun onLoadCleared(@Nullable placeholder: Drawable?) {}
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: com.bumptech.glide.request.transition.Transition<in Bitmap?>?
                ) {
                    binding.affirmationView.affirmationIcon.setImageBitmap(resource)
                    binding.affirmationView.affirmationIcon.buildDrawingCache()
                }
            })
//            .into(binding.affirmationView.affirmationIcon)

//        glideRequestManager.load(affirmationIconUrl).into(binding.affirmationView.affirmationIcon)
//            .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
    }
}

private fun MainActivity.showAffirmation() {
    binding.affirmationView.affirmationTitle.text = this.getTodayAffirmation().title

    binding.affirmationView.affirmationDesc.isVisible = !this.getTodayAffirmation().desc.isNullOrEmpty()
    binding.affirmationView.affirmationDesc.text = this.getTodayAffirmation().desc

    binding.affirmationView.affirmationShareBtn.text = App.resourcesProvider.getStringLocale(R.string.share)

    currentSecondaryView = SecondaryViews.Affirmation
    binding.affirmationView.affirmationContainer.isVisible = true

    binding.affirmationView.affirmationContainer.scaleXY(1f, 1f, 500) {
        binding.affirmationView.affirmationBlur.isVisible = true
    }

    binding.affirmationView.affirmationCross.setOnClickListener {
        binding.affirmationView.affirmationBlur.isVisible = false

        binding.affirmationView.affirmationContainer.scaleXY(0f, 0f, 500) {
            binding.affirmationView.affirmationContainer.isVisible = false
            currentSecondaryView = SecondaryViews.Empty
        }
    }

    binding.affirmationView.affirmationShareBtn.setOnClickListener {
        shareAffirmation()
    }

    binding.affirmationView.affirmationBlur.setOnClickListener {
        binding.affirmationView.affirmationBlur.isVisible = false

        binding.affirmationView.affirmationContainer.scaleXY(0f, 0f, 500) {
            binding.affirmationView.affirmationContainer.isVisible = false
            currentSecondaryView = SecondaryViews.Empty
        }
    }
}

fun MainActivity.setupAffirmation(isIncreaseNumber: Boolean) {

    if (isIncreaseNumber) {
        if (App.preferences.lastDayShownAffirmationMills == 0L) {
            App.preferences.lastDayShownAffirmationMills = System.currentTimeMillis() / 86400000

            if (currentSecondaryView == SecondaryViews.Empty)
                showAffirmation()

            return
        }

        val todayMills = System.currentTimeMillis() / 86400000

        if (App.preferences.lastDayShownAffirmationMills != todayMills) {
            App.preferences.currentAffirmationNumber ++
            App.preferences.lastDayShownAffirmationMills = todayMills

            if (currentSecondaryView == SecondaryViews.Empty)
                showAffirmation()
        }
    } else {
        if (currentSecondaryView == SecondaryViews.Empty)
            showAffirmation()
    }

}

fun MainActivity.getAffirmationBitmap(): Bitmap {
    binding.affirmationView.affirmationCard.isDrawingCacheEnabled = true
    binding.affirmationView.affirmationCard.buildDrawingCache()
    val bm: Bitmap = binding.affirmationView.affirmationCard.drawingCache
    val bytes = ByteArrayOutputStream()
    bm.compress(Bitmap.CompressFormat.PNG, 100, bytes)

    return bm
}

fun MainActivity.getRoundedRectBitmap(bitmap: Bitmap, pixels: Int): Bitmap {
    val result = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(result)
    val color: Int = -0xbdbdbe
    val paint = Paint()
    val rect = Rect(0, 0, bitmap.width, bitmap.height)
    val rectF = RectF(rect)
    val roundPx: Float = pixels.toFloat()
    paint.isAntiAlias = true
    canvas.drawARGB(0, 0, 0, 0)
    paint.color = color
    canvas.drawRoundRect(rectF, roundPx, roundPx, paint)
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(bitmap, rect, rect, paint)
    return result
}

fun MainActivity.saveAffirmationImg(bitmap: Bitmap): Uri? {
    val imagesFolder = File(cacheDir, "images")
    var uri: Uri? = null
    try {
        imagesFolder.mkdirs()
        val file = File(imagesFolder, "affirmation_${App.preferences.currentAffirmationNumber}.png")
        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
        stream.flush()
        stream.close()
        uri = FileProvider.getUriForFile(this, "ru.get.better.fileprovider", file)
    } catch (e: IOException) {
        Log.d("IOException", "IOException while trying to write file for sharing: " + e.message)
    }
    return uri
}

fun MainActivity.shareAffirmation() {

    binding.affirmationView.affirmationCross.isVisible = false
    binding.affirmationView.affirmationShareBtn.isVisible = false

    lifecycleScope.launch(Dispatchers.IO) {

        EventBus.getDefault().post(ChangeProgressStateEvent(isActive = true))
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(
            Intent.EXTRA_STREAM,
            saveAffirmationImg(
                getRoundedRectBitmap(
                    getAffirmationBitmap(), convertDpToPx(64f).toInt()
                )
            )
        )
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        intent.type = "image/png"
        startActivityForResult(intent, -999)

        EventBus.getDefault().post(ChangeProgressStateEvent(isActive = false))

    }.invokeOnCompletion {
        runOnUiThread {
            binding.affirmationView.affirmationCross.isVisible = true
            binding.affirmationView.affirmationShareBtn.isVisible = true
        }
    }

}