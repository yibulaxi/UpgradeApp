package com.velkonost.upgrade.vm

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestoreException
import com.velkonost.upgrade.App
import com.velkonost.upgrade.event.DeleteDiaryNoteEvent
import com.velkonost.upgrade.event.UpdateDiaryEvent
import com.velkonost.upgrade.event.UpdateUserInterestEvent
import com.velkonost.upgrade.model.*
import com.velkonost.upgrade.repo.UserDiaryRepository
import com.velkonost.upgrade.rest.UserDiaryFields
import com.velkonost.upgrade.rest.UserDiaryTable
import com.velkonost.upgrade.util.SingleLiveEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject

class UserDiaryViewModel @Inject constructor(
    private val userSettingsViewModel: UserSettingsViewModel,
    private val userDiaryRepository: UserDiaryRepository
) : BaseViewModel() {

    init {
        EventBus.getDefault().register(this)
    }

    //    var diary = Diary()
    val setDiaryNoteEvent = SingleLiveEvent<Boolean>()

    private fun setDiary(
        documentSnapshot: DocumentSnapshot,
        onComplete: () -> Unit
    ) {
//        diary.notes.clear()

//        try {
        val firestoreDiaryNotes: ArrayList<DiaryNote> = arrayListOf()

        documentSnapshot.data?.map {
            firestoreDiaryNotes.add(
                DiaryNote(
                    noteType = (it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.NoteType]!!]
                        .toString().toInt(),
                    interest = ((it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.Interest]!!] as HashMap<*, *>)
                        .toDiaryNoteInterest(),
                    diaryNoteId = (it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.DiaryNoteId]!!].toString(),
                    text = (it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.Text]!!].toString(),
                    date = (it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.Date]].toString(),
                    changeOfPoints = (it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.ChangeOfPoints]].toString()
                        .toInt(),
                    media =
                    if ((it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.Media]] == null) arrayListOf()
                    else (it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.Media]] as ArrayList<String>,
                    tags = (it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.Tags]] as ArrayList<String>,
                    datetimeStart = (it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.DatetimeStart]].toString(),
                    datetimeEnd = (it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.DatetimeEnd]].toString(),
                    isActiveNow = (it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.IsActiveNow]].toString()
                        .toBoolean(),
                    isPushAvailable = (it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.IsPushAvailable]].toString()
                        .toBoolean(),
                    initialAmount =
                    if ((it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.InitialAmount]] == null) 0
                    else (it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.InitialAmount]]
                        .toString().toInt(),
                    currentAmount =
                    if ((it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.CurrentAmount]] == null) 0
                    else (it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.CurrentAmount]]
                        .toString().toInt(),
                    regularity =
                    if ((it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.Regularity]] == null) 0
                    else (it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.Regularity]]
                        .toString().toInt(),
                    color = (it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.Color]].toString(),
                    datesCompletion =
                    if ((it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.DatesCompletion]] == null) arrayListOf()
                    else ((it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.DatesCompletion]] as HashMap<*, *>)
                        .toDiaryNoteDatesCompletion()
                )
            )
        }.run {
            Log.d("keke", "omg")
            updateDiaryNotes(firestoreDiaryNotes)
            onComplete.invoke()
        }

//        } catch (e: Exception) {
//            EventBus.getDefault().post(GoAuthEvent(true))
//        }
    }

    private fun updateDiaryNotes(diaryNotes: ArrayList<DiaryNote>) =
//        viewModelScope.launch {
        userDiaryRepository.insertOrUpdateList(diaryNotes)
