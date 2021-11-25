package com.velkonost.upgrade.vm

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.velkonost.upgrade.App
import com.velkonost.upgrade.event.*
import com.velkonost.upgrade.model.*
import com.velkonost.upgrade.navigation.Navigator
import com.velkonost.upgrade.rest.UserInterestsFields
import com.velkonost.upgrade.rest.UserInterestsTable
import com.velkonost.upgrade.rest.UserSettingsTable
import com.velkonost.upgrade.util.ResourcesProvider
import com.velkonost.upgrade.util.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import javax.inject.Inject

class UserInterestsViewModel @Inject constructor(
    private val userDiaryViewModel: UserDiaryViewModel,
    private val userSettingsViewModel: UserSettingsViewModel,
//    private val resourcesProvider: ResourcesProvider
) : BaseViewModel() {

    init {
        EventBus.getDefault().register(this)
    }
    private var interests = mutableListOf<Interest>()

    private val getDiaryEvent = SingleLiveEvent<Boolean>()

    private fun setInterests(
        documentSnapshot: DocumentSnapshot
    ) {

        interests.clear()

        try {

            documentSnapshot.data?.map{
                interests.add(
                    UserCustomInterest(
                        id = (it.value as HashMap<*, *>)[UserInterestsTable().tableFields[UserInterestsFields.Id]].toString(),
                        name = (it.value as HashMap<*, *>)[UserInterestsTable().tableFields[UserInterestsFields.Name]].toString(),
                        description = (it.value as HashMap<*, *>)[UserInterestsTable().tableFields[UserInterestsFields.Description]].toString(),
                        startValue = (it.value as HashMap<*, *>)[UserInterestsTable().tableFields[UserInterestsFields.StartValue]].toString().toFloat(),
                        currentValue = (it.value as HashMap<*, *>)[UserInterestsTable().tableFields[UserInterestsFields.CurrentValue]].toString().toFloat(),
                        dateLastUpdate = (it.value as HashMap<*, *>)[UserInterestsTable().tableFields[UserInterestsFields.DateLastUpdate]].toString(),
                        logoId = (it.value as HashMap<*, *>)[UserInterestsTable().tableFields[UserInterestsFields.Icon]].toString()
                    )
                )
            }

        } catch (e: Exception) {
            EventBus.getDefault().post(GoAuthEvent(true))
        }
    }

    fun getInterests() = interests

    fun getInterestById(id: String): Interest =
        interests.first { it.id == id }

    @Subscribe
    fun onInitUserInterestsEvent(e: InitUserInterestsEvent) {
        val map = hashMapOf<String, HashMap<String, String>>()
        val list = e.data.map { it.toFirestore() }

        list.forEach {
            map.plusAssign(it)
        }

        cloudFirestoreDatabase
            .collection(UserInterestsTable().tableName).document(App.preferences.uid!!)
            .set(map)
            .addOnSuccessListener {
                App.preferences.isInterestsInitialized = true

                cloudFirestoreDatabase
                    .collection(UserSettingsTable().tableName).document(App.preferences.uid!!)
                    .update(mapOf("is_interests_initialized" to true))
                    .addOnSuccessListener {

                        viewModelScope.launch(Dispatchers.IO) {

                            userSettingsViewModel.getUserSettings()
                            Log.d("keke", "1")
                            userDiaryViewModel.getDiary()
                            Log.d("keke", "2")

                            getInterests { Navigator.welcomeToMetric(e.f) }
                            Log.d("keke", "3")
                        }
                    }
            }
            .addOnFailureListener { }
    }

    private fun Interest.toFirestore() =
        hashMapOf<String, HashMap<String, String>>(
            /*System.currentTimeMillis().toString() + java.util.UUID.randomUUID().toString()*/
            id to hashMapOf<String, String>(
                UserInterestsTable().tableFields[UserInterestsFields.Id]!! to id,
                UserInterestsTable().tableFields[UserInterestsFields.Name]!! to (name?: App.resourcesProvider.getString(nameRes!!)),
                UserInterestsTable().tableFields[UserInterestsFields.Description]!! to (description?: App.resourcesProvider.getString(descriptionRes!!)),
                UserInterestsTable().tableFields[UserInterestsFields.StartValue]!! to startValue.toString(),
                UserInterestsTable().tableFields[UserInterestsFields.CurrentValue]!! to currentValue.toString(),
                UserInterestsTable().tableFields[UserInterestsFields.DateLastUpdate]!! to System.currentTimeMillis().toString(),
                UserInterestsTable().tableFields[UserInterestsFields.Icon]!! to logoId.toString()
            )
        )

    internal fun getInterests(onSuccess: () -> Unit) {
        cloudFirestoreDatabase.collection(UserInterestsTable().tableName)
            .document(App.preferences.uid!!)
            .get()
            .addOnSuccessListener {
                setInterests(it).run {
                    onSuccess.invoke()
                    setupNavMenuEvent.postValue("success")
                }
            }
            .addOnFailureListener {}
    }

     fun updateInterest(interest: Interest) {
        cloudFirestoreDatabase
            .collection(UserInterestsTable().tableName).document(App.preferences.uid!!)
            .update(interest.toFirestore() as Map<String, Any>)
            .addOnSuccessListener {
                getInterests { EventBus.getDefault().post(UpdateMetricsEvent(true)) }
                userDiaryViewModel.getDiary()
            }
            .addOnFailureListener {}
    }

    fun addInterest(interest: Interest) {
        cloudFirestoreDatabase
            .collection(UserInterestsTable().tableName).document(App.preferences.uid!!)
            .update(interest.toFirestore() as Map<String, Any>)
            .addOnSuccessListener {
                getInterests { EventBus.getDefault().post(UpdateMetricsEvent(true)) }
                userDiaryViewModel.getDiary()
            }
            .addOnFailureListener { }
    }

     fun deleteInterest(interest: Interest) {
        cloudFirestoreDatabase
            .collection(UserInterestsTable().tableName).document(App.preferences.uid!!)
            .update(
                hashMapOf(
                    interest.id to FieldValue.delete()
                ) as Map<String, Any>
            )
            .addOnSuccessListener {
                getInterests { EventBus.getDefault().post(UpdateMetricsEvent(true)) }
                userDiaryViewModel.getDiary()
            }
            .addOnFailureListener {}
    }

    fun calculateCurrentValueAverage(): Float {
        val list = getInterests().toMutableList()
        list.remove(EmptyInterest())

        var average = 0f
        list.forEach {
            average += it.currentValue?: 0f
        }

        average /= list.size

        return average
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