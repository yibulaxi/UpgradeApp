package com.velkonost.upgrade.ui.diary.adapter.viewpager

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.velkonost.upgrade.model.DiaryNote

class NotesPagerAdapter(
    private val context: Context,
    private val notes: MutableList<DiaryNote>
) : RecyclerView.Adapter<NotesPagerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        NotesPagerViewHolder.newInstance(parent, context)


    override fun onBindViewHolder(holder: NotesPagerViewHolder, position: Int) {
        holder.bind(notes[position])
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    fun removeNote(deleteNote: DiaryNote) {
        for (note in notes) {
            if (note.diaryNoteId == deleteNote.diaryNoteId) notes.remove(note)
        }

        notifyDataSetChanged()
    }

    fun removeNoteById(noteId: String) {
        for (i in 0 until notes.size) {
            if (notes[i].diaryNoteId == noteId) {
                notes.removeAt(i)
                notifyItemRemoved(i)
                break
            }
        }
    }

    fun updateNotes(newNotes: MutableList<DiaryNote>) {
        val notesToDeleteIndexes = arrayListOf<Int>()
        val notesToAddIndexes = arrayListOf<Int>()
        val notesToUpdateIndexes = arrayListOf<Int>()

        for (i in 0 until notes.size) {
            val foundNote = newNotes.findLast { it.diaryNoteId == notes[i].diaryNoteId}

            if (foundNote == null) {
                notesToDeleteIndexes.add(i)
            } else if (
                notes[i].text != foundNote.text
                || notes[i].media != foundNote.media
                || notes[i].interest?.interestIcon != foundNote.interest?.interestIcon
                || notes[i].interest?.interestId != foundNote.interest?.interestId
                || notes[i].interest?.interestName != foundNote.interest?.interestName
                || notes[i].changeOfPoints != foundNote.changeOfPoints
            ) {
                notesToUpdateIndexes.add(i)
            }
        }

        for (i in 0 until newNotes.size) {
            val foundNote = notes.findLast { it.diaryNoteId == newNotes[i].diaryNoteId}

            if (foundNote == null) {
                notesToAddIndexes.add(i)
            }
        }

        notesToDeleteIndexes.forEach {
            notes.removeAt(it)
            notifyItemRemoved(it)
        }

        notesToAddIndexes.forEach {
            notes.add(newNotes[it])
            notifyItemInserted(it)
        }

        notesToUpdateIndexes.forEach {
            newNotes.filter { diaryNote ->
                diaryNote.diaryNoteId == notes[it].diaryNoteId
            }.forEach { foundNote ->
                notes[it]
                notes[it].text = foundNote.text
                notes[it].media = foundNote.media
                notes[it].interest?.interestId = foundNote.interest?.interestId!!
                notes[it].interest?.interestName = foundNote.interest?.interestName!!
                notes[it].interest?.interestIcon = foundNote.interest?.interestIcon!!
                notes[it].changeOfPoints = foundNote.changeOfPoints

                notifyItemChanged(it)
            }
        }
    }

}