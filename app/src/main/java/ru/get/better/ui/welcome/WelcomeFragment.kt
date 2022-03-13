package ru.get.better.ui.welcome

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.annotation.DimenRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_welcome.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import ru.get.better.App
import ru.get.better.R
import ru.get.better.databinding.FragmentWelcomeBinding
import ru.get.better.event.*
import ru.get.better.model.DefaultInterest
import ru.get.better.ui.base.BaseFragment
import ru.get.better.ui.welcome.adapter.WelcomePagerAdapter

class WelcomeFragment : BaseFragment<WelcomeViewModel, FragmentWelcomeBinding>(
    R.layout.fragment_welcome,
    WelcomeViewModel::class,
    Handler::class
) {

    private val adapter: WelcomePagerAdapter by lazy { WelcomePagerAdapter(context!!) }

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)

        EventBus.getDefault().post(ChangeNavViewVisibilityEvent(isVisible = false))

        binding.viewPager.adapter = adapter

        binding.viewPager.offscreenPageLimit = 1

//        val nextItemVisiblePx = resources.getDimension(R.dimen.viewpager_next_item_visible)
//        val currentItemHorizontalMarginPx =
//            resources.getDimension(R.dimen.viewpager_current_item_horizontal_margin)
//        val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
//        val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->
//            page.translationX = -pageTranslationX * position
//            // Next line scales the item's height. You can remove it if you don't want this effect
//            page.scaleY = 1 - (0.5f * kotlin.math.abs(position))
//            // If you want a fading effect uncomment the next line:
//            page.alpha = 0.15f + (1 - kotlin.math.abs(position))
//        }
//        binding.viewPager.setPageTransformer(pageTransformer)
//
//        val itemDecoration = HorizontalMarginItemDecoration(
//            context!!,
//            R.dimen.viewpager_current_item_horizontal_margin
//        )
//        binding.viewPager.addItemDecoration(itemDecoration)
        binding.viewPager.isUserInputEnabled = false
    }

    override fun updateThemeAndLocale() {

        binding.tvMessage.text = App.resourcesProvider.getStringLocale(R.string.save)

        adapter.notifyDataSetChanged()

        background.background = ContextCompat.getDrawable(
            requireContext(),
            if (App.preferences.isDarkTheme) R.drawable.snack_neutral_gradient_dark
            else R.drawable.snack_neutral_gradient_light
        )

        binding.tvMessage.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentWelcomeTvMessageText
                else R.color.colorLightFragmentWelcomeTvMessageText
            )
        )
    }

    @Subscribe
    fun onSkipWelcomeEvent(e: SkipWelcomeEvent) {
        EventBus.getDefault().post(ChangeProgressStateEvent(isActive = true))
        EventBus.getDefault().post(
            InitUserInterestsEvent(
                adapter.getInterests().filter { it !is DefaultInterest.Companion },
                this@WelcomeFragment
            )
        )
    }

    @Subscribe
    fun onSaveInterestsChangeVisibilityEvent(e: SaveInterestsChangeVisibilityEvent) {
        binding.saveInterests.isVisible = e.isVisible
    }

    @Subscribe
    fun onSwipeViewPagerEvent(e: SwipeViewPagerEvent) {
        binding.viewPager.currentItem = binding.viewPager.currentItem + 1
    }

    @Subscribe
    fun onSaveInterestsClickedEvent(e: SaveInterestsClickedEvent) {
        EventBus.getDefault().post(ChangeProgressStateEvent(isActive = true))
        EventBus.getDefault().post(
            InitUserInterestsEvent(
                adapter.getInterests().filter { it !is DefaultInterest.Companion },
                this@WelcomeFragment
            )
        )
    }

    inner class Handler {
        fun onSaveInterestsClicked(v: View) {
            EventBus.getDefault().post(
                InitUserInterestsEvent(
                    adapter.getInterests().filter { it !is DefaultInterest.Companion },
                    this@WelcomeFragment
                )
            )
        }
    }

    /**
     * Adds margin to the left and right sides of the RecyclerView item.
     * Adapted from https://stackoverflow.com/a/27664023/4034572
     * @param horizontalMarginInDp the margin resource, in dp.
     */
    internal class HorizontalMarginItemDecoration(
        context: Context,
        @DimenRes horizontalMarginInDp: Int
    ) :
        RecyclerView.ItemDecoration() {

        private val horizontalMarginInPx: Int =
            context.resources.getDimension(horizontalMarginInDp).toInt()

        override fun getItemOffsets(
            outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
        ) {
            outRect.right = horizontalMarginInPx
            outRect.left = horizontalMarginInPx
        }

    }
}