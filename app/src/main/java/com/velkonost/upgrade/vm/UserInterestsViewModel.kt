package com.velkonost.upgrade.vm

import com.google.firebase.firestore.DocumentSnapshot
import com.velkonost.upgrade.App
import com.velkonost.upgrade.event.*
import com.velkonost.upgrade.model.*
import com.velkonost.upgrade.navigation.Navigator
import com.velkonost.upgrade.rest.UserInterestsTable
import com.velkonost.upgrade.rest.UserSettingsTable
import com.velkonost.upgrade.util.SingleLiveEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject

class UserInterestsViewModel @Inject constructor(
    private val userDiaryViewModel: UserDiaryViewModel
) : BaseViewModel() {

    init {
        EventBus.getDefault().register(this)
    }

    private var currentInterests = mutableListOf<Interest>()
    private var startInterests = mutableListOf<Interest>()

    private val getDiaryEvent = SingleLiveEvent<Boolean>()

    private fun setInterests(
        documentSnapshot: DocumentSnapshot
    ) {

        currentInterests.clear()
        startInterests.clear()

        try {
            val workInterest = Work()
            workInterest.currentValue = documentSnapshot.get("1").toString().toFloat()

            val workInterestStart = Work()
            workInterestStart.currentValue = documentSnapshot.get("1_start").toString().toFloat()

            val spiritInterest = Spirit()
            spiritInterest.currentValue = documentSnapshot.get("2").toString().toFloat()

            val spiritInterestStart = Spirit()
            spiritInterestStart.currentValue = documentSnapshot.get("2_start").toString().toFloat()

            val chillInterest = Chill()
            chillInterest.currentValue = documentSnapshot.get("3").toString().toFloat()

            val chillInterestStart = Chill()
            chillInterestStart.currentValue = documentSnapshot.get("3_start").toString().toFloat()

            val relationshipInterest = Relationship()
            relationshipInterest.currentValue = documentSnapshot.get("4").toString().toFloat()

            val relationshipInterestStart = Relationship()
            relationshipInterestStart.currentValue =
                documentSnapshot.get("4_start").toString().toFloat()

            val healthInterest = Health()
            healthInterest.currentValue = documentSnapshot.get("5").toString().toFloat()

            val healthInterestStart = Health()
            healthInterestStart.currentValue = documentSnapshot.get("5_start").toString().toFloat()

            val financeInterest = Finance()
            financeInterest.currentValue = documentSnapshot.get("6").toString().toFloat()

            val financeInterestStart = Finance()
            financeInterestStart.currentValue =
                documentSnapshot.get("6_start").toString().toFloat()

            val environmentInterest = Environment()
            environmentInterest.currentValue = documentSnapshot.get("7").toString().toFloat()

            val environmentInterestStart = Environment()
            environmentInterestStart.currentValue =
                documentSnapshot.get("7_start").toString().toFloat()

            val creationInterest = Creation()
            creationInterest.currentValue = documentSnapshot.get("8").toString().toFloat()

            val creationInterestStart = Creation()
            creationInterestStart.currentValue =
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

    fun getStartInterestByInterestId(
        interestId: String
    ): Interest? {
        for (startInterest in startInterests) {
            if (startInterest.id.toString() == interestId) return startInterest
        }
        return null
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
                        userDiaryViewModel.getDiary()

                        getInterests { Navigator.welcomeToMetric(e.f) }
                    }
            }
            .addOnFailureListener { }
    }

    internal fun getInterests(onSuccess: () -> Unit) {
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
                userDiaryViewModel.getDiary()
            }
            .addOnFailureListener {}
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

}