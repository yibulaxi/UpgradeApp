package ru.get.better.ui.diary.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import org.greenrobot.eventbus.EventBus
import ru.get.better.R
import ru.get.better.databinding.ItemNoteBinding
import ru.get.better.event.ShowNoteDetailEvent
import ru.get.better.model.AllLogo
import ru.get.better.model.DiaryNote
import ru.get.better.model.NoteType
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

//        when (note.noteType) {
//            NoteType.Note.id -> {
//
//            }
//        }
        binding.cardView.animation =
            AnimationUtils.loadAnimation(context, R.anim.scale)

        binding.title.text = note.text
        binding.description.text =
            SimpleDateFormat("dd MMM, HH:mm", Locale("ru")).format(note.date.toLong())


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
                    R.drawable.ic_tracker
                )
                else -> AppCompatResources.getDrawable(context, R.drawable.diary)
            }
        )
//
//        val firstColor = context.resources.getColor(R.color.colorTgWhite)
//        val secondColor = context.resources.getColor(R.color.colorTgPrimary)
//
//        val colorAnimationFromFirstToSecond = ValueAnimator.ofObject(ArgbEvaluator(), firstColor, secondColor)
//        val colorAnimationFromSecondToFirst = ValueAnimator.ofObject(ArgbEvaluator(), secondColor, firstColor)
//
//        colorAnimationFromFirstToSecond.duration = 500
//        colorAnimationFromSecondToFirst.duration = 500
//
//        colorAnimationFromSecondToFirst.startDelay = 100
//        colorAnimationFromFirstToSecond.startDelay = 100
//
//        if (note.noteType == NoteType.Tracker.id && note.isActiveNow!!) {
//
//
//            colorAnimationFromFirstToSecond.addUpdateListener { animator ->
//                ImageViewCompat.setImageTintList(binding.noteType, ColorStateList.valueOf(animator.animatedValue as Int))
//            }
//
//            colorAnimationFromSecondToFirst.addUpdateListener { animator ->
//                ImageViewCompat.setImageTintList(binding.noteType, ColorStateList.valueOf(animator.animatedValue as Int))
//            }
//
//            colorAnimationFromFirstToSecond.doOnEnd {
//                colorAnimationFromSecondToFirst.start()
//            }
//
//            colorAnimationFromSecondToFirst.doOnEnd {
//                colorAnimationFromFirstToSecond.start()
//            }
//
//            colorAnimationFromSecondToFirst.start()
//
//        } else {
//            colorAnimationFromFirstToSecond.end()
//            colorAnimationFromSecondToFirst.end()
//
//            colorAnimationFromFirstToSecond.cancel()
//            colorAnimationFromSecondToFirst.cancel()
//        }

        binding.container.setOnClickListener {
            EventBus.getDefault().post(ShowNoteDetailEvent(position))
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