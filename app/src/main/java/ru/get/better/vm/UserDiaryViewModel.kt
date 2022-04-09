package ru.get.better.vm

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import ru.get.better.App
import ru.get.better.event.ChangeProgressStateEvent
import ru.get.better.event.DeleteDiaryNoteEvent
import ru.get.better.event.UpdateDiaryEvent
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

    init {
        EventBus.getDefault().register(this)
        updateAllNotesLiveData()
    }

    val setDiaryNoteEvent = SingleLiveEvent<Boolean>()

    val allNotesLiveData = mutableLiveDataOf<List<DiaryNote>>(emptyList())
    val activeTrackerLiveData = mutableLiveDataOf<DiaryNote?>(null)

//    private fun setDiary(
//        documentSnapshot: DocumentSnapshot,
//        onComplete: () -> Unit
//    ) {
//
////        try {
//        val firestoreDiaryNotes: ArrayList<DiaryNote> = arrayListOf()
//
//        documentSnapshot.data?.map {
//            firestoreDiaryNotes.add(
//                DiaryNote(
//                    noteType = (it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.NoteType]!!]
//                        .toString().toInt(),
//                    interest = ((it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.Interest]!!] as HashMap<*, *>)
//                        .toDiaryNoteInterest(),
//                    diaryNoteId = (it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.DiaryNoteId]!!].toString(),
//                    text = (it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.Text]!!].toString(),
//                    date = (it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.Date]].toString(),
//                    changeOfPoints = (it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.ChangeOfPoints]].toString()
//                        .toInt(),
//                    media =
//                    if ((it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.Media]] == null) arrayListOf()
//                    else (it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.Media]] as ArrayList<String>,
//                    tags = (it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.Tags]] as ArrayList<String>,
//                    datetimeStart = (it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.DatetimeStart]].toString(),
//                    datetimeEnd = (it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.DatetimeEnd]].toString(),
//                    isActiveNow = (it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.IsActiveNow]].toString()
//                        .toBoolean(),
//                    isPushAvailable = (it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.IsPushAvailable]].toString()
//                        .toBoolean(),
//                    initialAmount =
//                    if ((it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.InitialAmount]] == null) 0
//                    else (it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.InitialAmount]]
//                        .toString().toInt(),
//                    currentAmount =
//                    if ((it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.CurrentAmount]] == null) 0
//                    else (it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.CurrentAmount]]
//                        .toString().toInt(),
//                    regularity =
//                    if ((it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.Regularity]] == null) 0
//                    else (it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.Regularity]]
//                        .toString().toInt(),
//                    color = (it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.Color]].toString(),
//                    datesCompletion =
//                    if ((it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.DatesCompletion]] == null) arrayListOf()
//                    else ((it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.DatesCompletion]] as ArrayList<*>)
//                        .toDiaryNoteDatesCompletion()
//                )
//            )
//        }.run {
//            updateDiaryNotes(firestoreDiaryNotes)
//            onComplete.invoke()
//        }

//        } catch (e: Exception) {
//            EventBus.getDefault().post(GoAuthEvent(true))
//        }
//    }

//    private fun updateDiaryNotes(diaryNotes: ArrayList<DiaryNote>) =
//        userDiaryRepository.insertOrUpdateList(diaryNotes)

    private fun updateAllNotesLiveData() =
        GlobalScope.launch {
            allNotesLiveData.postValue(App.database.userDiaryDao().getAll())
            updateActiveTrackerLiveData()
        }

    private fun updateActiveTrackerLiveData() =
        GlobalScope.launch {
            activeTrackerLiveData.postValue(App.database.userDiaryDao().getActiveTracker())
        }

    fun resetDiary() =
        GlobalScope.launch {
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
        GlobalScope.launch {
            App.database.userDiaryDao().deleteNoteById(noteId)
            updateAllNotesLiveData()
        }
//        EventBus.getDefault().post(ChangeProgressStateEvent(true))
//
//        val deleteNote = hashMapOf(
//            noteId to FieldValue.delete()
//        )
//
//        cloudFirestoreDatabase.collection(UserDiaryTable().tableName)
//            .document(App.preferences.uid!!)
//            .update(deleteNote as Map<String, Any>)
//            .addOnSuccessListener {
////                CoroutineScope(Dispatchers.IO).launch {
//                userDiaryRepository.deleteNoteById(noteId)
////                }
//            }
//            .addOnFailureListener { }
    }

