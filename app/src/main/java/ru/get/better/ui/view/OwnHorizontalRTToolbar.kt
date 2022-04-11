package ru.get.better.ui.view

import android.content.Context
import android.text.Layout
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.onegravity.rteditor.RTToolbar
import com.onegravity.rteditor.RTToolbarListener
import com.onegravity.rteditor.effects.Effects
import com.onegravity.rteditor.fonts.RTTypeface
import com.onegravity.rteditor.toolbar.RTToolbarImageButton
import com.onegravity.rteditor.toolbar.spinner.FontSpinnerItem
import com.onegravity.rteditor.toolbar.spinner.SpinnerItems
import ru.get.better.R
import java.util.concurrent.atomic.AtomicInteger

class OwnHorizontalRTToolbar : LinearLayout, RTToolbar, View.OnClickListener {
    private var mId = 0
    private var mListener: RTToolbarListener? = null
    private var mToolbarContainer: ViewGroup? = null

    /*
     * The buttons
     */
    private var mBold: RTToolbarImageButton? = null
    private var mItalic: RTToolbarImageButton? = null
    private var mUnderline: RTToolbarImageButton? = null
    private var mStrikethrough: RTToolbarImageButton? = null
    private var mSuperscript: RTToolbarImageButton? = null
    private var mSubscript: RTToolbarImageButton? = null
    private var mBullet: RTToolbarImageButton? = null

    // ****************************************** Initialize Methods *******************************************
    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int)
            : super(context, attrs, defStyle) {
        init()
    }

    private fun init() {
        synchronized(sIdCounter) {
            mId = sIdCounter.getAndIncrement()
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        // configure regular action buttons
        mBold = initImageButton(R.id.toolbar_bold)
        mItalic = initImageButton(R.id.toolbar_italic)
        mUnderline = initImageButton(R.id.toolbar_underline)
        mStrikethrough = initImageButton(R.id.toolbar_strikethrough)
        mSuperscript = initImageButton(R.id.toolbar_superscript)
        mSubscript = initImageButton(R.id.toolbar_subscript)
        mBullet = initImageButton(R.id.toolbar_bullet)
        initImageButton(R.id.toolbar_undo)
        initImageButton(R.id.toolbar_redo)
        initImageButton(R.id.toolbar_clear)
    }

    private fun initImageButton(id: Int): RTToolbarImageButton {
        val button = findViewById<View>(id) as RTToolbarImageButton
        button.setOnClickListener(this)
        return button
    }

    // ****************************************** RTToolbar Methods *******************************************
    override fun setToolbarContainer(toolbarContainer: ViewGroup) {
        mToolbarContainer = toolbarContainer
    }

    override fun getToolbarContainer(): ViewGroup {
        return if (mToolbarContainer == null) this else mToolbarContainer!!
    }

    override fun setToolbarListener(listener: RTToolbarListener) {
        mListener = listener
    }

    override fun removeToolbarListener() {
        mListener = null
    }

    override fun getId(): Int {
        return mId
    }

    override fun setBold(enabled: Boolean) {
        if (mBold != null) mBold!!.isChecked = enabled
    }

    override fun setItalic(enabled: Boolean) {
        if (mItalic != null) mItalic!!.isChecked = enabled
    }

    override fun setUnderline(enabled: Boolean) {
        if (mUnderline != null) mUnderline!!.isChecked = enabled
    }

    override fun setStrikethrough(enabled: Boolean) {
        if (mStrikethrough != null) mStrikethrough!!.isChecked = enabled
    }

    override fun setSuperscript(enabled: Boolean) {
        if (mSuperscript != null) mSuperscript!!.isChecked = enabled
    }

    override fun setSubscript(enabled: Boolean) {
        if (mSubscript != null) mSubscript!!.isChecked = enabled
    }

    override fun setBullet(enabled: Boolean) {
        if (mBullet != null) mBullet!!.isChecked = enabled
    }

    override fun setNumber(enabled: Boolean) {
    }

    override fun setAlignment(alignment: Layout.Alignment?) {
    }

    override fun setFont(typeface: RTTypeface?) {

    }

    override fun setFontSize(size: Int) {
    }

    override fun setFontColor(color: Int) {

    }

    override fun setBGColor(color: Int) {
    }

    override fun removeFontColor() {
    }

    override fun removeBGColor() {
    }

    private fun getFontItems(): SpinnerItems<FontSpinnerItem>? {

        /*
         * Create the spinner items
         */
        val spinnerItems = SpinnerItems<FontSpinnerItem>()

        return spinnerItems
    }

    // ****************************************** Item Selected Methods *******************************************
    override fun onClick(v: View) {
        if (mListener != null) {
            val id = v.id
            if (id == R.id.toolbar_bold) {
                mBold!!.isChecked = !mBold!!.isChecked
                mListener!!.onEffectSelected(Effects.BOLD, mBold!!.isChecked)
            } else if (id == R.id.toolbar_italic) {
                mItalic!!.isChecked = !mItalic!!.isChecked
                mListener!!.onEffectSelected(Effects.ITALIC, mItalic!!.isChecked)
            } else if (id == R.id.toolbar_underline) {
                mUnderline!!.isChecked = !mUnderline!!.isChecked
                mListener!!.onEffectSelected(Effects.UNDERLINE, mUnderline!!.isChecked)
            } else if (id == R.id.toolbar_strikethrough) {
                mStrikethrough!!.isChecked = !mStrikethrough!!.isChecked
                mListener!!.onEffectSelected(Effects.STRIKETHROUGH, mStrikethrough!!.isChecked)
            } else if (id == R.id.toolbar_superscript) {
                mSuperscript!!.isChecked = !mSuperscript!!.isChecked
                mListener!!.onEffectSelected(Effects.SUPERSCRIPT, mSuperscript!!.isChecked)
                if (mSuperscript!!.isChecked && mSubscript != null) {
                    mSubscript!!.isChecked = false
                    mListener!!.onEffectSelected(Effects.SUBSCRIPT, mSubscript!!.isChecked)
                }
            } else if (id == R.id.toolbar_subscript) {
                mSubscript!!.isChecked = !mSubscript!!.isChecked
                mListener!!.onEffectSelected(Effects.SUBSCRIPT, mSubscript!!.isChecked)
                if (mSubscript!!.isChecked && mSuperscript != null) {
                    mSuperscript!!.isChecked = false
                    mListener!!.onEffectSelected(Effects.SUPERSCRIPT, mSuperscript!!.isChecked)
                }
            } else if (id == R.id.toolbar_bullet) {
                mBullet!!.isChecked = !mBullet!!.isChecked
                val isChecked = mBullet!!.isChecked
                mListener!!.onEffectSelected(Effects.BULLET, isChecked)

            } else if (id == R.id.toolbar_clear) {
                mListener!!.onClearFormatting()
            } else if (id == R.id.toolbar_undo) {
                mListener!!.onUndo()
            } else if (id == R.id.toolbar_redo) {
                mListener!!.onRedo()
            }
        }
    }

    companion object {
        /*
     * We need a unique id for the toolbar because the RTManager is capable of managing multiple toolbars
     */
        private val sIdCounter = AtomicInteger(0)
    }


}
