package com.velkonost.upgrade.ui.diary

import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.jaeger.library.StatusBarUtil
import com.skydoves.balloon.*
import com.velkonost.upgrade.R
import com.velkonost.upgrade.databinding.FragmentDiaryBinding
import com.velkonost.upgrade.event.*
import com.velkonost.upgrade.model.DiaryNote
import com.velkonost.upgrade.navigation.Navigator
import com.velkonost.upgrade.ui.HomeViewModel
import com.velkonost.upgrade.ui.base.BaseFragment
import com.velkonost.upgrade.ui.diary.adapter.NotesAdapter
import com.velkonost.upgrade.ui.diary.adapter.viewpager.NotesPagerAdapter
import com.velkonost.upgrade.ui.welcome.WelcomeFragment
import com.velkonost.upgrade.util.ext.setUpRemoveItemTouchHelper
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class DiaryFragment : BaseFragment<HomeViewModel, FragmentDiaryBinding>(
    R.layout.fragment_diary,
    HomeViewModel::class,
    Handler::class
) {
    private lateinit var adapter: NotesAdapter
    private lateinit var pagerAdapter: NotesPagerAdapter

    private var onSwiped: ((DiaryNote) -> Unit)? = {
        EventBus.getDefault().post(DeleteDiaryNoteEvent(it.id))
    }

    val balloon: Balloon by lazy {
        Balloon.Builder(context!!)
            .setArrowSize(10)
            .setArrowOrientation(ArrowOrientation.TOP)
            .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
            .setArrowPosition(0.5f)
            .setTextGravity(Gravity.START)
            .setPadding(10)
            .setWidth(BalloonSizeSpec.WRAP)
            .setHeight(BalloonSizeSpec.WRAP)
            .setTextSize(15f)
            .setCornerRadius(4f)
            .setAlpha(0.9f)
            .setText(getString(R.string.diary_info))
            .setTextColor(ContextCompat.getColor(context!!, R.color.colorWhite))
            .setTextIsHtml(true)
            .setBackgroundColor(ContextCompat.getColor(context!!, R.color.colorBlueLight))
            .setBalloonAnimation(BalloonAnimation.FADE)
            .build()
    }


    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)
        StatusBarUtil.setColor(
            requireActivity(),
            ContextCompat.getColor(requireContext(), R.color.colorWhite),
            0
        )
        StatusBarUtil.setLightMode(requireActivity())

        binding.viewModel = ViewModelProviders.of(requireActivity()).get(HomeViewModel::class.java)

        setupDiary()

        EventBus.getDefault().post(ChangeNavViewVisibilityEvent(true))
    }

    private fun onItemInListSwiped(vh: RecyclerView.ViewHolder, swipeDirection: Int) {
        val swipedPosition = vh.absoluteAdapterPosition
        onSwiped?.invoke(adapter.getNoteAt(swipedPosition))
        adapter.removeNoteAt(swipedPosition)

        pagerAdapter.notifyDataSetChanged()
    }

    private fun setupDiary() {
        if (binding.viewModel!!.getDiary().notes.size == 0) {
            binding.emptyText.isVisible = true
            binding.emptyAnim.isVisible = true

            binding.recycler.isVisible = false
        } else {
            binding.emptyText.isVisible = false
            binding.emptyAnim.isVisible = false

            binding.recycler.isVisible = true

            adapter = NotesAdapter(context!!, binding.viewModel!!.getDiary().notes)
            binding.recycler.adapter = adapter
            binding.recycler.setUpRemoveItemTouchHelper(
                R.string.delete,
                R.dimen.text_size_12,
                ::onItemInListSwiped
            )

            pagerAdapter = NotesPagerAdapter(context!!, binding.viewModel!!.getDiary().notes)
            binding.viewPager.adapter = pagerAdapter
            binding.viewPager.offscreenPageLimit = 1

            val nextItemVisiblePx =
                resources.getDimension(R.dimen.diary_viewpager_next_item_visible)
            val currentItemHorizontalMarginPx =
                resources.getDimension(R.dimen.diary_viewpager_current_item_horizontal_margin)
            val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
            val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->
                page.translationX = -pageTranslationX * position
            }
            binding.viewPager.setPageTransformer(pageTransformer)

            val itemDecoration = WelcomeFragment.HorizontalMarginItemDecoration(
                context!!,
                R.dimen.diary_viewpager_current_item_horizontal_margin
            )
            binding.viewPager.addItemDecoration(itemDecoration)

        }
    }

    @Subscribe
    fun onEditDiaryNoteEvent(e: EditDiaryNoteEvent) {
        binding.viewPager.isVisible = false
        binding.blur.isVisible = false
    }

    @Subscribe
    fun onShowNoteDetailEvent(e: ShowNoteDetailEvent) {
        binding.viewPager.isVisible = true
        binding.blur.isVisible = true
        binding.viewPager.post { binding.viewPager.setCurrentItem(e.position, true) }

        EventBus.getDefault().post(ChangeNavViewVisibilityEvent(false))
    }

    @Subscribe
    fun onUpdateDiaryEvent(e: UpdateDiaryEvent) {
        Navigator.refresh(this@DiaryFragment)
    }

    inner class Handler {
        fun onInfoClicked(v: View) {
            balloon.showAlignBottom(binding.info)
        }

        fun onBlurClicked(v: View) {
            binding.viewPager.isVisible = false
            binding.blur.isVisible = false
            EventBus.getDefault().post(ChangeNavViewVisibilityEvent(true))
        }
    }
}