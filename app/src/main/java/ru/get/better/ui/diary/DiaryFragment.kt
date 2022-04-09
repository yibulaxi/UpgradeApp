package ru.get.better.ui.diary

import android.content.res.ColorStateList
import android.media.metrics.Event
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.shuhart.stickyheader.StickyHeaderItemDecorator
import com.takusemba.spotlight.Spotlight
import com.takusemba.spotlight.effet.RippleEffect
import com.takusemba.spotlight.shape.RoundedRectangle
import kotlinx.android.synthetic.main.target_diary_habits.view.*
import kotlinx.coroutines.*
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
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.*
import ru.get.better.ui.activity.main.ext.SecondaryViews
import java.lang.Thread.sleep
import java.util.concurrent.Executors
import kotlin.coroutines.coroutineContext


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

        lifecycleScope.launch(Dispatchers.IO) {
            sleep(500)
        }.invokeOnCompletion {
            lifecycleScope.launch(Dispatchers.Main) {
                setupLogic()
            }
        }
    }

    private fun setupLogic() {
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

    private fun observeDiary(notes: List<DiaryNote>) {
        val notesWithoutHabits = notes.filter { it.noteType != NoteType.Habit.id }

        val habits = notes.filter { it.noteType == NoteType.Habit.id }.getHabitsRealization()

        Log.d("keke", "step3")
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
                val formatter = SimpleDateFormat("MMMM, yyyy", Locale(App.preferences.locale))

                allNotes.forEach {
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = it.date.toLong()
                    datesSet.add(
                        formatter.format(calendar.time)
                    )
                }

                Collections.sort(datesSet.toList(), StringDateComparator())
                Collections.sort(datesSet.toList(), Collections.reverseOrder())

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
//
//                var reversedAllNotes = mutableListOf<DiaryNote>()
//                reversedAllNotes.addAll(allNotes)
//                reversedAllNotes = reversedAllNotes.reversed().tob

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
                    ::onItemInListSwiped
                )

            } else adapter.updateNotes(allNotes.toMutableList())

            pagerAdapter = NotesPagerAdapter(context!!, allNotes.toMutableList())
            binding.viewPagerBottomSheet.viewPager.adapter = pagerAdapter
            binding.viewPagerBottomSheet.viewPager.offscreenPageLimit = 1

            val nextItemVisiblePx =
                resources.getDimension(R.dimen.diary_viewpager_next_item_visible_new)
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

    override fun updateThemeAndLocale() {
        binding.title.text = App.resourcesProvider.getStringLocale(R.string.diary)
        binding.emptyText.text = App.resourcesProvider.getStringLocale(R.string.diary_empty_text)

        if (::adapter.isInitialized)
            adapter.notifyDataSetChanged()

        if (::habitsAdapter.isInitialized)
            habitsAdapter.notifyDataSetChanged()

        if (::pagerAdapter.isInitialized)
            pagerAdapter.notifyDataSetChanged()

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

        binding.emptyText.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentDiaryEmptyTextText
                else R.color.colorLightFragmentDiaryEmptyTextText
            )
        )

        binding.info.imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkFragmentDiaryInfoTint
                else R.color.colorLightFragmentDiaryInfoTint
            )
        )

        binding.blur.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkBlur
                else R.color.colorLightBlur
            )
        )
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
        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
            val habits = userDiaryViewModel.getHabits()

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

//        userDiaryViewModel.getHabits().observe(this) { habits ->
//            val habitsRealization = arrayListOf<DiaryNote>()
//
//            habits.forEach { habit ->
//                habit!!.datesCompletion!!.firstOrNull { dateCompletion ->
//                    dateCompletion.datesCompletionIsCompleted == false
//                            && dateCompletion.datesCompletionDatetime!!.toLong() <= System.currentTimeMillis()
//                }.let {
//                    if (it != null) habitsRealization.add(habit)
//                }
//            }
//
//            habitsAdapter = HabitsAdapter(requireContext(), habitsRealization.toMutableList())
//            binding.horizontalRecycler.adapter = habitsAdapter
//
//            binding.horizontalRecycler.post {
//                if (habitsAdapter.itemCount != 0 && allowShowHabitsSpotlight)
//                    showHabitsSpotlight()
//            }
//        }
    }

    private fun setupDiary() {
        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
            val notes = userDiaryViewModel.getNotes()
            observeDiary(notes)
        }
//        userDiaryViewModel.getNotes().observe(this) { notes ->
////            lifecycleScope.launch(Dispatchers.IO) {
//                observeDiary(notes)
////            }
////
//        }
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
        habitsTargetLayout.title.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkTargetDiaryHabitsTitleText
                else R.color.colorLightTargetDiaryHabitsTitleText
            )
        )

        habitsTargetLayout.title.text =
            App.resourcesProvider.getStringLocale(R.string.habits_spotlight)

        habitsTargetLayout.icArrow.imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkTargetDiaryHabitsIcArrowTint
                else R.color.colorLightTargetDiaryHabitsIcArrowTint
            )
        )

        val habitsTarget = com.takusemba.spotlight.Target.Builder()
            .setAnchor(binding.habitsSpotlightView)
            .setShape(
                RoundedRectangle(
                    binding.habitsSpotlightView.height.toFloat(),
                    binding.habitsSpotlightView.width.toFloat() - 25f,
                    16f
                )
            )
            .setEffect(
                RippleEffect(
                    100f,
                    200f,
                    ContextCompat.getColor(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.color.colorDarkHabitsSpotlightTarget
                        else R.color.colorLightHabitsSpotlightTarget
                    )
                )
            )
            .setOverlay(habitsTargetLayout)
            .build()

        val spotlight = Spotlight.Builder(requireActivity())
            .setTargets(habitsTarget)
            .setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkHabitsSpotlightBackground
                    else R.color.colorLightHabitsSpotlightBackground
                )
            )
            .setDuration(1000L)
            .setAnimation(DecelerateInterpolator(2f))
            .setContainer(binding.container)
            .build()
        spotlight.start()

        EventBus.getDefault().post(SecondaryViewUpdateStateEvent(newState = SecondaryViews.DiarySpotlight))
        EventBus.getDefault().post(ChangeIsAnySpotlightActiveNowEvent(true))


        habitsTargetLayout.findViewById<ConstraintLayout>(R.id.container).setOnClickListener {
            spotlight.finish()
            EventBus.getDefault().post(SecondaryViewUpdateStateEvent(newState = SecondaryViews.Empty))

            App.preferences.isDiaryHabitsSpotlightShown = true
            EventBus.getDefault().post(ChangeIsAnySpotlightActiveNowEvent(false))

            App.preferences.isDiaryHabitsSpotlightShown = true
//            userSettingsViewModel.updateField(UserSettingsFields.IsDiaryHabitsSpotlightShown, true)
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