package ru.get.better.ui.diary.adapter.viewpager

import android.content.Context
import android.content.res.ColorStateList
import android.text.Html
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import org.greenrobot.eventbus.EventBus
import ru.get.better.App
import ru.get.better.R
import ru.get.better.databinding.ItemAdapterPagerNotesNewBinding
import ru.get.better.event.EditDiaryNoteEvent
import ru.get.better.model.AllLogo
import ru.get.better.model.DiaryNote
import ru.get.better.model.Media
import ru.get.better.model.NoteType
import ru.get.better.ui.activity.main.ext.adapter.TagsAdapter
import ru.get.better.ui.diary.adapter.NotesMediaAdapter
import ru.get.better.util.formatMillsToFullDateTime

class NotesPagerViewHolder(
    val binding: ItemAdapterPagerNotesNewBinding,
    val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    private var MAX_ELEVATION_FACTOR = 8

    init {
        binding.handler = Handler()
    }

    fun bind(note: DiaryNote) {
        binding.habitsRealizationTitle.text =
            App.resourcesProvider.getStringLocale(R.string.completed)

        binding.textCard.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkItemAdapterPagerNotesBackgroundTint
                else R.color.colorLightItemAdapterPagerNotesBackgroundTint
            )
        )

        binding.dateCard.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkItemAdapterPagerNotesBackgroundTint
                else R.color.colorLightItemAdapterPagerNotesBackgroundTint
            )
        )

        binding.habitsCountCard.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkItemAdapterPagerNotesBackgroundTint
                else R.color.colorLightItemAdapterPagerNotesBackgroundTint
            )
        )

        binding.habitsRealizationsCard.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkItemAdapterPagerNotesBackgroundTint
                else R.color.colorLightItemAdapterPagerNotesBackgroundTint
            )
        )

        binding.typeCard.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkItemAdapterPagerNotesBackgroundTint
                else R.color.colorLightItemAdapterPagerNotesBackgroundTint
            )
        )

        binding.interestCard.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkItemAdapterPagerNotesBackgroundTint
                else R.color.colorLightItemAdapterPagerNotesBackgroundTint
            )
        )

        binding.wasteTimeCard.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkItemAdapterPagerNotesBackgroundTint
                else R.color.colorLightItemAdapterPagerNotesBackgroundTint
            )
        )

        binding.imagesCard.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkItemAdapterPagerNotesBackgroundTint
                else R.color.colorLightItemAdapterPagerNotesBackgroundTint
            )
        )

        binding.habitsRealizationValue.background = ContextCompat.getDrawable(
            context,
            if (App.preferences.isDarkTheme) R.drawable.bg_habits_realization_value_dark
            else R.drawable.bg_habits_realization_value_light
        )

        binding.noteType.imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkItemAdapterPagerNotesNoteTypeTint
                else R.color.colorLightItemAdapterPagerNotesNoteTypeTint
            )
        )

        binding.interestName.setTextColor(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkItemAdapterPagerNotesInterestNameText
                else R.color.colorLightItemAdapterPagerNotesInterestNameText
            )
        )

        binding.text.setTextColor(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkItemAdapterPagerNotesTextText
                else R.color.colorLightItemAdapterPagerNotesTextText
            )
        )

        binding.wasteTime.setTextColor(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkItemAdapterPagerNotesWasteTimeText
                else R.color.colorLightItemAdapterPagerNotesWasteTimeText
            )
        )

        binding.habitsRealizationTitle.setTextColor(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkItemAdapterPagerNotesHabitsRealizationTitleText
                else R.color.colorLightItemAdapterPagerNotesHabitsRealizationTitleText
            )
        )

        binding.habitsRealizationValue.setTextColor(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkItemAdapterPagerNotesHabitsRealizationValueText
                else R.color.colorLightItemAdapterPagerNotesHabitsRealizationValueText
            )
        )

        binding.date.setTextColor(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkItemAdapterPagerNotesDateText
                else R.color.colorLightItemAdapterPagerNotesDateText
            )
        )

        binding.edit.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkItemAdapterPagerNotesEditBackgroundTint
                else R.color.colorLightItemAdapterPagerNotesEditBackgroundTint
            )
        )

        binding.icEdit.imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkItemAdapterPagerNotesEditTint
                else R.color.colorLightItemAdapterPagerNotesEditTint
            )
        )

        binding.text.text = Html.fromHtml(note.text)

        if (note.noteType == NoteType.HabitRealization.id) {
            binding.date.text = context.formatMillsToFullDateTime(note.datetimeStart!!.toLong())
        } else {
            binding.date.text = context.formatMillsToFullDateTime(note.date.toLong())
        }

        binding.interestName.text = note.interest!!.interestName

        binding.icon.setImageDrawable(
            AppCompatResources.getDrawable(
                context,
                AllLogo().getLogoById(note.interest!!.interestIcon)
            )
        )

        binding.noteType.setImageDrawable(
            when (note.noteType) {
                NoteType.Note.id -> AppCompatResources.getDrawable(context, R.drawable.diary)
                NoteType.Goal.id -> AppCompatResources.getDrawable(context, R.drawable.ic_goal)
                NoteType.Habit.id -> AppCompatResources.getDrawable(context, R.drawable.ic_habit)
                NoteType.HabitRealization.id -> AppCompatResources.getDrawable(
                    context,
                    R.drawable.ic_habit
                )
                NoteType.Tracker.id -> AppCompatResources.getDrawable(
                    context,
                    R.drawable.ic_tracker_light
                )
                else -> AppCompatResources.getDrawable(context, R.drawable.diary)
            }
        )

