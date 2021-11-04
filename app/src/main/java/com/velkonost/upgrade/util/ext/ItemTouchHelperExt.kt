package com.velkonost.upgrade.util.ext

import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.*
import androidx.annotation.DimenRes
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.collection.lruCache
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.withTranslation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.velkonost.upgrade.R

fun RecyclerView.setUpRemoveItemTouchHelper(
    @StringRes backgroundString: Int,
    @DimenRes backgroundTextSize: Int,
    swiped: (viewHolder: RecyclerView.ViewHolder, swipeDir: Int) -> Unit
) {

    val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

        lateinit var background: Drawable
        var initiated: Boolean = false

        val textPaint = TextPaint().apply {
            color = Color.WHITE
            isAntiAlias = true
            textSize = resources.getDimension(backgroundTextSize)
            typeface = ResourcesCompat.getFont(context, R.font.sf_ui_display_medium)
        }

        val paint = Paint().apply {
            color = Color.WHITE
            isAntiAlias = true
            textSize = resources.getDimension(backgroundTextSize)
            typeface = ResourcesCompat.getFont(context, R.font.sf_ui_display_medium)
        }

        private fun init() {
            background = ColorDrawable(ContextCompat.getColor(context!!, R.color.colorPrimaryRed))
            initiated = true
        }

        override fun onMove(
            rv: RecyclerView,
            vh: RecyclerView.ViewHolder,
            t: RecyclerView.ViewHolder
        ): Boolean = false

        override fun getSwipeDirs(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            val position = viewHolder.adapterPosition
//            val conversationModel = (recyclerView.adapter as NotesAdapter).getItemAt(position)
            val canSwipe =
                true//conversationModel is CartItemProductModel || conversationModel is CartItemDishModel
            return if (canSwipe) super.getSwipeDirs(
                recyclerView,
                viewHolder
            ) else ItemTouchHelper.ACTION_STATE_IDLE
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
            swiped(viewHolder, swipeDir)
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            val itemView = viewHolder.itemView
            if (viewHolder.adapterPosition == -1) {
                return
            }
            if (!initiated) {
                init()
            }
            background.setBounds(
                itemView.right + dX.toInt(),
                itemView.top,
                itemView.right,
                itemView.bottom
            )
            background.draw(c)

            val bounds = Rect()
            val delete = context.getString(backgroundString)
            paint.getTextBounds(delete, 0, delete.length, bounds)
            val x = itemView.width - bounds.right - 20
            val y = itemView.bottom + bounds.height() - (itemView.height + bounds.height()) / 2

            c.drawMultilineText(
                delete, textPaint, bounds.width(),
                x.toFloat(), y.toFloat(), alignment = Layout.Alignment.ALIGN_CENTER
            )

            ContextCompat.getDrawable(context, R.drawable.ic_item_delete)?.let {
                val imgSize = resources.getDimension(R.dimen.icon_size_remove).toInt()
                val margin = resources.getDimension(R.dimen.three_quarters_activity_vertical_margin)
                val bitmap = it.toBitmap(imgSize, imgSize, Bitmap.Config.ARGB_8888)
                c.drawBitmap(
                    bitmap,
                    x + bounds.width() * 0.5f - imgSize * 0.4f,
                    y - bitmap.height - 0f,
                    paint
                )
            }
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }
    val touchHelper = ItemTouchHelper(simpleItemTouchCallback)
    touchHelper.attachToRecyclerView(this)
}

@RequiresApi(Build.VERSION_CODES.O)
fun Canvas.drawMultilineText(
    text: CharSequence,
    textPaint: TextPaint,
    width: Int,
    x: Float,
    y: Float,
    start: Int = 0,
    end: Int = text.length,
    alignment: Layout.Alignment = Layout.Alignment.ALIGN_NORMAL,
    textDir: TextDirectionHeuristic = TextDirectionHeuristics.FIRSTSTRONG_LTR,
    spacingMult: Float = 1f,
    spacingAdd: Float = 0f,
    includePad: Boolean = true,
    ellipsizedWidth: Int = width,
    ellipsize: TextUtils.TruncateAt? = null,
    maxLines: Int = Int.MAX_VALUE,
    breakStrategy: Int = Layout.BREAK_STRATEGY_SIMPLE,
    hyphenationFrequency: Int = Layout.HYPHENATION_FREQUENCY_NONE,
    justificationMode: Int = Layout.JUSTIFICATION_MODE_NONE
) {

    val cacheKey = "$text-$start-$end-$textPaint-$width-$alignment-$textDir-" +
            "$spacingMult-$spacingAdd-$includePad-$ellipsizedWidth-$ellipsize-" +
            "$maxLines-$breakStrategy-$hyphenationFrequency-$justificationMode"

    val staticLayout = StaticLayoutCache[cacheKey] ?: StaticLayout.Builder.obtain(
        text,
        start,
        end,
        textPaint,
        width
    )
        .setAlignment(alignment)
        .setTextDirection(textDir)
        .setLineSpacing(spacingAdd, spacingMult)
        .setIncludePad(includePad)
        .setEllipsizedWidth(ellipsizedWidth)
        .setEllipsize(ellipsize)
        .setMaxLines(maxLines)
        .setBreakStrategy(breakStrategy)
        .setHyphenationFrequency(hyphenationFrequency)
        .setJustificationMode(justificationMode)
        .build().apply { StaticLayoutCache[cacheKey] = this }

    staticLayout.draw(this, x, y)
}

