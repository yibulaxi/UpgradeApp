package com.velkonost.upgrade.ui.diary.adapter

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.animation.doOnEnd
import androidx.core.view.ViewCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.common.math.Quantiles.scale
import com.velkonost.upgrade.R
import com.velkonost.upgrade.databinding.ItemNoteBinding
import com.velkonost.upgrade.event.ShowNoteDetailEvent
import com.velkonost.upgrade.model.AllLogo
import com.velkonost.upgrade.model.DiaryNote
import com.velkonost.upgrade.model.NoteType
import org.greenrobot.eventbus.EventBus
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
        binding.description.text = SimpleDateFormat("dd MMM, HH:mm", Locale("ru")).format(note.date.toLong())


        binding.value.setImageDrawable(
            AppCompatResources.getDrawable(
                context,
                AllLogo().getLogoById(note.interest!!.interestIcon)
            )
        )

        binding.noteType.setImageDrawable(
            when(note.noteType) {
                NoteType.Note.id -> AppCompatResources.getDrawable(context, R.drawable.diary)
                NoteType.Goal.id -> AppCompatResources.getDrawable(context, R.drawable.ic_goal)
                NoteType.Habit.id -> AppCompatResources.getDrawable(context, R.drawable.ic_habit)
                NoteType.HabitRealization.id -> AppCompatResources.getDrawable(context, R.drawable.ic_habit)
                NoteType.Tracker.id -> AppCompatResources.getDrawable(context, R.drawable.ic_tracker)
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
            Log.d("keke_pos", position.toString())
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