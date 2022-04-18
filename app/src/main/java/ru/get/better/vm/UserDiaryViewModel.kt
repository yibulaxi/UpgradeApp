package ru.get.better.vm

import android.os.Handler
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import ru.get.better.App
import ru.get.better.event.ChangeProgressStateEvent
import ru.get.better.event.DeleteDiaryNoteEvent
import ru.get.better.event.UpdateUserInterestEvent
import ru.get.better.model.DiaryNote
import ru.get.better.model.NoteType
import ru.get.better.model.getDifficultyValue
import ru.get.better.util.SingleLiveEvent
import ru.get.better.util.ext.mutableLiveDataOf
import java.text.SimpleDateFormat
import javax.inject.Inject

class UserDiaryViewModel @Inject constructor(
    private val userSettingsViewModel: UserSettingsViewModel
) : BaseViewModel() {

    val setDiaryNoteEvent = SingleLiveEvent<Boolean>()

    lateinit var allNotesLiveData: MutableLiveData<List<DiaryNote>>
    lateinit var filteredNotesLiveData: MutableLiveData<List<DiaryNote>>
    lateinit var activeTrackerLiveData: MutableLiveData<DiaryNote?>

    var filterData = FilterData()

    init {
        EventBus.getDefault().register(this)

        allNotesLiveData = mutableLiveDataOf<List<DiaryNote>>(emptyList())
        filteredNotesLiveData = mutableLiveDataOf<List<DiaryNote>>(emptyList())
        activeTrackerLiveData = mutableLiveDataOf<DiaryNote?>(null)

        updateAllNotesLiveData()
    }

    private fun updateAllNotesLiveData() =
        viewModelScope.launch(Dispatchers.IO) {
            allNotesLiveData.postValue(App.database.userDiaryDao().getAll() ?: emptyList())

            updateFilteredNotes()
            updateActiveTrackerLiveData()
        }

    suspend fun updateFilteredNotes() =
        coroutineScope {
            withContext(Dispatchers.IO) {

                filteredNotesLiveData.postValue(
                    App.database.userDiaryDao().getAllFiltered(
                        noteType =
                        if (filterData.noteType == NoteType.All.id) null
                        else filterData.noteType,
                        startDay = filterData.startDay,
                        endDay = filterData.endDay,
                        pattern = filterData.pattern
                    )
                )

            }
        }

    private fun updateActiveTrackerLiveData() =
        viewModelScope.launch(Dispatchers.IO) {
            activeTrackerLiveData.postValue(App.database.userDiaryDao().getActiveTracker())
        }

    fun resetDiary() =
        viewModelScope.launch(Dispatchers.IO) {
            App.database.userDiaryDao().clear()
            updateAllNotesLiveData()
        }

    suspend fun getNotes() =
        coroutineScope {
            withContext(Dispatchers.IO) {
                App.database.userDiaryDao().getAll()
            }
        }
//        userDiaryRepository.getAllLiveData()

    suspend fun getHabits() =
        coroutineScope {
            withContext(Dispatchers.IO) {
                App.database.userDiaryDao().getHabits()
            }
        }
//        userDiaryRepository.getHabits()

    suspend fun getNoteMediaById(id: String) =
        coroutineScope {
            withContext(Dispatchers.IO) {
                App.database.userDiaryDao().getById(id)
            }
        }
//        userDiaryRepository.getByIdLiveData(id)

    suspend fun getActiveTracker() =
        coroutineScope {
            withContext(Dispatchers.IO) {
                updateActiveTrackerLiveData()
                App.database.userDiaryDao().getActiveTracker()
            }
        }
//        userDiaryRepository.getActiveTracker()

    private fun deleteDiaryNote(noteId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            App.database.userDiaryDao().deleteNoteById(noteId)
            updateAllNotesLiveData()
        }
    }

    fun changeTrackerState(
        tracker: DiaryNote,
        isActiveNow: Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            tracker.isActiveNow = isActiveNow
            if (!tracker.isActiveNow!!) tracker.datetimeEnd = System.currentTimeMillis()

            App.database.userDiaryDao().update(tracker)
            updateAllNotesLiveData()
        }
    }

    fun setNote(
        note: DiaryNote
    ) {
        Log.d("keke", "setNote")
//        EventBus.getDefault().post(ChangeProgressStateEvent(true))

        viewModelScope.launch(Dispatchers.IO) {
            val oldNote = App.database.userDiaryDao().getById(note.diaryNoteId)

            if (oldNote != null) {
                oldNote.text = note.text
                oldNote.date = note.date
                oldNote.media = note.media
                oldNote.changeOfPoints = note.changeOfPoints
                oldNote.interest = note.interest
                oldNote.datetimeStart = note.datetimeStart
                oldNote.datetimeEnd = note.datetimeEnd
                oldNote.isActiveNow = note.isActiveNow
                oldNote.isPushAvailable = note.isPushAvailable
                oldNote.initialAmount = note.initialAmount
                oldNote.currentAmount = note.currentAmount
                oldNote.regularity = note.regularity
                oldNote.color = note.color
                oldNote.datesCompletion = note.datesCompletion
                oldNote.tags = note.tags

                App.database.userDiaryDao().update(oldNote)
            } else {
                App.database.userDiaryDao().insert(note)
            }

//            setDiaryNoteEvent.postValue(true)

            userSettingsViewModel
                .getUserSettingsById(App.preferences.uid!!)?.let { userSettings ->
                        var amount: Float = when (note.changeOfPoints) {
                            1 -> userSettings.getDifficultyValue()
                            0 -> 0f
                            else -> -userSettings.getDifficultyValue()
                        }

//                    var amount = userSettings.getDifficultyValue()

                    Log.d("keke", "step1")
                    if (note.noteType == NoteType.Habit.id
                        && note.datesCompletion!!.none { it.datesCompletionIsCompleted == true }
                    ) amount = 0f

                    EventBus.getDefault()
                        .post(
                            UpdateUserInterestEvent(
                                interestId = note.interest!!.interestId,
                                amount = amount
                            )
                        )
                }

        }.invokeOnCompletion { updateAllNotesLiveData() }
    }

    @Subscribe
    fun onDeleteDiaryNoteEvent(e: DeleteDiaryNoteEvent) {
        deleteDiaryNote(e.noteId)
    }

    fun resetFilter(
        onComplete: () -> Unit
    ) {
        filterData.reset()
        viewModelScope.launch { updateFilteredNotes() }
            .invokeOnCompletion { onComplete.invoke() }
    }

    fun setupTags(
        onComplete: () -> Unit
    ) {
        GlobalScope.launch {
            val notes = App.database.userDiaryDao().getAll()
            val tags = mutableSetOf<String>()

            notes?.forEach { note ->
                if (!note.tags.isNullOrEmpty())
                    tags.addAll(note.tags!!.toList())
            }

            filterData.setupAllTags(tags.toList())
        }.invokeOnCompletion {
            viewModelScope.launch(Dispatchers.Main) { onComplete.invoke() }

        }
    }
}

data class FilterData(
    var noteType: Int = NoteType.All.id,
    var startDay: Long? = null,
    var endDay: Long? = null,
    var pattern: String? = null,
    var allTags: List<String>? = null,
    var tags: List<String>? = null
) {
    fun setupAllTags(allTags: List<String>?) {
        this.allTags = allTags
    }

    fun reset() {
        noteType = NoteType.All.id
        startDay = null
        endDay = null
        pattern = null
        tags = null
    }
}

class DateComparator : Comparator<DiaryNote?> {
    override fun compare(o1: DiaryNote?, o2: DiaryNote?): Int {
        return -o1!!.date.toLong().compareTo(o2!!.date.toLong())
    }
}

internal class StringDateComparator : Comparator<String?> {
    var dateFormat: SimpleDateFormat = SimpleDateFormat("MMMM, yyyy")
    override fun compare(lhs: String?, rhs: String?): Int {
        return dateFormat.parse(lhs).compareTo(dateFormat.parse(rhs))
    }
}