@RequiresApi(Build.VERSION_CODES.M)
fun Canvas.drawMultilineText(
    text: CharSequence,
    textPaint: TextPaint,
    width: Int,
    x: Float,
    y: Float,
    start: Int = 0,
    end: Int = text.length,
    alignment: Layout.Alignment = Layout.Alignment.ALIGN_NORMAL,
    textDir: TextDirectionHeuristic = TextDirectionHeuristics.FIRSTSTRONG_LTR,
    spacingMult: Float = 1f,
    spacingAdd: Float = 0f,
    includePad: Boolean = true,
    ellipsizedWidth: Int = width,
    ellipsize: TextUtils.TruncateAt? = null,
    maxLines: Int = Int.MAX_VALUE,
    breakStrategy: Int = Layout.BREAK_STRATEGY_SIMPLE,
    hyphenationFrequency: Int = Layout.HYPHENATION_FREQUENCY_NONE
) {

    val cacheKey = "$text-$start-$end-$textPaint-$width-$alignment-$textDir-" +
            "$spacingMult-$spacingAdd-$includePad-$ellipsizedWidth-$ellipsize-" +
            "$maxLines-$breakStrategy-$hyphenationFrequency"

    val staticLayout = StaticLayoutCache[cacheKey] ?: StaticLayout.Builder.obtain(
        text,
        start,
        end,
        textPaint,
        width
    )
        .setAlignment(alignment)
        .setTextDirection(textDir)
        .setLineSpacing(spacingAdd, spacingMult)
        .setIncludePad(includePad)
        .setEllipsizedWidth(ellipsizedWidth)
        .setEllipsize(ellipsize)
        .setMaxLines(maxLines)
        .setBreakStrategy(breakStrategy)
        .setHyphenationFrequency(hyphenationFrequency)
        .build().apply { StaticLayoutCache[cacheKey] = this }

    staticLayout.draw(this, x, y)
}

fun Canvas.drawMultilineText(
    text: CharSequence,
    textPaint: TextPaint,
    width: Int,
    x: Float,
    y: Float,
    start: Int = 0,
    end: Int = text.length,
    alignment: Layout.Alignment = Layout.Alignment.ALIGN_NORMAL,
    spacingMult: Float = 1f,
    spacingAdd: Float = 0f,
    includePad: Boolean = true,
    ellipsizedWidth: Int = width,
    ellipsize: TextUtils.TruncateAt? = null
) {

    val cacheKey = "$text-$start-$end-$textPaint-$width-$alignment-" +
            "$spacingMult-$spacingAdd-$includePad-$ellipsizedWidth-$ellipsize"

    // The public constructor was deprecated in API level 28,
    // but the builder is only available from API level 23 onwards
    val staticLayout =
        StaticLayoutCache[cacheKey] ?: if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder.obtain(text, start, end, textPaint, width)
                .setAlignment(alignment)
                .setLineSpacing(spacingAdd, spacingMult)
                .setIncludePad(includePad)
                .setEllipsizedWidth(ellipsizedWidth)
                .setEllipsize(ellipsize)
                .build()
        } else {
            StaticLayout(
                text, start, end, textPaint, width, alignment,
                spacingMult, spacingAdd, includePad, ellipsize, ellipsizedWidth
            )
                .apply { StaticLayoutCache[cacheKey] = this }
        }

    staticLayout.draw(this, x, y)
}

private fun StaticLayout.draw(canvas: Canvas, x: Float, y: Float) {
    canvas.withTranslation(x, y) {
        draw(this)
    }
}

private object StaticLayoutCache {

    private const val MAX_SIZE = 50 // Arbitrary max number of cached items
    private val cache = lruCache<String, StaticLayout>(MAX_SIZE)

    operator fun set(key: String, staticLayout: StaticLayout) {
        cache.put(key, staticLayout)
    }

    operator fun get(key: String): StaticLayout? {
        return cache[key]
    }
}