//        }
//        diaryNotes.forEach {
//            viewModelScope.launch {
//                userDiaryRepository.insertOrUpdate(it)
//            }
//        }

    fun getNotes() = userDiaryRepository.getAll()

    fun getNoteMediaById(id: String) = userDiaryRepository.getByIdLiveData(id)

    private fun deleteDiaryNote(noteId: String) {
        val deleteNote = hashMapOf(
            noteId to FieldValue.delete()
        )

        cloudFirestoreDatabase.collection(UserDiaryTable().tableName)
            .document(App.preferences.uid!!)
            .update(deleteNote as Map<String, Any>)
            .addOnSuccessListener {
//                CoroutineScope(Dispatchers.IO).launch {
                userDiaryRepository.deleteNoteById(noteId)
//                }
            }
            .addOnFailureListener { }
    }

    fun getDiary() {
        cloudFirestoreDatabase.collection(UserDiaryTable().tableName)
            .document(App.preferences.uid!!)
            .get()
            .addOnSuccessListener {
                setDiary(it) {
                    EventBus.getDefault().post(UpdateDiaryEvent(true))
                }
            }
            .addOnFailureListener {}
    }

    fun setNote(
        note: DiaryNote
    ) {

//        userSettingsViewModel.getUserSettingsById(App.preferences.uid!!).asFlow().collect {
//
//        }

        userSettingsViewModel.getUserSettingsById(App.preferences.uid!!)
            .observeForever { userSettings ->
                val amount: Float = when (note.changeOfPoints) {
                    0 -> userSettings!!.getDifficultyValue()
                    1 -> 0f
                    else -> -userSettings!!.getDifficultyValue()
                }

//        val amount = 0f
                val megaData = hashMapOf(
                    note.diaryNoteId to note.noteToFirestore()
                )

                cloudFirestoreDatabase.collection(UserDiaryTable().tableName)
                    .document(App.preferences.uid!!)
                    .update(megaData as Map<String, Any>)
                    .addOnSuccessListener {

                        setDiaryNoteEvent.postValue(true)

                        EventBus.getDefault()
                            .post(
                                UpdateUserInterestEvent(
                                    interestId = note.interest!!.interestId,
                                    amount = amount
                                )
                            )
                    }
                    .addOnFailureListener {
                        if (it is FirebaseFirestoreException && it.code.value() == 5) {
                            cloudFirestoreDatabase.collection(UserDiaryTable().tableName)
                                .document(App.preferences.uid!!)
                                .set(megaData as Map<String, Any>)
                                .addOnSuccessListener {
                                    setDiaryNoteEvent.postValue(true)

                                    EventBus.getDefault()
                                        .post(
                                            UpdateUserInterestEvent(
                                                interestId = note.interest!!.interestId,
                                                amount = amount
                                            )
                                        )
                                }
                                .addOnFailureListener {
                                    setDiaryNoteEvent.postValue(false)
                                }
                        } else {
                            setDiaryNoteEvent.postValue(false)
                        }
                    }
            }
    }

    private fun DiaryNote.noteToFirestore() =
        hashMapOf(
            UserDiaryTable().tableFields[UserDiaryFields.Id] to diaryNoteId,
            UserDiaryTable().tableFields[UserDiaryFields.DiaryNoteId] to diaryNoteId,
            UserDiaryTable().tableFields[UserDiaryFields.NoteType] to noteType,
            UserDiaryTable().tableFields[UserDiaryFields.Date] to date,
            UserDiaryTable().tableFields[UserDiaryFields.Text] to text,
            UserDiaryTable().tableFields[UserDiaryFields.Media] to media,
            UserDiaryTable().tableFields[UserDiaryFields.ChangeOfPoints] to changeOfPoints,
            UserDiaryTable().tableFields[UserDiaryFields.Tags] to tags,
            UserDiaryTable().tableFields[UserDiaryFields.Interest] to interest!!.toFirestore()
        )

    private fun DiaryNoteInterest.toFirestore() =
        hashMapOf(
            UserDiaryTable().tableFields[UserDiaryFields.InterestId] to interestId,
            UserDiaryTable().tableFields[UserDiaryFields.InterestName] to interestName,
            UserDiaryTable().tableFields[UserDiaryFields.InterestIcon] to interestIcon
        )

    private fun HashMap<*, *>.toDiaryNoteInterest() =
        DiaryNoteInterest(
            interestId = get(UserDiaryTable().tableFields[UserDiaryFields.InterestId]).toString(),
            interestName = get(UserDiaryTable().tableFields[UserDiaryFields.InterestName]).toString(),
            interestIcon = get(UserDiaryTable().tableFields[UserDiaryFields.InterestIcon]).toString(),
        )

    private fun HashMap<*, *>.toDiaryNoteDatesCompletion(): ArrayList<DiaryNoteDatesCompletion> =
        arrayListOf(

        )

    @Subscribe
    fun onDeleteDiaryNoteEvent(e: DeleteDiaryNoteEvent) {
        deleteDiaryNote(e.noteId)
    }
}