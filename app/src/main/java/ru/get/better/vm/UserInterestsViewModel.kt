package ru.get.better.vm

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import ru.get.better.App
import ru.get.better.event.InitUserInterestsEvent
import ru.get.better.event.UpdateMetricsEvent
import ru.get.better.event.UpdateUserInterestEvent
import ru.get.better.model.EmptyInterest
import ru.get.better.model.Interest
import ru.get.better.model.UserCustomInterest
import ru.get.better.navigation.Navigator
import ru.get.better.rest.UserInterestsFields
import ru.get.better.rest.UserInterestsTable
import ru.get.better.rest.UserSettingsTable
import ru.get.better.util.SingleLiveEvent
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

//        try {

        documentSnapshot.data?.map {
            interests.add(
                UserCustomInterest(
                    id = (it.value as HashMap<*, *>)[UserInterestsTable().tableFields[UserInterestsFields.Id]].toString(),
                    name = (it.value as HashMap<*, *>)[UserInterestsTable().tableFields[UserInterestsFields.Name]].toString(),
                    description = (it.value as HashMap<*, *>)[UserInterestsTable().tableFields[UserInterestsFields.Description]].toString(),
                    startValue = (it.value as HashMap<*, *>)[UserInterestsTable().tableFields[UserInterestsFields.StartValue]].toString()
                        .toFloat(),
                    currentValue = (it.value as HashMap<*, *>)[UserInterestsTable().tableFields[UserInterestsFields.CurrentValue]].toString()
                        .toFloat(),
                    dateLastUpdate = (it.value as HashMap<*, *>)[UserInterestsTable().tableFields[UserInterestsFields.DateLastUpdate]].toString(),
                    logoId = (it.value as HashMap<*, *>)[UserInterestsTable().tableFields[UserInterestsFields.Icon]].toString()
                )
            )
        }

//        } catch (e: Exception) {
//            EventBus.getDefault().post(GoAuthEvent(true))
//        }
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
                viewModelScope.launch(Dispatchers.IO) {

                    userSettingsViewModel.getUserSettings()
                    userDiaryViewModel.getDiary()
                    getInterests { Navigator.toMetric(e.f) }
                }

//                cloudFirestoreDatabase
//                    .collection(UserSettingsTable().tableName).document(App.preferences.uid!!)
//                    .update(mapOf("is_interests_initialized" to true))
//                    .addOnSuccessListener {
//
//
//                    }
            }
            .addOnFailureListener { }
    }

    private fun Interest.toFirestore() =
        hashMapOf(
            /*System.currentTimeMillis().toString() + java.util.UUID.randomUUID().toString()*/
            id to hashMapOf<String, String>(
                UserInterestsTable().tableFields[UserInterestsFields.Id]!! to id,
                UserInterestsTable().tableFields[UserInterestsFields.Name]!! to name!!,
                UserInterestsTable().tableFields[UserInterestsFields.Description]!! to description!!,
                UserInterestsTable().tableFields[UserInterestsFields.StartValue]!! to startValue.toString(),
                UserInterestsTable().tableFields[UserInterestsFields.CurrentValue]!! to currentValue.toString(),
                UserInterestsTable().tableFields[UserInterestsFields.DateLastUpdate]!! to System.currentTimeMillis()
                    .toString(),
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

    fun updateInterest(interest: Interest, onSuccess: () -> Unit) {
        cloudFirestoreDatabase
            .collection(UserInterestsTable().tableName).document(App.preferences.uid!!)
            .update(interest.toFirestore() as Map<String, Any>)
            .addOnSuccessListener {
                getInterests { EventBus.getDefault().post(UpdateMetricsEvent(true)) }
                userDiaryViewModel.getDiary()
                onSuccess.invoke()
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

    fun deleteInterest(interest: Interest, onSuccess: () -> Unit) {
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
                onSuccess.invoke()
            }
            .addOnFailureListener {}
    }

    fun calculateCurrentValueAverage(): Float {
        val list = getInterests().toMutableList()
        list.remove(EmptyInterest())

        var average = 0f
        list.forEach {
            average += it.currentValue ?: 0f
        }

        average /= list.size

        return average
    }

    private fun setInterestAmount(
        interest: Interest
    ) {
        cloudFirestoreDatabase
            .collection(UserInterestsTable().tableName).document(App.preferences.uid!!)
            .update(interest.toFirestore() as Map<String, Any>)
            .addOnSuccessListener {
                getInterests { EventBus.getDefault().post(UpdateMetricsEvent(true)) }
                userDiaryViewModel.getDiary()
            }
            .addOnFailureListener {}
    }

    private fun HashMap<*, *>.toUserCustomInterest() =
        UserCustomInterest(
            id = get(UserInterestsTable().tableFields[UserInterestsFields.Id]).toString(),
            name = get(UserInterestsTable().tableFields[UserInterestsFields.Name]).toString(),
            description = get(UserInterestsTable().tableFields[UserInterestsFields.Description]).toString(),
            startValue = get(UserInterestsTable().tableFields[UserInterestsFields.StartValue]).toString()
                .toFloat(),
            currentValue = get(UserInterestsTable().tableFields[UserInterestsFields.CurrentValue]).toString()
                .toFloat(),
            logoId = get(UserInterestsTable().tableFields[UserInterestsFields.Icon]).toString()
        )

    @Subscribe
    fun onUpdateUserInterestEvent(e: UpdateUserInterestEvent) {
        cloudFirestoreDatabase
            .collection(UserInterestsTable().tableName).document(App.preferences.uid!!)
            .get()
            .addOnSuccessListener {

                Log.d("keke", "step2")
                val interestToUpdate =
                    (it.get(e.interestId) as HashMap<*, *>).toUserCustomInterest()
                interestToUpdate.currentValue = interestToUpdate.currentValue?.plus(e.amount)

                if (interestToUpdate.currentValue!! > 10f) interestToUpdate.currentValue = 10f
                if (interestToUpdate.currentValue!! < 0f) interestToUpdate.currentValue = 0f

                setInterestAmount(interestToUpdate)
            }
            .addOnFailureListener {}
    }

}