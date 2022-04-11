package ru.get.better.ui.welcome

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.annotation.DimenRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_welcome.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import ru.get.better.App
import ru.get.better.R
import ru.get.better.databinding.FragmentWelcomeBinding
import ru.get.better.event.*
import ru.get.better.model.DefaultInterest
import ru.get.better.ui.base.BaseFragment
import ru.get.better.ui.welcome.adapter.WelcomePagerAdapter

class WelcomeFragment : BaseFragment<FragmentWelcomeBinding>(
    R.layout.fragment_welcome,
    Handler::class
) {

    private val adapter: WelcomePagerAdapter by lazy { WelcomePagerAdapter(context!!) }

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)

        EventBus.getDefault().post(ChangeNavViewVisibilityEvent(isVisible = false))

        binding.viewPager.post {
            binding.viewPager.adapter = adapter
            binding.viewPager.offscreenPageLimit = 1
            binding.viewPager.isUserInputEnabled = false
        }
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
        GlobalScope.launch(Dispatchers.IO) {
            EventBus.getDefault().post(ChangeProgressStateEvent(isActive = true))
            EventBus.getDefault().post(
                InitUserInterestsEvent(
                    adapter.getInterests().filter { it !is DefaultInterest.Companion },
                )
            )
        }
    }

    @Subscribe
    fun onSwipeViewPagerEvent(e: SwipeViewPagerEvent) {
        binding.viewPager.currentItem = binding.viewPager.currentItem + 1
    }

    @Subscribe
    fun onSaveInterestsClickedEvent(e: SaveInterestsClickedEvent) {
        GlobalScope.launch(Dispatchers.IO) {
            EventBus.getDefault().post(ChangeProgressStateEvent(isActive = true))
            EventBus.getDefault().post(
                InitUserInterestsEvent(
                    adapter.getInterests().filter { it !is DefaultInterest.Companion },
                )
            )
        }

    }

    inner class Handler {
        fun onSaveInterestsClicked(v: View) {
            GlobalScope.launch(Dispatchers.IO) {
                EventBus.getDefault().post(
                    InitUserInterestsEvent(
                        adapter.getInterests().filter { it !is DefaultInterest.Companion },
                    )
                )
            }
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