//        binding.cardView.maxCardElevation = (binding.cardView.cardElevation * MAX_ELEVATION_FACTOR)

        binding.edit.isVisible = note.noteType != NoteType.HabitRealization.id
        binding.edit.setOnClickListener { EventBus.getDefault().post(EditDiaryNoteEvent(note)) }

        binding.recycler.isVisible = !note.media.isNullOrEmpty()
        (binding.recycler.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.HORIZONTAL
        val media = arrayListOf<Media>()
        note.media?.map { media.add(Media(url = it)) }

        binding.recycler.adapter = NotesMediaAdapter(context, media)

        binding.imagesCard.isVisible = !media.isNullOrEmpty()

        if (note.noteType == NoteType.HabitRealization.id) {
            binding.habitsCountCard.isVisible = true
            binding.habitsRealizationsCard.isVisible = true

            binding.habitsRealizationValue.text =
                note.datesCompletion!!.filter { it.datesCompletionIsCompleted == true }.size.toString() +
                        " / " +
                        note.datesCompletion!!.size.toString()

            binding.habitsRealizationRecycler.adapter =
                HabitsRealizationAdapter(context, note.datesCompletion!!.toMutableList())
        } else {
            binding.habitsCountCard.isVisible = false
            binding.habitsRealizationsCard.isVisible = false
        }

        if (note.noteType == NoteType.Tracker.id && !note.isActiveNow!!) {
            binding.wasteTimeCard.isVisible = true

            val trackerTime = note.datetimeEnd!!.toLong() - note.datetimeStart!!.toLong()

            val hours = (trackerTime / (1000 * 60 * 60)).toInt()
            val minutes = ((trackerTime / (1000 * 60)) % 60).toInt()
            val seconds = (trackerTime / 1000) % 60

            binding.wasteTime.text = String.format(
                App.resourcesProvider.getStringLocale(R.string.waste_time, App.preferences.locale),
                hours, minutes, seconds
            )
        } else {
            binding.wasteTimeCard.isVisible = false
        }

        binding.recycler.addOnItemTouchListener(object : OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                when (e.action) {
                    MotionEvent.ACTION_MOVE -> rv.parent.requestDisallowInterceptTouchEvent(true)
                }
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        })

        binding.habitsRealizationRecycler.addOnItemTouchListener(object : OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                when (e.action) {
                    MotionEvent.ACTION_MOVE -> rv.parent.requestDisallowInterceptTouchEvent(true)
                }
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        })

        val lm = FlexboxLayoutManager(context)
        lm.flexDirection = FlexDirection.ROW
        lm.justifyContent = JustifyContent.FLEX_START

        binding.tagsRecycler.layoutManager = lm

        val tagsAdapter = TagsAdapter()
        binding.tagsRecycler.adapter = tagsAdapter

        if (!note.tags.isNullOrEmpty()) {
            tagsAdapter.createList(note.tags!!.toMutableList(), isAddEmptyTag = false)
        } else {
            binding.tagsRecycler.isVisible = false
        }
    }

    inner class Handler

    companion object {
        fun newInstance(
            parent: ViewGroup,
            context: Context,
        ) =
            NotesPagerViewHolder(
                ItemAdapterPagerNotesNewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ), context = context
            )
    }
}