//    fun getDiary() {
//        Log.d("keke", "getDiary")
//        viewModelScope.launch(Dispatchers.IO) {
//            EventBus.getDefault().post(ChangeProgressStateEvent(true))
//
//            cloudFirestoreDatabase.collection(UserDiaryTable().tableName)
//                .document(App.preferences.uid!!)
//                .get()
//                .addOnSuccessListener {
//                    setDiary(it) {
////                        EventBus.getDefault().post(UpdateDiaryEvent(true))
//                        EventBus.getDefault().post(ChangeProgressStateEvent(isActive = false))
//                    }
//                }
//                .addOnFailureListener {}
//        }
//    }

    fun changeTrackerState(
        tracker: DiaryNote,
        isActiveNow: Boolean
    ) {
        GlobalScope.launch {
            tracker.isActiveNow = isActiveNow
            if (!tracker.isActiveNow!!) tracker.datetimeEnd = System.currentTimeMillis().toString()

            App.database.userDiaryDao().update(tracker)
            updateAllNotesLiveData()
        }
//        EventBus.getDefault().post(ChangeProgressStateEvent(true))

//        tracker.isActiveNow = isActiveNow
//        if (!tracker.isActiveNow!!) tracker.datetimeEnd = System.currentTimeMillis().toString()

//        val data = hashMapOf(
//            tracker.diaryNoteId to tracker.toFirestore()
//        )
//
//        cloudFirestoreDatabase.collection(UserDiaryTable().tableName)
//            .document(App.preferences.uid!!)
//            .update(data as Map<String, Any>)
//            .addOnSuccessListener {
//                getDiary()
//            }
//            .addOnFailureListener {
//
//            }
    }

    fun setNote(
        note: DiaryNote
    ) {
        Log.d("keke", "setNote")
        EventBus.getDefault().post(ChangeProgressStateEvent(true))

        GlobalScope.launch {
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

//        val data = hashMapOf(
//            note.diaryNoteId to note.toFirestore()
//        )

//        cloudFirestoreDatabase.collection(UserDiaryTable().tableName)
//            .document(App.preferences.uid!!)
//            .update(data as Map<String, Any>)
//            .addOnSuccessListener {
//
//                setDiaryNoteEvent.postValue(true)
//                getDiary()
//
//                GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
//                    userSettingsViewModel
//                        .getUserSettingsById(App.preferences.uid!!)?.let { userSettings ->
////                        var amount: Float = when (note.changeOfPoints) {
////                            0 -> userSettings!!.getDifficultyValue()
////                            1 -> 0f
////                            else -> -userSettings!!.getDifficultyValue()
////                        }
//
//                            var amount = userSettings.getDifficultyValue()
//
//                            Log.d("keke", "step1")
//                            if (note.noteType == NoteType.Habit.id
//                                && note.datesCompletion!!.none { it.datesCompletionIsCompleted == true }
//                            ) amount = 0f
//
//                            EventBus.getDefault()
//                                .post(
//                                    UpdateUserInterestEvent(
//                                        interestId = note.interest!!.interestId,
//                                        amount = amount
//                                    )
//                                )
//                        }
//                }
//            }
//            .addOnFailureListener {
//                if (it is FirebaseFirestoreException && it.code.value() == 5) {
//                    cloudFirestoreDatabase.collection(UserDiaryTable().tableName)
//                        .document(App.preferences.uid!!)
//                        .set(data as Map<String, Any>)
//                        .addOnSuccessListener {
//                            setDiaryNoteEvent.postValue(true)
//
//                            GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) {
//                                userSettingsViewModel
//                                    .getUserSettingsById(App.preferences.uid!!)?.let { userSettings ->
////                                        var amount: Float = when (note.changeOfPoints) {
////                                            0 -> userSettings!!.getDifficultyValue()
////                                            1 -> 0f
////                                            else -> -userSettings!!.getDifficultyValue()
////                                        }
//                                        var amount = userSettings.getDifficultyValue()
//
//                                        if (note.noteType == NoteType.Habit.id)
//                                            amount = 0f
//
//                                        EventBus.getDefault()
//                                            .post(
//                                                UpdateUserInterestEvent(
//                                                    interestId = note.interest!!.interestId,
//                                                    amount = amount
//                                                )
//                                            )
//                                    }
//                            }
//
////                            userSettingsViewModel.getUserSettingsById(App.preferences.uid!!)
////                                .observeOnce { userSettings ->
////
////                                }
//                        }
//                        .addOnFailureListener {
//                            setDiaryNoteEvent.postValue(false)
//                        }
//                } else {
//                    setDiaryNoteEvent.postValue(false)
//                }
//            }
//            }
    }

    private fun DiaryNote.toFirestore() =
        hashMapOf(
            UserDiaryTable().tableFields[UserDiaryFields.Id] to diaryNoteId,
            UserDiaryTable().tableFields[UserDiaryFields.DiaryNoteId] to diaryNoteId,
            UserDiaryTable().tableFields[UserDiaryFields.NoteType] to noteType,
            UserDiaryTable().tableFields[UserDiaryFields.Date] to date,
            UserDiaryTable().tableFields[UserDiaryFields.Text] to text,
            UserDiaryTable().tableFields[UserDiaryFields.Media] to media,
            UserDiaryTable().tableFields[UserDiaryFields.ChangeOfPoints] to changeOfPoints,
            UserDiaryTable().tableFields[UserDiaryFields.Tags] to tags,
            UserDiaryTable().tableFields[UserDiaryFields.Interest] to interest!!.toFirestore(),
            UserDiaryTable().tableFields[UserDiaryFields.DatetimeStart] to datetimeStart,
            UserDiaryTable().tableFields[UserDiaryFields.DatetimeEnd] to datetimeEnd,
            UserDiaryTable().tableFields[UserDiaryFields.IsActiveNow] to isActiveNow,
            UserDiaryTable().tableFields[UserDiaryFields.InitialAmount] to initialAmount,
            UserDiaryTable().tableFields[UserDiaryFields.CurrentAmount] to currentAmount,
            UserDiaryTable().tableFields[UserDiaryFields.Regularity] to regularity,
            UserDiaryTable().tableFields[UserDiaryFields.IsPushAvailable] to isPushAvailable,
            UserDiaryTable().tableFields[UserDiaryFields.Color] to color,
            UserDiaryTable().tableFields[UserDiaryFields.DatesCompletion] to datesCompletion?.map { it.toFirestore() },
        )

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

    private fun HashMap<*, *>.toDiaryNoteInterest() =
        DiaryNoteInterest(
            interestId = get(UserDiaryTable().tableFields[UserDiaryFields.InterestId]).toString(),
            interestName = get(UserDiaryTable().tableFields[UserDiaryFields.InterestName]).toString(),
            interestIcon = get(UserDiaryTable().tableFields[UserDiaryFields.InterestIcon]).toString(),
        )

    private fun ArrayList<*>.toDiaryNoteDatesCompletion(): ArrayList<DiaryNoteDatesCompletion> {
        val list = arrayListOf<DiaryNoteDatesCompletion>()
        forEach {
            list.add(
                DiaryNoteDatesCompletion(
                    datesCompletionIsCompleted =
                    (it as HashMap<String, String>)
                            [UserDiaryTable().tableFields[UserDiaryFields.DatesCompletionIsCompleted]]
                        .toString().toBoolean(),
                    datesCompletionDatetime =
                    it
                            [UserDiaryTable().tableFields[UserDiaryFields.DatesCompletionDatetime]]
                        .toString()
                )
            )

        }
        return list
    }

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
