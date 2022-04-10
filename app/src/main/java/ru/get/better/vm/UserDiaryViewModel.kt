package ru.get.better.vm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import ru.get.better.App
import ru.get.better.event.ChangeProgressStateEvent
import ru.get.better.event.DeleteDiaryNoteEvent
import ru.get.better.event.UpdateUserInterestEvent
import ru.get.better.model.*
import ru.get.better.rest.UserDiaryFields
import ru.get.better.rest.UserDiaryTable
import ru.get.better.util.SingleLiveEvent
import ru.get.better.util.ext.mutableLiveDataOf
import java.text.SimpleDateFormat
import javax.inject.Inject

class UserDiaryViewModel @Inject constructor(
    private val userSettingsViewModel: UserSettingsViewModel
) : BaseViewModel() {

    val setDiaryNoteEvent = SingleLiveEvent<Boolean>()

    lateinit var allNotesLiveData: MutableLiveData<List<DiaryNote>>
    lateinit var activeTrackerLiveData: MutableLiveData<DiaryNote?>

    init {
        EventBus.getDefault().register(this)

        allNotesLiveData = mutableLiveDataOf<List<DiaryNote>>(emptyList())
        activeTrackerLiveData = mutableLiveDataOf<DiaryNote?>(null)

        updateAllNotesLiveData()
    }

    private fun updateAllNotesLiveData() =
        GlobalScope.launch(Dispatchers.IO) {
            allNotesLiveData.postValue(App.database.userDiaryDao().getAll()?: emptyList())
            updateActiveTrackerLiveData()
        }

    private fun updateActiveTrackerLiveData() =
        GlobalScope.launch(Dispatchers.IO) {
            activeTrackerLiveData.postValue(App.database.userDiaryDao().getActiveTracker())
        }

    fun resetDiary() =
        GlobalScope.launch(Dispatchers.IO) {
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
        GlobalScope.launch(Dispatchers.IO) {
            App.database.userDiaryDao().deleteNoteById(noteId)
            updateAllNotesLiveData()
        }
    }

    fun changeTrackerState(
        tracker: DiaryNote,
        isActiveNow: Boolean
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            tracker.isActiveNow = isActiveNow
            if (!tracker.isActiveNow!!) tracker.datetimeEnd = System.currentTimeMillis().toString()

            App.database.userDiaryDao().update(tracker)
            updateAllNotesLiveData()
        }
    }

    fun setNote(
        note: DiaryNote
    ) {
        Log.d("keke", "setNote")
        EventBus.getDefault().post(ChangeProgressStateEvent(true))

        GlobalScope.launch(Dispatchers.IO) {
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

            setDiaryNoteEvent.postValue(true)

            userSettingsViewModel
                .getUserSettingsById(App.preferences.uid!!)?.let { userSettings ->
//                        var amount: Float = when (note.changeOfPoints) {
//                            0 -> userSettings!!.getDifficultyValue()
//                            1 -> 0f
//                            else -> -userSettings!!.getDifficultyValue()
//                        }

                    var amount = userSettings.getDifficultyValue()

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

            updateAllNotesLiveData()
        }
    }

    private fun DiaryNoteInterest.toFirestore() =
        hashMapOf(
            UserDiaryTable().tableFields[UserDiaryFields.InterestId] to interestId,
            UserDiaryTable().tableFields[UserDiaryFields.InterestName] to interestName,
            UserDiaryTable().tableFields[UserDiaryFields.InterestIcon] to interestIcon
        )

    private fun DiaryNoteDatesCompletion.toFirestore() =
        hashMapOf(
            UserDiaryTable().tableFields[UserDiaryFields.DatesCompletionDatetime] to datesCompletionDatetime,
            UserDiaryTable().tableFields[UserDiaryFields.DatesCompletionIsCompleted] to datesCompletionIsCompleted
        )

    @Subscribe
    fun onDeleteDiaryNoteEvent(e: DeleteDiaryNoteEvent) {
        deleteDiaryNote(e.noteId)
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
