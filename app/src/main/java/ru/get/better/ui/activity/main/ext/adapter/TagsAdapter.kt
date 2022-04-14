package ru.get.better.ui.activity.main.ext.adapter

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.CursorAnchorInfo
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.annotation.Keep
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doBeforeTextChanged
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModel
import kotlinx.android.synthetic.main.item_tag.view.*
import kotlinx.android.synthetic.main.item_tag_empty.view.*
import org.greenrobot.eventbus.EventBus
import ru.get.better.App
import ru.get.better.R
import ru.get.better.event.AddTagEvent
import ru.get.better.event.RemoveTagEvent
import ru.get.better.glide.GlideRequests
import ru.get.better.model.NoteType
import ru.get.better.util.Keyboard
import ru.get.better.util.inputMethodManager
import java.security.Key

class TagsAdapter : EpoxyAdapter() {

    private var items: MutableList<String> = mutableListOf()
    private val tagEmptyModel = TagEmptyModel(items)

    fun createList(
        models: MutableList<String>,
        isAddEmptyTag: Boolean = true
    ) {
        items = models
        tagEmptyModel.items = items

        removeAllModels()

        models.map { addModel(TagModel(it, isAddEmptyTag)) }

        if (
            isAddEmptyTag
            && items.size < 5
        ) {
            addModel(tagEmptyModel)
            showModel(tagEmptyModel)
        }

        notifyDataSetChanged()
    }

    fun addTag(tag: String) {
        items.add(tag)
        tagEmptyModel.items = items

        insertModelBefore(
            TagModel(tag),
            tagEmptyModel
        )

        if (items.size >= 5)
            hideModel(tagEmptyModel)
    }

    fun removeTag(tag: String) {
        items.remove(tag)
        tagEmptyModel.items = items

        removeModel(
            models.find {
                it is TagModel && it.model == tag
            }
        )

        if (items.size < 5)
            showModel(tagEmptyModel)

    }

    fun getItems() = items

}

class TagModel(
    val model: String,
    private val isAllowDelete: Boolean = true
): EpoxyModel<View>() {

    private var root: View? = null

    override fun bind(view: View) {
        super.bind(view)
        root = view

        with(view) {
            chip.text = model

            chip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkBgEditTextStroke
                else R.color.colorLightBgEditTextStroke
            ))

            chip.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkViewPostAddTitleText
                else R.color.colorLightViewPostAddTitleText
            ))

            chip.closeIconTint = ColorStateList.valueOf(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkViewPostAddEditTextHint
                else R.color.colorLightViewPostAddEditTextHint
            ))

            chip.isCloseIconVisible = isAllowDelete

            chip.setOnCloseIconClickListener {
                EventBus.getDefault().post(RemoveTagEvent(model))
            }
        }
    }

    override fun unbind(view: View) {
//        EventBus.getDefault().unregister(this)
        super.unbind(view)
    }

    override fun getDefaultLayout(): Int = R.layout.item_tag
}

class TagEmptyModel(
    var items: List<String>
): EpoxyModel<View>() {

    private var root: View? = null

    override fun bind(view: View) {
        super.bind(view)
        root = view

//        if (!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this)

        with(view) {

            chipEdit.hint = App.resourcesProvider.getStringLocale(R.string.add_tag_hint)

            chipEdit.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewPostAddEditTextText
                    else R.color.colorLightViewPostAddEditTextText
                )
            )

            chipEdit.setHintTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewPostAddEditTextHint
                    else R.color.colorLightViewPostAddEditTextHint
                )
            )

            chipEdit.doAfterTextChanged {
                if (
                    chipEdit.text.toString().replace(" ", "").isNotEmpty()
                    && chipEdit.text.last() == ' '
                    && !items.contains(chipEdit.text.toString().replace(" ", ""))
                ) {
                    EventBus.getDefault().post(AddTagEvent(chipEdit.text.toString().replace(" ", "")))
                    chipEdit.text.clear()

                    chipEdit.clearFocus()
                    Keyboard.hide(chipEdit)

                    Handler().postDelayed({
                        chipEdit.setSelection(0)
                        chipEdit.requestFocus()
                        context.inputMethodManager.showSoftInput(chipEdit, InputMethodManager.SHOW_FORCED)
                    }, 500)
                } else if (chipEdit.text.toString().contains(" ")) {
                    chipEdit.setText(chipEdit.text.toString().replace(" ", ""))
                    chipEdit.setSelection(chipEdit.text.length)
                }
            }

            chipEdit.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (
                        chipEdit.text.toString().replace(" ", "").isNotEmpty()
                        && chipEdit.text.last() == ' '
                        && !items.contains(chipEdit.text.toString().replace(" ", ""))
                    ) {
                        EventBus.getDefault()
                            .post(AddTagEvent(chipEdit.text.toString().replace(" ", "")))
                        chipEdit.text.clear()

                        chipEdit.clearFocus()
                        Keyboard.hide(chipEdit)

                        Handler().postDelayed({
                            chipEdit.setSelection(0)
                            chipEdit.requestFocus()
                            context.inputMethodManager.showSoftInput(
                                chipEdit,
                                InputMethodManager.SHOW_FORCED
                            )
                        }, 500)

                        return@setOnEditorActionListener true
                    }
                }
                false
            }
        }
    }

    override fun unbind(view: View) {
//        EventBus.getDefault().unregister(this)
        super.unbind(view)
    }

    override fun getDefaultLayout(): Int = R.layout.item_tag_empty
}