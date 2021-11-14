package com.velkonost.upgrade.ui

import android.annotation.SuppressLint
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.velkonost.upgrade.App
import com.velkonost.upgrade.event.*
import com.velkonost.upgrade.model.*
import com.velkonost.upgrade.navigation.Navigator
import com.velkonost.upgrade.rest.*
import com.velkonost.upgrade.util.RxViewModel
import com.velkonost.upgrade.util.SingleLiveEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject

@SuppressLint("CheckResult")
class HomeViewModel @Inject constructor(
) : RxViewModel() {

    val errorEvent = SingleLiveEvent<String>()
    val successEvent = SingleLiveEvent<String>()
    val setupNavMenuEvent = SingleLiveEvent<String>()
    val setDiaryNoteEvent = SingleLiveEvent<Boolean>()

    private var currentInterests = mutableListOf<Interest>()
    private var startInterests = mutableListOf<Interest>()

    var userSettings: UserSettings = UserSettings()
    var diary = Diary()

    private var cloudFirestoreDatabase: FirebaseFirestore

    init {
        EventBus.getDefault().register(this)
        cloudFirestoreDatabase = Firebase.firestore
    }

    override fun onCleared() {
        EventBus.getDefault().unregister(this)
        super.onCleared()
    }

    private fun setUserSettings(
        documentSnapshot: DocumentSnapshot
    ) {
        userSettings = UserSettings(
            difficulty = documentSnapshot.get(UserSettingsTable().tableFields[UserSettingsFields.Difficulty]!!)
                .toString().toInt(),
            is_interests_initialized = documentSnapshot.getBoolean("is_interests_initialized"),
            is_push_available = documentSnapshot.getBoolean(UserSettingsTable().tableFields[UserSettingsFields.IsPushAvailable]!!),
            reg_time = documentSnapshot.getLong(UserSettingsTable().tableFields[UserSettingsFields.DateRegistration]!!)
        )
    }

    private fun setInterests(
        documentSnapshot: DocumentSnapshot
    ) {

        currentInterests.clear()
        startInterests.clear()

        try {
            val workInterest = Work()
            workInterest.selectedValue = documentSnapshot.get("1").toString().toFloat()

            val workInterestStart = Work()
            workInterestStart.selectedValue = documentSnapshot.get("1_start").toString().toFloat()

            val spiritInterest = Spirit()
            spiritInterest.selectedValue = documentSnapshot.get("2").toString().toFloat()

            val spiritInterestStart = Spirit()
            spiritInterestStart.selectedValue = documentSnapshot.get("2_start").toString().toFloat()

            val chillInterest = Chill()
            chillInterest.selectedValue = documentSnapshot.get("3").toString().toFloat()

            val chillInterestStart = Chill()
            chillInterestStart.selectedValue = documentSnapshot.get("3_start").toString().toFloat()

            val relationshipInterest = Relationship()
            relationshipInterest.selectedValue = documentSnapshot.get("4").toString().toFloat()

            val relationshipInterestStart = Relationship()
            relationshipInterestStart.selectedValue =
                documentSnapshot.get("4_start").toString().toFloat()

            val healthInterest = Health()
            healthInterest.selectedValue = documentSnapshot.get("5").toString().toFloat()

            val healthInterestStart = Health()
            healthInterestStart.selectedValue = documentSnapshot.get("5_start").toString().toFloat()

            val financeInterest = Finance()
            financeInterest.selectedValue = documentSnapshot.get("6").toString().toFloat()

            val financeInterestStart = Finance()
            financeInterestStart.selectedValue =
                documentSnapshot.get("6_start").toString().toFloat()

            val environmentInterest = Environment()
            environmentInterest.selectedValue = documentSnapshot.get("7").toString().toFloat()

            val environmentInterestStart = Environment()
            environmentInterestStart.selectedValue =
                documentSnapshot.get("7_start").toString().toFloat()

            val creationInterest = Creation()
            creationInterest.selectedValue = documentSnapshot.get("8").toString().toFloat()

            val creationInterestStart = Creation()
            creationInterestStart.selectedValue =
                documentSnapshot.get("8_start").toString().toFloat()

            currentInterests.add(workInterest)
            currentInterests.add(spiritInterest)
            currentInterests.add(chillInterest)
            currentInterests.add(relationshipInterest)
            currentInterests.add(healthInterest)
            currentInterests.add(financeInterest)
            currentInterests.add(environmentInterest)
            currentInterests.add(creationInterest)

            startInterests.add(workInterestStart)
            startInterests.add(spiritInterestStart)
            startInterests.add(chillInterestStart)
            startInterests.add(relationshipInterestStart)
            startInterests.add(healthInterestStart)
            startInterests.add(financeInterestStart)
            startInterests.add(environmentInterestStart)
            startInterests.add(creationInterestStart)
        } catch (e: Exception) {
            EventBus.getDefault().post(GoAuthEvent(true))
        }
    }

    fun getCurrentInterests() = currentInterests
    fun getStartInterests() = startInterests

    private fun setDiary(
        documentSnapshot: DocumentSnapshot
    ) {
        diary.notes.clear()

        try {
            documentSnapshot.data?.map {
                diary.notes.add(
                    DiaryNote(
                        id = (it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.Id]!!].toString(),
                        text = (it.value as HashMap<*, *>)[UserDiaryTable().tableFields[UserDiaryFields.Text]!!].toString(),
                        date = (it.value as HashMap<*, *>)["date"].toString(),
                        amount = (it.value as HashMap<*, *>)["amount"].toString(),
                        interestId = (it.value as HashMap<*, *>)["interest"].toString(),
                        mediaUrls = (it.value as HashMap<*, *>)["media"] as ArrayList<String>
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
            if (note.interestId == interestId) notes.add(note)
        }

        return notes
    }

    fun getStartInterestByInterestId(
        interestId: String
    ): Interest? {
        for (startInterest in startInterests) {
            if (startInterest.id.toString() == interestId) return startInterest
        }
        return null
    }

    fun getNoteMediaUrlsById(
        noteId: String? = null
    ): ArrayList<String> {
        if (noteId == null) return arrayListOf()
        for (note in diary.notes) {
            if (note.id == noteId) return note.mediaUrls
        }
        return arrayListOf()
    }

    @Subscribe
    fun onInitUserInterestsEvent(e: InitUserInterestsEvent) {
        cloudFirestoreDatabase
            .collection(UserInterestsTable().tableName).document(App.preferences.uid!!)
            .set(e.data)
            .addOnSuccessListener {
                App.preferences.isInterestsInitialized = true
                cloudFirestoreDatabase
                    .collection(UserSettingsTable().tableName).document(App.preferences.uid!!)
                    .update(mapOf("is_interests_initialized" to true))
                    .addOnSuccessListener {
                        getDiary()
                        getInterests { Navigator.welcomeToMetric(e.f) }
                    }
            }
            .addOnFailureListener { }
    }

    private fun getInterests(onSuccess: () -> Unit) {
        cloudFirestoreDatabase.collection(UserInterestsTable().tableName)
            .document(App.preferences.uid!!)
            .get()
            .addOnSuccessListener {
                setInterests(it).run {
                    onSuccess.invoke()
                    setupNavMenuEvent.postValue("success")
//                    if (binding.navView.menu.size() == 0) setupNavMenu()
                }
            }
            .addOnFailureListener {}
    }

    private fun setInterestAmount(
        interestId: String,
        amount: String
    ) {
        val data = mutableMapOf(
            interestId to amount
        )

        cloudFirestoreDatabase
            .collection(UserInterestsTable().tableName).document(App.preferences.uid!!)
            .update(data as Map<String, Any>)
            .addOnSuccessListener {
                getInterests { EventBus.getDefault().post(UpdateMetricsEvent(true)) }
                getDiary()
            }
            .addOnFailureListener {}
    }

    private fun getUserSettings() {
        cloudFirestoreDatabase.collection(UserSettingsTable().tableName)
            .document(App.preferences.uid!!)
            .get()
            .addOnSuccessListener {
                setUserSettings(it)
            }
            .addOnFailureListener { }
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

    private fun updateDifficulty(difficulty: Int) {
        val data = hashMapOf(
            UserSettingsTable().tableFields[UserSettingsFields.Difficulty] to difficulty
        )

        cloudFirestoreDatabase.collection(UserSettingsTable().tableName)
            .document(App.preferences.uid!!)
            .update(data as Map<String, Any>)
            .addOnSuccessListener {

            }
            .addOnFailureListener { }
    }

    private fun getDiary() {
        cloudFirestoreDatabase.collection(UserDiaryTable().tableName)
            .document(App.preferences.uid!!)
            .get()
            .addOnSuccessListener {
                setDiary(it)
                EventBus.getDefault().post(UpdateDiaryEvent(true))
            }
            .addOnFailureListener {}
    }

    @Subscribe
    fun onDeleteDiaryNoteEvent(e: DeleteDiaryNoteEvent) {
        deleteDiaryNote(e.noteId)
    }

    @Subscribe
    fun onUpdateDifficultyEvent(e: UpdateDifficultyEvent) {
        updateDifficulty(e.difficulty)
    }

    @Subscribe
    fun onUpdateUserInterestEvent(e: UpdateUserInterestEvent) {
        cloudFirestoreDatabase
            .collection(UserInterestsTable().tableName).document(App.preferences.uid!!)
            .get()
            .addOnSuccessListener {
                val interestPrevAmount = (it.get(e.interestId)).toString().toFloat()
                var interestNewAmount = interestPrevAmount + e.amount

                if (interestNewAmount > 10f) interestNewAmount = 10f
                if (interestNewAmount < 0f) interestNewAmount = 0f

                setInterestAmount(e.interestId, String.format("%.1f", interestNewAmount))
            }
            .addOnFailureListener {}
    }

    @Subscribe
    fun onInitUserSettingsEvent(e: InitUserSettingsEvent) {

        val userSettings = hashMapOf(
            "is_push_available" to true,
            UserSettingsTable().tableFields[UserSettingsFields.Difficulty] to 1,
            "is_interests_initialized" to false,
            "reg_time" to System.currentTimeMillis()
        )

        cloudFirestoreDatabase
            .collection(UserSettingsTable().tableName).document(e.userId)
            .set(userSettings)
            .addOnSuccessListener { }
            .addOnFailureListener { }
    }

    @Subscribe
    fun onLoadMainEvent(e: LoadMainEvent) {
        getUserSettings()
        getDiary()
        getInterests { Navigator.splashToMetric(e.f) }
    }

    public fun setDiaryNote(
        noteId: String? = null,
        text: String? = null,
        date: String? = null,
        selectedDiffPointToAddPost: Int? = 0,
        selectedInterestIdToAddPost: String? = null,
        mediaUrls: java.util.ArrayList<String>? = arrayListOf()
    ) {

        val amount: Float = when (selectedDiffPointToAddPost) {
            0 -> userSettings.getDifficultyValue()
            1 -> 0f
            else -> -userSettings.getDifficultyValue()
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