package ru.get.better.ui.diary

import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.shuhart.stickyheader.StickyHeaderItemDecorator
import com.takusemba.spotlight.Spotlight
import com.takusemba.spotlight.effet.RippleEffect
import com.takusemba.spotlight.shape.RoundedRectangle
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import ru.get.better.App
import ru.get.better.R
import ru.get.better.databinding.FragmentDiaryBinding
import ru.get.better.event.*
import ru.get.better.model.DiaryNote
import ru.get.better.model.NoteType
import ru.get.better.model.getHabitsRealization
import ru.get.better.rest.UserSettingsFields
import ru.get.better.ui.base.BaseFragment
import ru.get.better.ui.diary.adapter.NotesAdapter
import ru.get.better.ui.diary.adapter.habits.HabitsAdapter
import ru.get.better.ui.diary.adapter.viewpager.NotesPagerAdapter
import ru.get.better.ui.welcome.WelcomeFragment
import ru.get.better.util.ext.getBalloon
import ru.get.better.util.ext.setUpRemoveItemTouchHelper
import ru.get.better.util.stickyheader.Section
import ru.get.better.util.stickyheader.SectionHeader
import ru.get.better.util.stickyheader.SectionItem
import ru.get.better.vm.*
import java.text.SimpleDateFormat
import java.util.*


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

    private val userSettingsViewModel: UserSettingsViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(
            UserSettingsViewModel::class.java
        )
    }

    private val viewPagerBehavior: BottomSheetBehavior<ConstraintLayout> by lazy {
        BottomSheetBehavior.from(binding.viewPagerBottomSheet.bottomSheetContainer)
    }

    private lateinit var adapter: NotesAdapter
    private lateinit var pagerAdapter: NotesPagerAdapter
    private lateinit var habitsAdapter: HabitsAdapter

    private var allowShowHabitsSpotlight: Boolean = false

    private var onSwiped: ((DiaryNote) -> Unit)? = {
        EventBus.getDefault().post(DeleteDiaryNoteEvent(it.diaryNoteId))
    }

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)

        EventBus.getDefault().post(ChangeNavViewVisibilityEvent(true))
        EventBus.getDefault().post(ChangeProgressStateEvent(true))
        EventBus.getDefault().post(TryShowSpotlightEvent(SpotlightType.DiaryHabits))

        setupDiary()
        setupHabitsRealization()

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
                    EventBus.getDefault().post(ChangeNavViewVisibilityEvent(true))
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    binding.blur.alpha = 1f
                    binding.blur.isVisible = true
                    EventBus.getDefault().post(ChangeNavViewVisibilityEvent(false))
                }
            }
        })

    }

    @Subscribe
    fun onShowSpotlightEvent(e: ShowSpotlightEvent) {
        if (e.spotlightType == SpotlightType.DiaryHabits)
            allowShowHabitsSpotlight = true

    }

    @Subscribe
    fun onBackPressedEvent(e: BackPressedEvent) {
        if (viewPagerBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
            viewPagerBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun onItemInListSwiped(vh: RecyclerView.ViewHolder, swipeDirection: Int) {
        val swipedPosition = vh.absoluteAdapterPosition
        onSwiped?.invoke(adapter.getNoteAt(swipedPosition))
    }

    private fun setupHabitsRealization() {
        userDiaryViewModel.getHabits().observe(this) { habits ->
            val habitsRealization = arrayListOf<DiaryNote>()

            habits.forEach { habit ->
                habit!!.datesCompletion!!.firstOrNull { dateCompletion ->
                    dateCompletion.datesCompletionIsCompleted == false
                            && dateCompletion.datesCompletionDatetime!!.toLong() <= System.currentTimeMillis()
                }.let {
                    if (it != null) habitsRealization.add(habit)
                }
            }

            habitsAdapter = HabitsAdapter(requireContext(), habitsRealization.toMutableList())
            binding.horizontalRecycler.adapter = habitsAdapter

            binding.horizontalRecycler.post {
                if (habitsAdapter.itemCount != 0 && allowShowHabitsSpotlight)
                    showHabitsSpotlight()
            }
        }
    }

    private fun setupDiary() {
        userDiaryViewModel.getNotes().observe(this) { notes ->
            val notesWithoutHabits = notes.filter { it.noteType != NoteType.Habit.id }

            val habits = notes.filter { it.noteType == NoteType.Habit.id }.getHabitsRealization()

            val allNotes = notesWithoutHabits.plus(habits)


            if (allNotes.isEmpty()) {
                binding.emptyText.isVisible = true
                binding.emptyAnim.isVisible = true

                binding.recycler.isVisible = false

                EventBus.getDefault().post(ChangeProgressStateEvent(false))
            } else {
                binding.emptyText.isVisible = false
                binding.emptyAnim.isVisible = false

                binding.recycler.isVisible = true

                Collections.sort(allNotes, DateComparator())
                if (!::adapter.isInitialized) {


                    val datesSet = linkedSetOf<String>()
                    val formatter = SimpleDateFormat("MMMM, yyyy")

                    allNotes.forEach {
                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = it.date.toLong()
                        datesSet.add(
                            formatter.format(calendar.time)
                        )
                    }

                    Collections.sort(datesSet.toList(), StringDateComparator())

                    adapter = NotesAdapter(
                        context = requireContext(),
                        datesSet = datesSet
                    )
                    binding.recycler.adapter = adapter

                    val decorator = StickyHeaderItemDecorator(adapter)
                    decorator.attachToRecyclerView(binding.recycler)

                    var section = -1
                    var sectionName = "--------------"

                    val items = arrayListOf<Section>()
                    for (i in allNotes.indices) {
                        if (
                            !formatter.format(allNotes[i].date.toLong())
                                .contains(sectionName)
                        ) {
                            section++
                            sectionName = datesSet.elementAt(section)
                            items.add(SectionHeader(section, sectionName))
                        }
                        items.add(SectionItem(section, sectionName, allNotes[i]))
                    }

                    adapter.items = items

                    binding.recycler.setUpRemoveItemTouchHelper(
                        R.string.delete,
                        R.dimen.text_size_12,
                        ::onItemInListSwiped
                    )

                } else adapter.updateNotes(allNotes.toMutableList())

                pagerAdapter = NotesPagerAdapter(context!!, allNotes.toMutableList())
                binding.viewPagerBottomSheet.viewPager.adapter = pagerAdapter
                binding.viewPagerBottomSheet.viewPager.offscreenPageLimit = 1

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


                binding.viewPagerBottomSheet.viewPager.post {
                    EventBus.getDefault().post(ChangeProgressStateEvent(false))
                }
            }
        }
    }

    @Subscribe
    fun onHabitRealizationCompletedEvent(e: HabitRealizationCompletedEvent) {
        userDiaryViewModel.setNote(e.habitRealization)
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

    private fun showHabitsSpotlight() {
        val habitsTargetLayout =
            layoutInflater.inflate(R.layout.target_diary_habits, FrameLayout(requireContext()))
        val habitsTarget = com.takusemba.spotlight.Target.Builder()
            .setAnchor(binding.horizontalRecycler)
            .setShape(
                RoundedRectangle(
                    binding.horizontalRecycler.height.toFloat(),
                    binding.horizontalRecycler.width.toFloat() - 25f,
                    16f
                )
            )
            .setEffect(
                RippleEffect(
                    100f,
                    200f,
                    ContextCompat.getColor(requireContext(), R.color.colorTgPrimary)
                )
            )
            .setOverlay(habitsTargetLayout)
            .build()

        val spotlight = Spotlight.Builder(requireActivity())
            .setTargets(habitsTarget)
            .setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorTgPrimary))
            .setDuration(1000L)
            .setAnimation(DecelerateInterpolator(2f))
            .setContainer(binding.container)
            .build()
        spotlight.start()
        EventBus.getDefault().post(ChangeIsAnySpotlightActiveNowEvent(true))


        habitsTargetLayout.findViewById<ConstraintLayout>(R.id.container).setOnClickListener {
            spotlight.finish()

            App.preferences.isDiaryHabitsSpotlightShown = true
            EventBus.getDefault().post(ChangeIsAnySpotlightActiveNowEvent(false))
            userSettingsViewModel.updateField(UserSettingsFields.IsDiaryHabitsSpotlightShown, true)
        }
    }

    inner class Handler {
        fun onInfoClicked(v: View) {
            getBalloon(getString(R.string.diary_info)).showAlignBottom(binding.info)
        }

        fun onBlurClicked(v: View) {
            viewPagerBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
//            EventBus.getDefault().post(ChangeNavViewVisibilityEvent(true))
        }
    }
}