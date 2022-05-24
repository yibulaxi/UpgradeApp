package ru.get.better.ui.articles

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import github.com.st235.lib_expandablebottombar.MenuItem
import github.com.st235.lib_expandablebottombar.MenuItemDescriptor
import kotlinx.android.synthetic.main.view_article.view.*
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import ru.get.better.App
import ru.get.better.R
import ru.get.better.databinding.FragmentAchievementsBinding
import ru.get.better.databinding.FragmentArticlesBinding
import ru.get.better.event.ArticleClickEvent
import ru.get.better.event.BackPressedEvent
import ru.get.better.event.ChangeNavViewVisibilityEvent
import ru.get.better.model.Article
import ru.get.better.model.ArticleState
import ru.get.better.model.ArticleType
import ru.get.better.model.NoteType
import ru.get.better.ui.achievements.AchievementsFragment
import ru.get.better.ui.articles.adapter.ArticlesAdapter
import ru.get.better.ui.base.BaseFragment
import ru.get.better.vm.ArticlesViewModel
import ru.get.better.vm.UserAchievementsViewModel
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnScrollChangedListener
import android.widget.ScrollView
import kotlinx.coroutines.Dispatchers


class ArticlesFragment : BaseFragment<FragmentArticlesBinding>(
    ru.get.better.R.layout.fragment_articles,
    Handler::class
) {

    private var selectedType = ArticleType.SHORT
    private var openedArticle: Article? = null

    private val articlesViewModel: ArticlesViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(
            ArticlesViewModel::class.java
        )
    }

    private val articlesAdapter: ArticlesAdapter by lazy {
        ArticlesAdapter()
    }

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)

        setupTypesBar()
        binding.recycler.adapter = articlesAdapter
    }

    override fun onViewModelReady() {
        super.onViewModelReady()

        articlesViewModel.getArticles(ArticleType.SHORT)

        articlesViewModel.currentArticlesLiveData.observe(this) { list ->

            when(selectedType) {
                ArticleType.SHORT -> App.analyticsEventsManager.tab4ShortArticleShown()
                ArticleType.MID -> App.analyticsEventsManager.tab4MidArticleShown()
                ArticleType.LONG -> App.analyticsEventsManager.tab4LongArticleShown()
            }

            articlesAdapter.createList(list)

            binding.amountArticles.text = "${list.count { it.flag == ArticleState.READ.flag }} / ${list.size}"
        }
    }

    override fun updateThemeAndLocale() {
        super.updateThemeAndLocale()

        binding.title.text = App.resourcesProvider.getStringLocale(R.string.articles)

        binding.container.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentDiaryBackground
                else R.color.colorLightFragmentDiaryBackground
            )
        )

        binding.title.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentDiaryTitleText
                else R.color.colorLightFragmentDiaryTitleText
            )
        )

        binding.amountArticles.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentDiaryTitleText
                else R.color.colorLightFragmentDiaryTitleText
            )
        )

        binding.info.imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentDiaryInfoTint
                else R.color.colorLightFragmentDiaryInfoTint
            )
        )

        binding.amountCard.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkNavViewContainerBackgroundTint
                else R.color.colorLightNavViewContainerBackgroundTint
            ))

        binding.viewArticle.articleContainer.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentDiaryBackground
                else R.color.colorLightFragmentDiaryBackground
            )
        )

        binding.viewArticle.title.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentDiaryTitleText
                else R.color.colorLightFragmentDiaryTitleText
            )
        )

        binding.viewArticle.text.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentDiaryTitleText
                else R.color.colorLightFragmentDiaryTitleText
            )
        )

        binding.viewArticle.icArrowBack.imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                if (!App.preferences.isDarkTheme) R.color.colorDarkFragmentDiaryInfoTint
                else R.color.colorLightFragmentDiaryInfoTint
            )
        )

    }

    private fun setupTypesBar() {
        binding.typesBar.setBackgroundColor(
            ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.colorDarkNavViewContainerBackgroundTint
            else R.color.colorLightNavViewContainerBackgroundTint
        ))

        binding.typesBar.menu.add(
            MenuItemDescriptor.Builder(
                requireContext(),
                R.id.filter_articles_short,
                ru.get.better.R.drawable.ic_article_short,
                R.string.articles_short,
                ContextCompat.getColor(
                    requireContext(),
                    if (!App.preferences.isDarkTheme) R.color.colorDarkNavigationBar
                    else R.color.colorLightNavigationBar
                )
            ).build()
        )

        binding.typesBar.menu.add(
            MenuItemDescriptor.Builder(
                requireContext(),
                R.id.filter_articles_mid,
                ru.get.better.R.drawable.ic_article_mid,
                R.string.articles_mid,
                ContextCompat.getColor(
                    requireContext(),
                    if (!App.preferences.isDarkTheme) R.color.colorDarkNavigationBar
                    else R.color.colorLightNavigationBar
                )
            ).build()
        )

        binding.typesBar.menu.add(
            MenuItemDescriptor.Builder(
                requireContext(),
                R.id.filter_articles_long,
                ru.get.better.R.drawable.ic_article_long,
                R.string.articles_long,
                ContextCompat.getColor(
                    requireContext(),
                    if (!App.preferences.isDarkTheme) R.color.colorDarkNavigationBar
                    else R.color.colorLightNavigationBar
                )
            ).build()
        )

        binding.typesBar.onItemSelectedListener = { view: View, menuItem: MenuItem, b: Boolean ->
            when(menuItem.id) {
                R.id.filter_articles_short -> {
                    App.analyticsEventsManager.tab4ShortArticlesTapped()

                    selectedType = ArticleType.SHORT
                    articlesViewModel.getArticles(ArticleType.SHORT)
                }
                R.id.filter_articles_mid -> {
                    App.analyticsEventsManager.tab4MidArticlesTapped()

                    selectedType = ArticleType.MID
                    articlesViewModel.getArticles(ArticleType.MID)
                }
                R.id.filter_articles_long -> {
                    App.analyticsEventsManager.tab4LongArticlesTapped()

                    selectedType = ArticleType.LONG
                    articlesViewModel.getArticles(ArticleType.LONG)
                }
            }

//            lifecycleScope.launch { userDiaryViewModel.updateFilteredNotes() }
        }
    }

    @Subscribe
    fun onBackPressedEvent(e: BackPressedEvent) {
        if (binding.viewArticle.articleContainer.alpha == 1f)
            binding.viewArticle.articleContainer
                .animate()
                .alpha(0f)
                .scaleX(0f)
                .scaleY(0f)
                .setDuration(500)
                .withEndAction {
                    binding.viewArticle.isVisible = false
                }
        EventBus.getDefault().post(ChangeNavViewVisibilityEvent(true))
        openedArticle = null
    }

    @Subscribe
    fun onArticleClickEvent(e: ArticleClickEvent) {
        binding.viewArticle.isVisible = true
        binding.viewArticle.articleContainer
            .animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(500)
            .withEndAction {
                if (
                    openedArticle != null
                    && openedArticle!!.flag == ArticleState.UNREAD.flag
                ) {
                    openedArticle!!.flag = ArticleState.OPENED.flag
                    articlesViewModel.updateReadStateArticle(openedArticle!!._id, ArticleState.OPENED) {
//                        articlesViewModel.getArticles(ArticleType.SHORT)

                    }
                    articlesAdapter.updateArticle(openedArticle!!)
                }

                binding.viewArticle.articleScroll.viewTreeObserver
                    .addOnScrollChangedListener {
                        if (openedArticle != null) {

                            lifecycleScope.launch(Dispatchers.IO) {
                                val scrollViewHeight: Double =
                                    binding.viewArticle.articleScroll.getChildAt(0).bottom.toDouble() - binding.viewArticle.articleScroll.height
                                val getScrollY: Double =
                                    binding.viewArticle.articleScroll.scrollY.toDouble()
                                val scrollPosition = getScrollY / scrollViewHeight * 100.0

                                if (scrollPosition > 50 && openedArticle!!.flag != ArticleState.READ.flag) {
                                    when(selectedType) {
                                        ArticleType.SHORT -> App.analyticsEventsManager.tab4ShortArticleCompletelyRead()
                                        ArticleType.MID -> App.analyticsEventsManager.tab4MidArticleCompletelyRead()
                                        ArticleType.LONG -> App.analyticsEventsManager.tab4LongArticleCompletelyRead()
                                    }

                                    openedArticle!!.flag = ArticleState.READ.flag
                                    articlesViewModel.updateReadStateArticle(
                                        openedArticle!!._id,
                                        ArticleState.READ
                                    ) {

//                                        articlesViewModel.getArticles(ArticleType.SHORT)
                                    }
                                    lifecycleScope.launch(Dispatchers.Main) {
                                        articlesAdapter.updateArticle(openedArticle!!)
                                    }

                                }
                            }
                        }
                    }

            }
        EventBus.getDefault().post(ChangeNavViewVisibilityEvent(false))

        openedArticle = e.article

        binding.viewArticle.title.text = Html.fromHtml(e.article.name)
        binding.viewArticle.text.text = Html.fromHtml(e.article.text)

        binding.viewArticle.amount.text = "Время чтения ${e.article.readTime} мин."

        binding.viewArticle.articleScroll.fullScroll(ScrollView.FOCUS_UP);

        binding.viewArticle.arrowBack.setOnClickListener {
            binding.viewArticle.articleContainer
                .animate()
                .alpha(0f)
                .scaleX(0f)
                .scaleY(0f)
                .setDuration(500)
                .withEndAction {
                    binding.viewArticle.isVisible = false
                }
            EventBus.getDefault().post(ChangeNavViewVisibilityEvent(true))
            openedArticle = null
        }
    }

    inner class Handler {
        fun onInfoClicked(v: View) {

        }
    }
}