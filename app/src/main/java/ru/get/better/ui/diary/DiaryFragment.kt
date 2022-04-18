package ru.get.better.ui.diary

import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.SparseIntArray
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.aminography.primecalendar.civil.CivilCalendar
import com.aminography.primedatepicker.calendarview.PrimeCalendarView
import com.aminography.primedatepicker.common.BackgroundShapeType
import com.aminography.primedatepicker.common.LabelFormatter
import com.aminography.primedatepicker.picker.PrimeDatePicker
import com.aminography.primedatepicker.picker.theme.LightThemeFactory
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.shuhart.stickyheader.StickyHeaderItemDecorator
import com.takusemba.spotlight.Spotlight
import com.takusemba.spotlight.effet.RippleEffect
import com.takusemba.spotlight.shape.RoundedRectangle
import github.com.st235.lib_expandablebottombar.MenuItem
import github.com.st235.lib_expandablebottombar.MenuItemDescriptor
import kotlinx.android.synthetic.main.target_diary_habits.view.*
import kotlinx.android.synthetic.main.view_goal_add.*
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import ru.get.better.App
import ru.get.better.R
import ru.get.better.databinding.FragmentDiaryBinding
import ru.get.better.event.*
import ru.get.better.model.DiaryNote
import ru.get.better.model.NoteType
import ru.get.better.model.getHabitsRealization
import ru.get.better.ui.activity.main.ext.SecondaryViews
import ru.get.better.ui.base.BaseFragment
import ru.get.better.ui.diary.adapter.NotesAdapter
import ru.get.better.ui.diary.adapter.filter.FilterTagsAdapter
import ru.get.better.ui.diary.adapter.habits.HabitsAdapter
import ru.get.better.ui.diary.adapter.viewpager.NotesPagerAdapter
import ru.get.better.ui.welcome.WelcomeFragment
import ru.get.better.util.ext.getBalloon
import ru.get.better.util.ext.setUpRemoveItemTouchHelper
import ru.get.better.util.stickyheader.Section
import ru.get.better.util.stickyheader.SectionHeader
import ru.get.better.util.stickyheader.SectionItem
import ru.get.better.vm.DateComparator
import ru.get.better.vm.StringDateComparator
import ru.get.better.vm.UserDiaryViewModel
import ru.get.better.vm.UserSettingsViewModel
import java.lang.Thread.sleep
import java.text.SimpleDateFormat
import java.util.*


