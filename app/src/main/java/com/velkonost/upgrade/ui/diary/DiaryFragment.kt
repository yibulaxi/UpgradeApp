package com.velkonost.upgrade.ui.diary

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.velkonost.upgrade.R
import com.velkonost.upgrade.databinding.FragmentDiaryBinding
import com.velkonost.upgrade.event.*
import com.velkonost.upgrade.model.DiaryNote
import com.velkonost.upgrade.ui.base.BaseFragment
import com.velkonost.upgrade.ui.diary.adapter.NotesAdapter
import com.velkonost.upgrade.ui.diary.adapter.viewpager.NotesPagerAdapter
import com.velkonost.upgrade.ui.welcome.WelcomeFragment
import com.velkonost.upgrade.util.ext.getBalloon
import com.velkonost.upgrade.util.ext.setUpRemoveItemTouchHelper
import com.velkonost.upgrade.vm.BaseViewModel
import com.velkonost.upgrade.vm.UserDiaryViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class DiaryFragment : BaseFragment<BaseViewModel, FragmentDiaryBinding>(
    R.layout.fragment_diary,
    BaseViewModel::class,
    Handler::class
) {

    private val userDiaryViewModel: UserDiaryViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(
            UserDiaryViewModel::class.java
        )
    }

    private val viewPagerBehavior: BottomSheetBehavior<ConstraintLayout> by lazy {
        BottomSheetBehavior.from(binding.viewPagerBottomSheet.bottomSheetContainer)
    }

    private lateinit var adapter: NotesAdapter
    private lateinit var pagerAdapter: NotesPagerAdapter

    private var onSwiped: ((DiaryNote) -> Unit)? = {
        EventBus.getDefault().post(DeleteDiaryNoteEvent(it.diaryNoteId))
    }

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)

        setupDiary()

        EventBus.getDefault().post(ChangeNavViewVisibilityEvent(true))

        viewPagerBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.blur.alpha = slideOffset

                if (!binding.blur.isVisible) {
                    binding.blur.isVisible = true
                }
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    binding.blur.alpha = 0f
                    binding.blur.isVisible = false
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    binding.blur.alpha = 1f
                    binding.blur.isVisible = true
                }
            }
        })
    }

    private fun onItemInListSwiped(vh: RecyclerView.ViewHolder, swipeDirection: Int) {
        val swipedPosition = vh.absoluteAdapterPosition
        onSwiped?.invoke(adapter.getNoteAt(swipedPosition))
    }

    private fun setupDiary() {
        userDiaryViewModel.getNotes().observe(this) { notes ->
            if (notes.isEmpty()) {
                binding.emptyText.isVisible = true
                binding.emptyAnim.isVisible = true

                binding.recycler.isVisible = false
            } else {
                binding.emptyText.isVisible = false
                binding.emptyAnim.isVisible = false

                binding.recycler.isVisible = true

                if (!::adapter.isInitialized) {
                    adapter = NotesAdapter(context!!, notes.toMutableList())
                    binding.recycler.adapter = adapter
                    binding.recycler.setUpRemoveItemTouchHelper(
                        R.string.delete,
                        R.dimen.text_size_12,
                        ::onItemInListSwiped
                    )
                } else adapter.updateNotes(notes.toMutableList())

//                if (!::pagerAdapter.isInitialized) {
                pagerAdapter = NotesPagerAdapter(context!!, notes.toMutableList())
                binding.viewPagerBottomSheet.viewPager.adapter = pagerAdapter
                binding.viewPagerBottomSheet.viewPager.offscreenPageLimit = 1
//                } else pagerAdapter.updateNotes(notes.toMutableList())

                val nextItemVisiblePx =
                    resources.getDimension(R.dimen.diary_viewpager_next_item_visible)
                val currentItemHorizontalMarginPx =
                    resources.getDimension(R.dimen.diary_viewpager_current_item_horizontal_margin)
                val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
                val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->
                    page.translationX = -pageTranslationX * position
                }

                binding.viewPagerBottomSheet.viewPager.setPageTransformer(pageTransformer)

                val itemDecoration = WelcomeFragment.HorizontalMarginItemDecoration(
                    context!!,
                    R.dimen.diary_viewpager_current_item_horizontal_margin
                )

                if (binding.viewPagerBottomSheet.viewPager.itemDecorationCount == 0)
                    binding.viewPagerBottomSheet.viewPager.addItemDecoration(itemDecoration)

            }
        }
    }

    @Subscribe
    fun onEditDiaryNoteEvent(e: EditDiaryNoteEvent) {
        viewPagerBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    @Subscribe
    fun onShowNoteDetailEvent(e: ShowNoteDetailEvent) {
        viewPagerBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        binding.blur.isVisible = true
        binding.viewPagerBottomSheet.viewPager.post {
            binding.viewPagerBottomSheet.viewPager.setCurrentItem(
                e.position,
                true
            )
        }

        EventBus.getDefault().post(ChangeNavViewVisibilityEvent(false))
    }

    @Subscribe
    fun onUpdateDiaryEvent(e: UpdateDiaryEvent) {
//        Navigator.refresh(this@DiaryFragment)
    }

    inner class Handler {
        fun onInfoClicked(v: View) {
            getBalloon(getString(R.string.diary_info)).showAlignBottom(binding.info)
        }

        fun onBlurClicked(v: View) {
            viewPagerBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            EventBus.getDefault().post(ChangeNavViewVisibilityEvent(true))
        }
    }
}