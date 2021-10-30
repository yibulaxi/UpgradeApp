package com.velkonost.upgrade.ui

import android.annotation.SuppressLint
import com.google.firebase.firestore.DocumentSnapshot
import com.velkonost.upgrade.event.GoAuthEvent
import com.velkonost.upgrade.model.*
import com.velkonost.upgrade.util.RxViewModel
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@SuppressLint("CheckResult")
class HomeViewModel @Inject constructor(
) : RxViewModel() {

    private var currentInterests = mutableListOf<Interest>()
    private var startInterests = mutableListOf<Interest>()

    private var diary = Diary()

    fun setInterests(documentSnapshot: DocumentSnapshot) {

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

    fun setDiary(documentSnapshot: DocumentSnapshot) {
        diary.notes.clear()

        try {
            documentSnapshot.data?.map {
                diary.notes.add(
                    DiaryNote(
                        (it.value as HashMap<*, *>)["id"].toString(),
                        (it.value as HashMap<*, *>)["text"].toString(),
                        (it.value as HashMap<*, *>)["date"].toString(),
                        (it.value as HashMap<*, *>)["amount"].toString(),
                        (it.value as HashMap<*, *>)["interest"].toString(),
                    )
                )
            }
        } catch (e: Exception) {
            EventBus.getDefault().post(GoAuthEvent(true))
        }
    }

    fun getDiary() = diary

    fun getNotesByInterestId(interestId: String): MutableList<DiaryNote> {
        val notes = mutableListOf<DiaryNote>()
        for (note in diary.notes) {
            if (note.interestId == interestId) notes.add(note)
        }

        return notes
    }

    fun getStartInterestByInterestId(interestId: String): Interest? {
        for (startInterest in startInterests) {
            if (startInterest.id.toString() == interestId) return startInterest
        }
        return null
    }
}