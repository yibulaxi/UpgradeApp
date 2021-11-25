package com.velkonost.upgrade.vm

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestoreException
import com.velkonost.upgrade.App
import com.velkonost.upgrade.event.*
import com.velkonost.upgrade.model.Diary
import com.velkonost.upgrade.model.DiaryNote
import com.velkonost.upgrade.model.DiaryNoteInterest
import com.velkonost.upgrade.navigation.Navigator
import com.velkonost.upgrade.rest.UserDiaryFields
import com.velkonost.upgrade.rest.UserDiaryTable
import com.velkonost.upgrade.util.SingleLiveEvent
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject

class UserDiaryViewModel @Inject constructor(
    private val userSettingsViewModel: UserSettingsViewModel
) : BaseViewModel() {

    init {
        EventBus.getDefault().register(this)
    }

    var diary = Diary()
    val setDiaryNoteEvent = SingleLiveEvent<Boolean>()

    private fun setDiary(
        documentSnapshot: DocumentSnapshot
    ) {
        diary.notes.clear()

        try {
            documentSnapshot.data?.map {
                diary.notes.add(
                    DiaryNote(
                        noteType = 1,
                        interest = DiaryNoteInterest(
                            interestId = "1",
                            interestName = "123",
                            interestIcon = "icon"
                        ),
                        id = (it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.Id]!!].toString(),
                        text = (it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.Text]!!].toString(),
                        date = (it.value as HashMap<*, *>)["date"].toString(),
                        changeOfPoints = (it.value as HashMap<*, *>)["amount"].toString().toInt(),

//                        interest = (it.value as HashMap<*, *>)["interest"].toString(),
                        media = (it.value as HashMap<*, *>)["media"] as ArrayList<String>
                    )
                )
            }
        } catch (e: Exception) {
            EventBus.getDefault().post(GoAuthEvent(true))
        }
    }

    fun getNotesByInterestId(
        interestId: String
    ): MutableList<DiaryNote> {
        val notes = mutableListOf<DiaryNote>()
        for (note in diary.notes) {
            if (note.interest.interestId == interestId) notes.add(note)
        }

        return notes
    }

    fun getNoteMediaUrlsById(
        noteId: String? = null
    ): ArrayList<String> {
        if (noteId == null) return arrayListOf()
        for (note in diary.notes) {
            if (note.id == noteId) return note.media?: arrayListOf()
        }
        return arrayListOf()
    }

    private fun deleteDiaryNote(noteId: String) {
        val deleteNote = hashMapOf(
            noteId to FieldValue.delete()
        )

        cloudFirestoreDatabase.collection(UserDiaryTable().tableName)
            .document(App.preferences.uid!!)
            .update(deleteNote as Map<String, Any>)
            .addOnSuccessListener { }
            .addOnFailureListener { }
    }

    fun getDiary() {
        Log.d("keke", "22")
        cloudFirestoreDatabase.collection(UserDiaryTable().tableName)
            .document(App.preferences.uid!!)
            .get()
            .addOnSuccessListener {
                setDiary(it)
                EventBus.getDefault().post(UpdateDiaryEvent(true))
            }
            .addOnFailureListener {}
    }

    public fun setDiaryNote(
        noteId: String? = null,
        text: String? = null,
        date: String? = null,
        selectedDiffPointToAddPost: Int? = 0,
        selectedInterestIdToAddPost: String? = null,
        mediaUrls: java.util.ArrayList<String>? = arrayListOf()
    ) {

        viewModelScope.launch {
            val amount: Float = when (selectedDiffPointToAddPost) {
                0 -> userSettingsViewModel.getDifficultyValue()
                1 -> 0f
                else -> -userSettingsViewModel.getDifficultyValue()
            }

            val diaryId = noteId ?: System.currentTimeMillis()
            val data = hashMapOf(
                UserDiaryTable().tableFields[UserDiaryFields.Id] to diaryId,
                UserDiaryTable().tableFields[UserDiaryFields.Text] to text,
                "date" to date,
                "interest" to selectedInterestIdToAddPost,
                "amount" to String.format("%.1f", amount),
                UserDiaryTable().tableFields[UserDiaryFields.Media] to mediaUrls
            )

            val megaData = hashMapOf(
                diaryId.toString() to data
            )

            cloudFirestoreDatabase.collection(UserDiaryTable().tableName)
                .document(App.preferences.uid!!)
                .update(megaData as Map<String, Any>)
                .addOnSuccessListener {

                    setDiaryNoteEvent.postValue(true)

                    EventBus.getDefault()
                        .post(
                            UpdateUserInterestEvent(
                                interestId = selectedInterestIdToAddPost!!,
                                amount = amount
                            )
                        )
                }
                .addOnFailureListener {
                    if (it is FirebaseFirestoreException && it.code.value() == 5) {
                        cloudFirestoreDatabase.collection(UserDiaryTable().tableName)
                            .document(com.velkonost.upgrade.App.preferences.uid!!)
                            .set(megaData as Map<String, Any>)
                            .addOnSuccessListener {
                                setDiaryNoteEvent.postValue(true)

                                EventBus.getDefault()
                                    .post(
                                        UpdateUserInterestEvent(
                                            interestId = selectedInterestIdToAddPost!!,
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

    @Subscribe
    fun onDeleteDiaryNoteEvent(e: DeleteDiaryNoteEvent) {
        deleteDiaryNote(e.noteId)
    }
}