class DiaryFragment : BaseFragment<FragmentDiaryBinding>(
    R.layout.fragment_diary,
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

    private val filterTagsBehavior: BottomSheetBehavior<ConstraintLayout> by lazy {
        BottomSheetBehavior.from(binding.tagsBottomSheet.bottomSheetContainer)
    }

    private val viewPagerBehavior: BottomSheetBehavior<ConstraintLayout> by lazy {
        BottomSheetBehavior.from(binding.viewPagerBottomSheet.bottomSheetContainer)
    }

    private val filterTagsAdapter: FilterTagsAdapter by lazy {
        FilterTagsAdapter()
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

    private fun setupFilterTags() {
        if (userDiaryViewModel.filterData.allTags.isNullOrEmpty()) {
            binding.filterTags.isVisible = false
            return
        }

        binding.filterTags.isVisible = true

        val lm = FlexboxLayoutManager(context)
        lm.flexDirection = FlexDirection.ROW
        lm.justifyContent = JustifyContent.FLEX_START

        binding.tagsBottomSheet.tagsRecycler.layoutManager = lm

        binding.tagsBottomSheet.tagsRecycler.adapter = filterTagsAdapter
        filterTagsAdapter.createList(userDiaryViewModel.filterData.allTags?.toMutableList()?: mutableListOf())

        binding.tagsBottomSheet.tagsCancel.setOnClickListener {
            filterTagsBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.tagsBottomSheet.tagsSubmit.setOnClickListener {
            isRecreateNotesAdapter = true
            lifecycleScope.launch {
                userDiaryViewModel.updateFilteredNotes()
            }
            filterTagsBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.tagsBottomSheet.tagsClear.setOnClickListener {
            filterTagsAdapter.clearAll()
        }

        binding.tagsBottomSheet.tagsAll.setOnClickListener {
            filterTagsAdapter.selectAll()
        }

        binding.tagsBottomSheet.tagsCancel.setTextColor(
            requireContext().getColor(
                com.aminography.primedatepicker.R.color.lightButtonBarNegativeTextColor
            )
        )

        binding.tagsBottomSheet.tagsSubmit.setTextColor(
            requireContext().getColor(
                com.aminography.primedatepicker.R.color.lightButtonBarPositiveTextColor
            )
        )
    }

    private fun setupCalendarSheet() {
        val themeFactory = object : LightThemeFactory() {

            override val dialogBackgroundColor: Int
                get() =
                    ContextCompat.getColor(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.color.calendarBgColorDark
                        else R.color.calendarBgColorLight
                    )

            override val calendarViewBackgroundColor: Int
                get() =
                    ContextCompat.getColor(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.color.calendarBgColorDark
                        else R.color.calendarBgColorLight
                    )

            override val pickedDayBackgroundShapeType: BackgroundShapeType
                get() = BackgroundShapeType.ROUND_SQUARE

            override val calendarViewPickedDayBackgroundColor: Int
                get() = ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.calendarPickedDayBgDark
                    else R.color.calendarPickedDayBgLight
                )

            override val calendarViewPickedDayInRangeBackgroundColor: Int
                get() = ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.calendarPickedDayInRangeBgDark
                    else R.color.calendarPickedDayInRangeBgLight
                )

            override val calendarViewPickedDayInRangeLabelTextColor: Int
                get() = ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkNotification
                    else R.color.colorLightNotification
                )

            override val calendarViewTodayLabelTextColor: Int
                get() = ContextCompat.getColor(
                requireContext(),
                if (App.preferences.isDarkTheme) R.color.colorDarkNotification
                else R.color.colorLightNotification
            )

            override val calendarViewWeekLabelFormatter: LabelFormatter
                get() = { primeCalendar ->
                    String.format("%s", primeCalendar.weekDayNameShort.replaceFirstChar { it.uppercase() })
                }

            override val calendarViewWeekLabelTextColors: SparseIntArray
                get() = SparseIntArray(7).apply {
                    val color = ContextCompat.getColor(
                        requireContext(),
                        if (App.preferences.isDarkTheme) R.color.colorDarkNotification
                        else R.color.colorLightNotification
                    )

                    put(Calendar.SATURDAY, color)
                    put(Calendar.SUNDAY, color)
                    put(Calendar.MONDAY, color)
                    put(Calendar.TUESDAY, color)
                    put(Calendar.WEDNESDAY, color)
                    put(Calendar.THURSDAY, color)
                    put(Calendar.FRIDAY, color)
                }

            override val calendarViewShowAdjacentMonthDays: Boolean
                get() = true

            override val selectionBarBackgroundColor: Int
                get() = ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.calendarSelectionBarBgDark
                    else R.color.calendarSelectionBarBgLight
                )

            override val selectionBarRangeDaysItemBackgroundColor: Int
                get() = ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkNavViewContainerBackgroundTint
                    else R.color.colorLightNavViewContainerBackgroundTint
                )

            override val selectionBarRangeDaysItemBottomLabelTextColor: Int
                get() =
                    if (!App.preferences.isDarkTheme)
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorLightNotification
                        )
                    else super.selectionBarRangeDaysItemBottomLabelTextColor

            override val selectionBarRangeDaysItemTopLabelTextColor: Int
                get() =
                    if (!App.preferences.isDarkTheme)
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorLightNotification
                        )
                    else super.selectionBarRangeDaysItemBottomLabelTextColor

            override val calendarViewDayLabelTextColor: Int
                get() = ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkBottomNavSelectorUnchecked
                    else R.color.colorLightBottomNavSelectorUnchecked
                )

            override val actionBarTodayTextColor: Int
                get() = ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkBottomNavSelectorUnchecked
                    else R.color.colorLightBottomNavSelectorUnchecked
                )

            override val calendarViewAdjacentMonthDayLabelTextColor: Int
                get() = ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkCustomWheelHighlightBgSolid
                    else R.color.colorLightCustomWheelHighlightBgSolid
                )

            override val actionBarNegativeTextColor: Int
                get() = super.actionBarNegativeTextColor
        }

        val today = CivilCalendar()
        PrimeCalendarView
        val datePicker = PrimeDatePicker
            .dialogWith(today)
            .pickRangeDays { startDay, endDay ->
                userDiaryViewModel.filterData.startDay = (startDay.timeInMillis / 86400000).toInt().toLong() * 86400000L
                userDiaryViewModel.filterData.endDay = ((endDay.timeInMillis / 86400000) + 1).toInt().toLong() * 86400000L

                lifecycleScope.launch { userDiaryViewModel.updateFilteredNotes() }
            }
            .autoSelectPickEndDay(true)

        val startDay = CivilCalendar()
            startDay.timeInMillis =
            if (userDiaryViewModel.filterData.startDay == null)
                System.currentTimeMillis()
            else userDiaryViewModel.filterData.startDay!!

        val endDay = CivilCalendar()
            endDay.timeInMillis =
            if (userDiaryViewModel.filterData.endDay == null)
                System.currentTimeMillis()
            else userDiaryViewModel.filterData.endDay!! - 86400000L

        if (
            userDiaryViewModel.filterData.endDay != null
            && userDiaryViewModel.filterData.startDay != null
        ) datePicker.initiallyPickedRangeDays(startDay, endDay)

        datePicker
            .applyTheme(themeFactory)
            .firstDayOfWeek(Calendar.MONDAY)

        val dialog = datePicker.build()
        dialog.show(childFragmentManager, "calendar")
    }

    private fun setupNoteTypesBar() {
        binding.noteTypesBar.setBackgroundColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.colorDarkNavViewContainerBackgroundTint
            else R.color.colorLightNavViewContainerBackgroundTint
        ))

        binding.noteTypesBar.menu.add(
            MenuItemDescriptor.Builder(
                requireContext(),
                R.id.filter_all_notes,
                ru.get.better.R.drawable.ic_all_filter,
                R.string.filter_all,
                ContextCompat.getColor(
                    requireContext(),
                    if (!App.preferences.isDarkTheme) R.color.colorDarkNavigationBar
                    else R.color.colorLightNavigationBar
                )
            ).build()
        )

        binding.noteTypesBar.menu.add(
            MenuItemDescriptor.Builder(
                requireContext(),
                R.id.filter_notes,
                ru.get.better.R.drawable.diary,
                R.string.filter_notes,
                ContextCompat.getColor(
                    requireContext(),
                    if (!App.preferences.isDarkTheme) R.color.colorDarkNavigationBar
                    else R.color.colorLightNavigationBar
                )
            ).build()
        )

        binding.noteTypesBar.menu.add(
            MenuItemDescriptor.Builder(
                requireContext(),
                R.id.filter_goals,
                ru.get.better.R.drawable.ic_goal,
                R.string.filter_goals,
                ContextCompat.getColor(
                    requireContext(),
                    if (!App.preferences.isDarkTheme) R.color.colorDarkNavigationBar
                    else R.color.colorLightNavigationBar
                )
            ).build()
        )

        binding.noteTypesBar.menu.add(
            MenuItemDescriptor.Builder(
                requireContext(),
                R.id.filter_habits,
                ru.get.better.R.drawable.ic_habit,
                R.string.filter_habits,
                ContextCompat.getColor(
                    requireContext(),
                    if (!App.preferences.isDarkTheme) R.color.colorDarkNavigationBar
                    else R.color.colorLightNavigationBar
                )
            ).build()
        )

        binding.noteTypesBar.menu.add(
            MenuItemDescriptor.Builder(
                requireContext(),
                R.id.filter_trackers,
                ru.get.better.R.drawable.ic_tracker_dark,
                R.string.filter_trackers,
                ContextCompat.getColor(
                    requireContext(),
                    if (!App.preferences.isDarkTheme) R.color.colorDarkNavigationBar
                    else R.color.colorLightNavigationBar
                )
            ).build()
        )

        binding.noteTypesBar.onItemSelectedListener = { view: View, menuItem: MenuItem, b: Boolean ->
            when(menuItem.id) {
                R.id.filter_all_notes -> userDiaryViewModel.filterData.noteType = NoteType.All.id
                R.id.filter_notes -> userDiaryViewModel.filterData.noteType = NoteType.Note.id
                R.id.filter_goals -> userDiaryViewModel.filterData.noteType = NoteType.Goal.id
                R.id.filter_habits -> userDiaryViewModel.filterData.noteType = NoteType.Habit.id
                R.id.filter_trackers -> userDiaryViewModel.filterData.noteType = NoteType.Tracker.id
            }

            isRecreateNotesAdapter = true
            lifecycleScope.launch { userDiaryViewModel.updateFilteredNotes() }
        }

    }

    private fun setupLogic() {
        userDiaryViewModel.setupTags {
            setupFilterTags()
        }

        userDiaryViewModel.resetFilter {
            isRecreateNotesAdapter = true
            setupDiary()
        }

        setupNoteTypesBar()

        binding.filterSearch.addTextChangedListener {
            lifecycleScope.launch {
                userDiaryViewModel.filterData.pattern = it.toString()
                userDiaryViewModel.updateFilteredNotes()
            }
        }

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

        filterTagsBehavior.addBottomSheetCallback(object :
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

    private var isRecreateNotesAdapter = false
    private fun observeDiary(notes: List<DiaryNote>) {
        val notesWithoutHabits = notes.filter { it.noteType != NoteType.Habit.id }

        val habits = notes.filter { it.noteType == NoteType.Habit.id }.getHabitsRealization()

        Log.d("keke", "step3")
        val allNotes = notesWithoutHabits.plus(habits).filter {
            when {
                filterTagsAdapter.getSelectedItems().isNullOrEmpty() -> true
                it.tags.isNullOrEmpty() -> false
                else -> it.tags!!.any(filterTagsAdapter.getSelectedItems()::contains)
            }
        }

        lifecycleScope.launch(Dispatchers.IO) {
            when (userDiaryViewModel.filterData.noteType) {
                NoteType.All.id -> {
                    binding.noteTypesBar.menu.findItemById(R.id.filter_all_notes).notification()
                        .show(allNotes.size.toString())

                    binding.noteTypesBar.menu.findItemById(R.id.filter_notes).notification().clear()
                    binding.noteTypesBar.menu.findItemById(R.id.filter_goals).notification().clear()
                    binding.noteTypesBar.menu.findItemById(R.id.filter_habits).notification()
                        .clear()
                    binding.noteTypesBar.menu.findItemById(R.id.filter_trackers).notification()
                        .clear()
                }
                NoteType.Note.id -> {
                    binding.noteTypesBar.menu.findItemById(R.id.filter_notes).notification()
                        .show(allNotes.size.toString())

                    binding.noteTypesBar.menu.findItemById(R.id.filter_all_notes).notification()
                        .clear()
                    binding.noteTypesBar.menu.findItemById(R.id.filter_goals).notification().clear()
                    binding.noteTypesBar.menu.findItemById(R.id.filter_habits).notification()
                        .clear()
                    binding.noteTypesBar.menu.findItemById(R.id.filter_trackers).notification()
                        .clear()
                }
                NoteType.Goal.id -> {
                    binding.noteTypesBar.menu.findItemById(R.id.filter_goals).notification()
                        .show(allNotes.size.toString())

                    binding.noteTypesBar.menu.findItemById(R.id.filter_all_notes).notification()
                        .clear()
                    binding.noteTypesBar.menu.findItemById(R.id.filter_notes).notification().clear()
                    binding.noteTypesBar.menu.findItemById(R.id.filter_habits).notification()
                        .clear()
                    binding.noteTypesBar.menu.findItemById(R.id.filter_trackers).notification()
                        .clear()
                }
                NoteType.Habit.id -> {
                    binding.noteTypesBar.menu.findItemById(R.id.filter_habits).notification()
                        .show(allNotes.size.toString())

                    binding.noteTypesBar.menu.findItemById(R.id.filter_all_notes).notification()
                        .clear()
                    binding.noteTypesBar.menu.findItemById(R.id.filter_notes).notification().clear()
                    binding.noteTypesBar.menu.findItemById(R.id.filter_goals).notification().clear()
                    binding.noteTypesBar.menu.findItemById(R.id.filter_trackers).notification()
                        .clear()
                }
                NoteType.Tracker.id -> {
                    binding.noteTypesBar.menu.findItemById(R.id.filter_trackers).notification()
                        .show(allNotes.size.toString())

                    binding.noteTypesBar.menu.findItemById(R.id.filter_all_notes).notification()
                        .clear()
                    binding.noteTypesBar.menu.findItemById(R.id.filter_notes).notification().clear()
                    binding.noteTypesBar.menu.findItemById(R.id.filter_goals).notification().clear()
                    binding.noteTypesBar.menu.findItemById(R.id.filter_habits).notification()
                        .clear()
                }
            }
        }

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

            if (
                !::adapter.isInitialized ||
                isRecreateNotesAdapter
            ) {
                val items = arrayListOf<Section>()
                val datesSet = linkedSetOf<String>()

                lifecycleScope.launch(Dispatchers.IO) {
                    isRecreateNotesAdapter = false

                    val formatter = SimpleDateFormat("MMMM, yyyy", Locale(App.preferences.locale))

                    allNotes.forEach {
                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = it.date.toLong()
                        datesSet.add(
                            formatter.format(calendar.time)
                        )
                    }

                    Collections.sort(datesSet.toMutableList(), StringDateComparator())
                    Collections.sort(datesSet.toMutableList(), Collections.reverseOrder())

                    var section = -1
                    var sectionName = "--------------"

                    for (i in allNotes.indices) {
                        if (
                            !formatter.format(allNotes[i].date)
                                .contains(sectionName)
                        ) {
                            section++
                            sectionName = datesSet.elementAt(section)
                            items.add(SectionHeader(section, sectionName))
                        }
                        items.add(SectionItem(section, sectionName, allNotes[i]))
                    }
                }.invokeOnCompletion {
                    lifecycleScope.launch(Dispatchers.Main) {
                        adapter = NotesAdapter(
                            context = requireContext(),
                            datesSet = datesSet
                        )
                        binding.recycler.adapter = adapter

                        val decorator = StickyHeaderItemDecorator(adapter)
                        decorator.attachToRecyclerView(binding.recycler)

                        adapter.items = items
                        binding.recycler.setUpRemoveItemTouchHelper(
                            ::onItemInListSwiped
                        )
                    }
                }

            } else adapter.updateNotes(allNotes.toMutableList())

            pagerAdapter = NotesPagerAdapter(requireContext(), allNotes.toMutableList())
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
                requireContext(), R.dimen.diary_viewpager_current_item_horizontal_margin
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

        lifecycleScope.launch(Dispatchers.IO) {

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

            binding.filterCard.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkNavViewContainerBackgroundTint
                    else R.color.colorLightNavViewContainerBackgroundTint
                )
            )

            binding.filterSearch.hint =
                App.resourcesProvider.getStringLocale(R.string.filter_search)
            binding.filterSearch.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewPostAddEditTextText
                    else R.color.colorLightViewPostAddEditTextText
                )
            )

            binding.filterSearch.setHintTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkViewPostAddEditTextHint
                    else R.color.colorLightViewPostAddEditTextHint
                )
            )

            binding.icTags.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkBottomNavSelectorUnchecked
                    else R.color.colorLightBottomNavSelectorUnchecked
                )
            )

            binding.icCalendar.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkBottomNavSelectorUnchecked
                    else R.color.colorLightBottomNavSelectorUnchecked
                )
            )

            binding.tagsBottomSheet.container.background = ContextCompat.getDrawable(
                requireContext(),
                if (App.preferences.isDarkTheme) R.drawable.background_bottom_sheet_dark
                else R.drawable.background_bottom_sheet_light
            )

            binding.tagsBottomSheet.viewHeader.background = ContextCompat.getDrawable(
                requireContext(),
                if (App.preferences.isDarkTheme) R.drawable.snack_neutral_gradient_dark
                else R.drawable.snack_neutral_gradient_light
            )

            binding.tagsBottomSheet.tagsCancel.text = App.resourcesProvider.getStringLocale(R.string.action_cancel)
            binding.tagsBottomSheet.tagsSubmit.text = App.resourcesProvider.getStringLocale(R.string.action_select)
            binding.tagsBottomSheet.tagsClear.text = App.resourcesProvider.getStringLocale(R.string.tags_clear)
            binding.tagsBottomSheet.tagsAll.text = App.resourcesProvider.getStringLocale(R.string.tags_all)


