package ru.get.better.ui.diary.adapter.viewpager

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import org.greenrobot.eventbus.EventBus
import ru.get.better.R
import ru.get.better.databinding.ItemAdapterPagerNotesBinding
import ru.get.better.event.EditDiaryNoteEvent
import ru.get.better.model.DefaultInterest
import ru.get.better.model.DiaryNote
import ru.get.better.model.Media
import ru.get.better.model.NoteType
import ru.get.better.ui.diary.adapter.NotesMediaAdapter
import ru.get.better.util.formatMillsToFullDateTime

class NotesPagerViewHolder(
    val binding: ItemAdapterPagerNotesBinding,
    val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    private var MAX_ELEVATION_FACTOR = 8

    init {
        binding.handler = Handler()
    }

    fun bind(note: DiaryNote) {
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
                DefaultInterest.getInterestById(note.interest.interestId.toInt()).getLogo()
            )
        )

        binding.amount.text = note.changeOfPoints.toString().replace(".", ",")

        when {
            note.changeOfPoints.toFloat() > 0 -> binding.amount.background =
                AppCompatResources.getDrawable(context, R.drawable.snack_success_gradient)
            note.changeOfPoints.toFloat() < 0 -> binding.amount.background =
                AppCompatResources.getDrawable(context, R.drawable.snack_warning_gradient)
            else -> binding.amount.background =
                AppCompatResources.getDrawable(context, R.drawable.bg_list_metric_value)
        }

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

        binding.cardView.maxCardElevation = (binding.cardView.cardElevation * MAX_ELEVATION_FACTOR)

        binding.edit.isVisible = note.noteType != NoteType.HabitRealization.id
        binding.edit.setOnClickListener { EventBus.getDefault().post(EditDiaryNoteEvent(note)) }

        binding.recycler.isVisible = !note.media.isNullOrEmpty()
        (binding.recycler.layoutManager as LinearLayoutManager).orientation =
            LinearLayoutManager.HORIZONTAL
        val media = arrayListOf<Media>()
        note.media?.map { media.add(Media(url = it)) }

        binding.recycler.adapter = NotesMediaAdapter(context, media)

        if (note.noteType == NoteType.HabitRealization.id) {
            binding.habitsRealizationBlock.isVisible = true
            binding.habitsRealizationRecycler.isVisible = true

            binding.habitsRealizationValue.text =
                note.datesCompletion!!.filter { it.datesCompletionIsCompleted == true }.size.toString() +
                        " / " +
                        note.datesCompletion.size.toString()

            binding.habitsRealizationRecycler.adapter =
                HabitsRealizationAdapter(context, note.datesCompletion.toMutableList())
        } else {
            binding.habitsRealizationBlock.isVisible = false
            binding.habitsRealizationRecycler.isVisible = false
        }

//        if (note.noteType == NoteType.Tracker.id && note.isActiveNow!!) {
//            val firstColor = context.resources.getColor(R.color.colorTgWhite)
//            val secondColor = context.resources.getColor(R.color.colorTgPrimary)
//
//            val colorAnimationFromFirstToSecond = ValueAnimator.ofObject(ArgbEvaluator(), firstColor, secondColor)
//            val colorAnimationFromSecondToFirst = ValueAnimator.ofObject(ArgbEvaluator(), secondColor, firstColor)
//
//            colorAnimationFromFirstToSecond.duration = 500
//            colorAnimationFromSecondToFirst.duration = 500
//
//            colorAnimationFromSecondToFirst.startDelay = 100
//            colorAnimationFromFirstToSecond.startDelay = 100
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
//        }

        if (note.noteType == NoteType.Tracker.id && !note.isActiveNow!!) {
            binding.wasteTime.isVisible = true

            val trackerTime = note.datetimeEnd!!.toLong() - note.datetimeStart!!.toLong()

            val hours = (trackerTime / (1000 * 60 * 60)).toInt()
            val minutes = ((trackerTime / (1000 * 60)) % 60).toInt()
            val seconds = (trackerTime / 1000) % 60

            binding.wasteTime.text = String.format(
                "Продолжительность: %02d:%02d:%02d",
                hours, minutes, seconds
            )
        } else binding.wasteTime.isVisible = false

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
    }

    inner class Handler

    companion object {
        fun newInstance(
            parent: ViewGroup,
            context: Context,
        ) =
            NotesPagerViewHolder(
                ItemAdapterPagerNotesBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ), context = context
            )
    }
}