package ru.get.better.ui.diary.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.shuhart.stickyheader.StickyAdapter
import org.greenrobot.eventbus.EventBus
import ru.get.better.App
import ru.get.better.R
import ru.get.better.databinding.ItemNoteBinding
import ru.get.better.event.ChangeProgressStateEvent
import ru.get.better.model.DiaryNote
import ru.get.better.util.stickyheader.Section
import ru.get.better.util.stickyheader.SectionHeader
import ru.get.better.util.stickyheader.SectionItem
import java.text.SimpleDateFormat
import java.util.*


class NotesAdapter(
    private val context: Context? = null,
    var items: ArrayList<Section>? = null,
    var datesSet: LinkedHashSet<String>
) : StickyAdapter<RecyclerView.ViewHolder, RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == Section.HEADER || viewType == Section.CUSTOM_HEADER) {
            HeaderViewHolder(inflater.inflate(R.layout.notes_list_header_item, parent, false))
        } else
            NotesViewHolder(ItemNoteBinding.inflate(inflater, parent, false), context!!)
    }

    override fun getItemViewType(position: Int): Int {
        return items!![position].type()
    }


    override fun getItemCount(): Int {
        return items!!.size
    }

    fun getNoteAt(position: Int): DiaryNote {
        return items!![position].diaryNote!!
    }

    fun updateNotes(newNotes: MutableList<DiaryNote>) {
        val notesToDeleteIndexes = arrayListOf<Int>()
        val notesToAddIndexes = arrayListOf<Int>()
        val notesToUpdateIndexes = arrayListOf<Int>()

        for (i in 0 until items!!.size) {
            if (items!![i] is SectionHeader) continue

            val foundNote =
                newNotes.findLast { it.diaryNoteId == items!![i].diaryNote!!.diaryNoteId }

            if (foundNote == null) {
                notesToDeleteIndexes.add(i)
            } else if (
                items!![i].diaryNote!!.text != foundNote.text
                || items!![i].diaryNote!!.media != foundNote.media
                || items!![i].diaryNote!!.interest?.interestIcon != foundNote.interest?.interestIcon
                || items!![i].diaryNote!!.interest?.interestId != foundNote.interest?.interestId
                || items!![i].diaryNote!!.interest?.interestName != foundNote.interest?.interestName
                || items!![i].diaryNote!!.changeOfPoints != foundNote.changeOfPoints
            ) {
                notesToUpdateIndexes.add(i)
            }
        }

        for (i in 0 until newNotes.size) {
            val foundNote = items!!.findLast {
                it is SectionItem
                        && it.diaryNote!!.diaryNoteId == newNotes[i].diaryNoteId
                        && it.diaryNote!!.date == newNotes[i].date
            }

            if (foundNote == null) {
                notesToAddIndexes.add(i)
            }
        }

        notesToDeleteIndexes.forEach {
            val section = items!![it].sectionPosition()

            items!!.removeAt(it)
            notifyItemRemoved(it)

            checkIsSectionEmpty(section)
        }

        notesToAddIndexes.forEach {
            items!!.add(
                getSectionItem(
                    newNotes[it]
                )
            )

            notifyItemInserted(items!!.size - 1)
        }

        notesToUpdateIndexes.forEach {
            newNotes.filter { diaryNote ->
                diaryNote.diaryNoteId == items!![it].diaryNote!!.diaryNoteId
            }.forEach { foundNote ->
                items!![it].diaryNote!!.text = foundNote.text
                items!![it].diaryNote!!.media = foundNote.media
                items!![it].diaryNote!!.interest?.interestId = foundNote.interest?.interestId!!
                items!![it].diaryNote!!.interest?.interestName = foundNote.interest.interestName
                items!![it].diaryNote!!.interest?.interestIcon = foundNote.interest.interestIcon
                items!![it].diaryNote!!.changeOfPoints = foundNote.changeOfPoints

                notifyItemChanged(it)
            }
        }

        EventBus.getDefault().post(ChangeProgressStateEvent(false))
    }

    private fun checkIsSectionEmpty(section: Int) {
        var isSectionEmpty = true
        items!!.forEach {
            if (it !is SectionHeader && it.sectionPosition() == section) {
                isSectionEmpty = false
                return@forEach
            }
        }

        if (isSectionEmpty) {
            for (i in items!!.indices) {
                if (items!![i] is SectionHeader && items!![i].sectionPosition() == section) {
                    datesSet.removeAll { it == items!![i].sectionName() }
                    items!!.removeAt(i)
                    notifyItemRemoved(i)
                    return
                }
            }
        }
    }

    private fun getSectionItem(diaryNote: DiaryNote): SectionItem {
        val formatter = SimpleDateFormat("MMMM, yyyy")

        val section: Int
        val sectionName: String

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = diaryNote.date.toLong()

        if (datesSet.contains(formatter.format(calendar.time))) {
            section = datesSet.size - 1
            sectionName = datesSet.elementAt(section)
        } else {
            section = datesSet.size
            sectionName = formatter.format(calendar.time)

            items!!.add(
                SectionHeader(
                    section = section,
                    sectionName = sectionName
                )
            )

            notifyItemInserted(items!!.size - 1)

            datesSet.add(formatter.format(calendar.time))
        }

        return SectionItem(
            section = section,
            sectionName = sectionName,
            diaryNote = diaryNote
        )
    }

    override fun getHeaderPositionForItem(itemPosition: Int): Int =
        items!![itemPosition].sectionPosition()

    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder?, headerPosition: Int) {

        (holder as HeaderViewHolder).textView.setTextColor(
            ContextCompat.getColor(
                context!!,
                if (App.preferences.isDarkTheme) R.color.colorDarkNotesListHeaderItemText
                else R.color.colorLightNotesListHeaderItemText
            )
        )
        (holder as HeaderViewHolder).textView.text = datesSet.elementAt(headerPosition)
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
        createViewHolder(parent, Section.HEADER)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (items!![position].type()) {
            Section.HEADER -> {
                (holder as HeaderViewHolder).textView.background = ContextCompat.getDrawable(
                    context!!,
                    if (App.preferences.isDarkTheme) R.drawable.bg_sticky_header_dark
                    else R.drawable.bg_sticky_header_light
                )

                (holder as HeaderViewHolder).textView.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        if (App.preferences.isDarkTheme) R.color.colorDarkNotesListHeaderItemText
                        else R.color.colorLightNotesListHeaderItemText
                    )
                )
                (holder as HeaderViewHolder).textView.text = items!![position].sectionName()
            }
            Section.ITEM -> {
                (holder as NotesViewHolder).bind(
                    note = items!![position].diaryNote!!,
                    position =
                    getNotePosition(
                        diaryNoteId = items!![position].diaryNote!!.diaryNoteId,
                        date = items!![position].diaryNote!!.date
                    )
                )
            }
            else -> {
                (holder as HeaderViewHolder).textView.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        if (App.preferences.isDarkTheme) R.color.colorDarkNotesListHeaderItemText
                        else R.color.colorLightNotesListHeaderItemText
                    )
                )
                (holder as HeaderViewHolder).textView.text = "Custom header"
            }
        }
    }

    private fun getNotePosition(
        diaryNoteId: String,
        date: String
    ): Int =
        items!!.filter { it !is SectionHeader }
            .indexOfFirst {
                it.diaryNote!!.diaryNoteId == diaryNoteId
                        && it.diaryNote!!.date == date
            }
}

class HeaderViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var textView: TextView = itemView.findViewById(ru.get.better.R.id.textView)
}