//            binding.tagsBottomSheet.tagsCancel.setTextColor(
//                ContextCompat.getColor(
//                    requireContext(),
//                    if (App.preferences.isDarkTheme) R.color.colorDarkFragmentDiaryTitleText
//                    else R.color.colorLightFragmentDiaryTitleText
//                )
//            )
//
//            binding.tagsBottomSheet.tagsSubmit.setTextColor(
//                ContextCompat.getColor(
//                    requireContext(),
//                    if (App.preferences.isDarkTheme) R.color.colorDarkFragmentDiaryTitleText
//                    else R.color.colorLightFragmentDiaryTitleText
//                )
//            )

            binding.tagsBottomSheet.title.text = App.resourcesProvider.getStringLocale(R.string.tags_title)
            binding.tagsBottomSheet.title.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkFragmentDiaryTitleText
                    else R.color.colorLightFragmentDiaryTitleText
                )
            )

            binding.tagsBottomSheet.tagsClear.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkBottomNavSelectorUnchecked
                    else R.color.colorLightBottomNavSelectorUnchecked
                )
            )

            binding.tagsBottomSheet.tagsAll.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (App.preferences.isDarkTheme) R.color.colorDarkBottomNavSelectorUnchecked
                    else R.color.colorLightBottomNavSelectorUnchecked
                )
            )
        }
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
        else if (filterTagsBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
            filterTagsBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
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
                if (habitsAdapter.itemCount != 0 && !App.preferences.isDiaryHabitsSpotlightShown)
                    showHabitsSpotlight()
            }
        }
    }

    private fun setupDiary() {
        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
            userDiaryViewModel.filteredNotesLiveData.observe(this@DiaryFragment) { notes ->
                setupHabitsRealization()
                observeDiary(notes?: emptyList())
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
                getViewPagerPositionByNoteId(e.noteId),
                false
            )
        }

        EventBus.getDefault().post(ChangeNavViewVisibilityEvent(false))
    }

    override fun onStop() {

        super.onStop()

    }

    private fun getViewPagerPositionByNoteId(noteId: String): Int =
        pagerAdapter.notes.indexOf(
            pagerAdapter.notes.first { it.diaryNoteId == noteId }
        )

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

        EventBus.getDefault()
            .post(SecondaryViewUpdateStateEvent(newState = SecondaryViews.DiarySpotlight))
        EventBus.getDefault().post(ChangeIsAnySpotlightActiveNowEvent(true))


        habitsTargetLayout.findViewById<ConstraintLayout>(R.id.container).setOnClickListener {
            spotlight.finish()
            EventBus.getDefault()
                .post(SecondaryViewUpdateStateEvent(newState = SecondaryViews.Empty))

            App.preferences.isDiaryHabitsSpotlightShown = true
            EventBus.getDefault().post(ChangeIsAnySpotlightActiveNowEvent(false))

            App.preferences.isDiaryHabitsSpotlightShown = true
        }
    }

    inner class Handler {
        fun onInfoClicked(v: View) {
            getBalloon(getString(R.string.diary_info)).showAlignBottom(binding.info)
        }

        fun onBlurClicked(v: View) {
            if (viewPagerBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                viewPagerBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            else if (filterTagsBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                filterTagsBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        fun onCalendarClicked(v: View) {
            setupCalendarSheet()
        }

        fun onTagsClicked(v: View) {
            filterTagsBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }
}