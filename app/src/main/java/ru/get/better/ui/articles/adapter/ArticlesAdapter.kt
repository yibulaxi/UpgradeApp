package ru.get.better.ui.articles.adapter

import android.content.res.ColorStateList
import android.graphics.Paint
import android.text.Html
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModel
import kotlinx.android.synthetic.main.item_article.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import ru.get.better.App
import ru.get.better.R
import ru.get.better.event.ArticleClickEvent
import ru.get.better.glide.GlideRequests
import ru.get.better.model.Article
import ru.get.better.model.ArticleState

class ArticlesAdapter(
) : EpoxyAdapter() {

    fun createList(models: List<Article>) {
        removeAllModels()
        models.map { addModel(ArticleModel(it)) }
        notifyDataSetChanged()
    }

    fun updateArticle(article: Article) {
        models.forEach { model ->
            if (
                model is ArticleModel
                && model.model._id == article._id
                    ) {
                model.model.flag = article.flag
                notifyModelChanged(model)
            }
        }
    }
}

class ArticleModel(
    val model: Article,
): EpoxyModel<View>() {

    private var root: View? = null

    override fun bind(view: View) {
        super.bind(view)
        root = view

//        if (!EventBus.getDefault().isRegistered(this))
//            EventBus.getDefault().register(this)

        with(view) {
            cardView.animation =
                AnimationUtils.loadAnimation(context, R.anim.scale)

            title.text = Html.fromHtml(model.name)

            cardView.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.colorDarkItemNoteBackgroundTint
                    else R.color.colorLightItemNoteBackgroundTint
                )
            )

            title.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (App.preferences.isDarkTheme) R.color.colorDarkItemNoteTitleText
                    else R.color.colorLightItemNoteTitleText
                )
            )

            when(model.flag) {
                ArticleState.UNREAD.flag -> stateView.isVisible = false
                ArticleState.OPENED.flag -> {
                    stateView.isVisible = true

                    stateView.setBackgroundColor(
                        context.getColor(
                            com.aminography.primedatepicker.R.color.yellow400
                        )
                    )
                }
                ArticleState.READ.flag -> {
                    stateView.isVisible = true

                    stateView.setBackgroundColor(
                        context.getColor(
                            com.aminography.primedatepicker.R.color.lightButtonBarPositiveTextColor
                        )
                    )

                }
            }

            container.setOnClickListener {
                EventBus.getDefault().post(ArticleClickEvent(model))
            }
        }
    }

    override fun unbind(view: View) {
//        EventBus.getDefault().unregister(this)
        super.unbind(view)
    }

    override fun getDefaultLayout(): Int = R.layout.item_article

}