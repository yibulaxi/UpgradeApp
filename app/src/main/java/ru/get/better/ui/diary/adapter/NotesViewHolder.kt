package ru.get.better.ui.diary.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.text.Html
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import org.greenrobot.eventbus.EventBus
import ru.get.better.App
import ru.get.better.R
import ru.get.better.databinding.ItemNoteBinding
import ru.get.better.event.ShowNoteDetailEvent
import ru.get.better.model.AllLogo
import ru.get.better.model.DiaryNote
import ru.get.better.model.NoteType
import ru.get.better.ui.activity.main.ext.adapter.TagsAdapter
import java.text.SimpleDateFormat
import java.util.*

class NotesViewHolder(
    val binding: ItemNoteBinding,
    val context: Context
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.handler = Handler()
    }

    fun bind(note: DiaryNote, position: Int) {

        binding.cardView.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkItemNoteBackgroundTint
                else R.color.colorLightItemNoteBackgroundTint
            )
        )

        binding.title.setTextColor(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkItemNoteTitleText
                else R.color.colorLightItemNoteTitleText
            )
        )

        binding.description.setTextColor(
            ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.colorDarkItemNoteDescriptionText
                else R.color.colorLightItemNoteDescriptionText
            )
        )

        binding.noteType.imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                context,
                when(note.changeOfPoints) {
                    -1 -> com.aminography.primedatepicker.R.color.lightButtonBarNegativeTextColor
                    1 -> com.aminography.primedatepicker.R.color.lightButtonBarPositiveTextColor
                    else -> {
                        if (App.preferences.isDarkTheme) R.color.colorDarkItemNoteNoteTypeTint
                        else R.color.colorLightItemNoteNoteTypeTint
                    }
                }
            )
        )

        binding.cardView.animation =
            AnimationUtils.loadAnimation(context, R.anim.scale)

        binding.title.text = Html.fromHtml(note.text)
        binding.description.text =
            SimpleDateFormat(
                "dd MMM, HH:mm",
                Locale(App.preferences.locale)
            ).format(note.date)


        binding.value.setImageDrawable(
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

        binding.tagsRecycler.setOnClickListener {
            EventBus.getDefault().post(
                ShowNoteDetailEvent(position, note.diaryNoteId)
            )
        }

        binding.container.setOnClickListener {
            EventBus.getDefault().post(
                ShowNoteDetailEvent(position, note.diaryNoteId)
            )
        }

    }

    inner class Handler

    companion object {
        fun newInstance(
            parent: ViewGroup,
            context: Context,
        ) =
            NotesViewHolder(
                ItemNoteBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ), context = context
            )
    }
}