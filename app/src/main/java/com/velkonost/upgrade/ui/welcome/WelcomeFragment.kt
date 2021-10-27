package com.velkonost.upgrade.ui.welcome

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.annotation.DimenRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.jaeger.library.StatusBarUtil
import com.velkonost.upgrade.R
import com.velkonost.upgrade.databinding.FragmentSplashBinding
import com.velkonost.upgrade.databinding.FragmentWelcomeBinding
import com.velkonost.upgrade.event.InitUserInterestsEvent
import com.velkonost.upgrade.event.SaveInterestsChangeVisibilityEvent
import com.velkonost.upgrade.ui.base.BaseFragment
import com.velkonost.upgrade.ui.splash.SplashFragment
import com.velkonost.upgrade.ui.splash.SplashViewModel
import com.velkonost.upgrade.ui.welcome.adapter.WelcomePagerAdapter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.lang.Math.abs

class WelcomeFragment : BaseFragment<WelcomeViewModel, FragmentWelcomeBinding>(
    R.layout.fragment_welcome,
    WelcomeViewModel::class,
    Handler::class
) {

    private val adapter: WelcomePagerAdapter by lazy { WelcomePagerAdapter(context!!) }

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)
        StatusBarUtil.setColor(
            requireActivity(),
            ContextCompat.getColor(requireContext(), R.color.colorWhite),
            0
        )
        StatusBarUtil.setLightMode(requireActivity())

        binding.viewPager.adapter = adapter

        binding.viewPager.offscreenPageLimit = 1

        val nextItemVisiblePx = resources.getDimension(R.dimen.viewpager_next_item_visible)
        val currentItemHorizontalMarginPx = resources.getDimension(R.dimen.viewpager_current_item_horizontal_margin)
        val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
        val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->
            page.translationX = -pageTranslationX * position
            // Next line scales the item's height. You can remove it if you don't want this effect
            page.scaleY = 1 - (0.5f * kotlin.math.abs(position))
            // If you want a fading effect uncomment the next line:
             page.alpha = 0.15f + (1 - kotlin.math.abs(position))
        }
        binding.viewPager.setPageTransformer(pageTransformer)

        val itemDecoration = HorizontalMarginItemDecoration(
            context!!,
            R.dimen.viewpager_current_item_horizontal_margin
        )
        binding.viewPager.addItemDecoration(itemDecoration)
    }

    @Subscribe
    fun onSaveInterestsChangeVisibilityEvent(e: SaveInterestsChangeVisibilityEvent) {
        binding.saveInterests.isVisible = e.isVisible
    }

    inner class Handler {
        fun onSaveInterestsClicked(v: View) {
            val map = adapter.getInterests().map {
                it.id.toString() to it.selectedValue
            }.toMap()

            map.plus(adapter.getInterests().map {
                it.id.toString() + "_start" to it.selectedValue
            }).let { EventBus.getDefault().post(InitUserInterestsEvent(it, this@WelcomeFragment)) }

        }
    }

    /**
     * Adds margin to the left and right sides of the RecyclerView item.
     * Adapted from https://stackoverflow.com/a/27664023/4034572
     * @param horizontalMarginInDp the margin resource, in dp.
     */
    class HorizontalMarginItemDecoration(context: Context, @DimenRes horizontalMarginInDp: Int) :
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