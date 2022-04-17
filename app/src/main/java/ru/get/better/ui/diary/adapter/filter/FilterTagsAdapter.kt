package ru.get.better.ui.diary.adapter.filter

import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModel
import kotlinx.android.synthetic.main.item_tag.view.*
import org.greenrobot.eventbus.EventBus
import ru.get.better.App
import ru.get.better.R
import ru.get.better.event.RemoveTagEvent
import ru.get.better.ui.activity.main.ext.adapter.TagEmptyModel
import ru.get.better.ui.activity.main.ext.adapter.TagModel

class FilterTagsAdapter : EpoxyAdapter() {

    private var items: MutableList<String> = mutableListOf()
    private var selectedItems: MutableList<String> = mutableListOf()

    fun createList(
        models: MutableList<String>
    ) {
        items = models
        selectedItems = mutableListOf()

        removeAllModels()

        models.map { addModel(FilterTagModel(it)) }

        notifyDataSetChanged()
    }

    fun getSelectedItems(): MutableList<String> {
        val selectedTags = mutableListOf<String>()

        models.forEach { model ->
            if (model is FilterTagModel && model.isTagSelected)
                selectedTags.add(model.model)
        }

        return selectedTags
    }

    fun clearAll() {
        models.forEach { model ->
            if (model is FilterTagModel)
                model.isTagSelected = false
        }
        notifyDataSetChanged()
    }

    fun selectAll() {
        models.forEach { model ->
            if (model is FilterTagModel)
                model.isTagSelected = true
        }
        notifyDataSetChanged()
    }

    fun getItems() = items

}

class FilterTagModel(
    val model: String,
    var isTagSelected: Boolean = false
): EpoxyModel<View>() {

    private var root: View? = null

    override fun bind(view: View) {
        super.bind(view)
        root = view

        with(view) {
            chip.text = model

            chip.chipBackgroundColor = ColorStateList.valueOf(
                ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkBgEditTextSolid
                else R.color.colorLightBgEditTextSolid
            ))

            chip.setTextColor(
                ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkViewPostAddTitleText
                else R.color.colorLightViewPostAddTitleText
            ))

//            chip.chipBackgroundColor = ColorStateList.valueOf(
//                ContextCompat.getColor(
//                context,
//                if (App.preferences.isDarkTheme) R.color.colorDarkViewPostAddEditTextHint
//                else R.color.colorLightViewPostAddEditTextHint
//            ))

            chip.elevation = 4f
            chip.isCloseIconVisible = false

            if (isTagSelected) {
                chip.chipBackgroundColor = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        context,
                        if (App.preferences.isDarkTheme) R.color.colorDarkViewPostAddEditTextHint
                        else R.color.colorLightViewPostAddEditTextHint
                    ))
            } else {

                chip.chipBackgroundColor = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        context,
                        if (App.preferences.isDarkTheme) R.color.colorDarkBgEditTextSolid
                        else R.color.colorLightBgEditTextSolid
                    ))
            }

            chip.setOnClickListener {
                isTagSelected = !isTagSelected

                if (isTagSelected) {
                    chip.chipBackgroundColor = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            context,
                            if (App.preferences.isDarkTheme) R.color.colorDarkViewPostAddEditTextHint
                            else R.color.colorLightViewPostAddEditTextHint
                        ))
                } else {

                    chip.chipBackgroundColor = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            context,
                            if (App.preferences.isDarkTheme) R.color.colorDarkBgEditTextSolid
                            else R.color.colorLightBgEditTextSolid
                        ))
                }
            }
        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_tag
}