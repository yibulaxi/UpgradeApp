package ru.get.better.vm

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import ru.get.better.App
import ru.get.better.event.InitUserInterestsEvent
import ru.get.better.event.LoadMainEvent
import ru.get.better.event.UpdateMetricsEvent
import ru.get.better.event.UpdateUserInterestEvent
import ru.get.better.model.Interest
import ru.get.better.model.UserCustomInterest
import ru.get.better.model.UserInterest
import ru.get.better.util.ext.mutableLiveDataOf
import javax.inject.Inject

class UserInterestsViewModel @Inject constructor(
    private val userDiaryViewModel: UserDiaryViewModel,
    private val userSettingsViewModel: UserSettingsViewModel,
) : BaseViewModel() {

    lateinit var interestsLiveData: MutableLiveData<List<UserInterest>?>

    init {
        EventBus.getDefault().register(this)

        interestsLiveData = mutableLiveDataOf(emptyList())
    }

    fun init() {

    }

    suspend fun getInterestsLiveData() =
        coroutineScope {
            withContext(Dispatchers.IO) {
                App.database.userInterestsDao().getByUserIdLiveData(App.preferences.uid!!)
            }
        }

//            interestsLiveData.postValue(App.database.userInterestsDao().getByUserId(App.preferences.uid!!))


    suspend fun getInterestsByUserId() =
        coroutineScope {
            withContext(Dispatchers.IO) {
                App.preferences.uid?.let {
//                    updateInterestsLiveData()
                    App.database.userInterestsDao().getByUserId(App.preferences.uid!!)
                        ?: emptyList()
                } ?: emptyList()
            }
        }

    suspend fun getInterestById(id: String): UserInterest? =
        coroutineScope {
            withContext(Dispatchers.IO) {
                App.database.userInterestsDao().getById(id)
            }
        }

    @Subscribe
    fun onInitUserInterestsEvent(e: InitUserInterestsEvent) {
        GlobalScope.launch {
            e.data.forEach {
                App.database.userInterestsDao().insert(it.toUserInterest())
            }

//            updateInterestsLiveData()
            App.preferences.isInterestsInitialized = true

        }.invokeOnCompletion { EventBus.getDefault().post(LoadMainEvent()) }
    }

    fun updateInterest(interest: Interest, onSuccess: () -> Unit) {
        GlobalScope.launch {
            val userInterest = App.database.userInterestsDao().getById(interest.id)

            if (userInterest != null) {
                userInterest.name = interest.name
                userInterest.description = interest.description
                userInterest.startValue = interest.startValue
                userInterest.currentValue = interest.currentValue
                userInterest.logoId = interest.logoId!!.toInt()
                userInterest.dateLastUpdate = System.currentTimeMillis()

                App.database.userInterestsDao().update(userInterest)

//                updateInterestsLiveData()
                onSuccess.invoke()
            }
        }
    }

    fun addInterest(interest: Interest) {
        GlobalScope.launch {
            App.database.userInterestsDao().insert(interest.toUserInterest())

//            updateInterestsLiveData()
            EventBus.getDefault().post(UpdateMetricsEvent(true))
        }
    }

    fun deleteInterest(interest: Interest, onSuccess: () -> Unit) {
        GlobalScope.launch {
            val userInterest = App.database.userInterestsDao().getById(interest.id)

            if (userInterest != null) {
                App.database.userInterestsDao().delete(userInterest)

//                updateInterestsLiveData()
                EventBus.getDefault().post(UpdateMetricsEvent(true))
                onSuccess.invoke()
            }
        }
    }

    suspend fun calculateCurrentValueAverage(): Float =
        coroutineScope {
            withContext(Dispatchers.IO) {
                val list = App.database.userInterestsDao().getByUserId(App.preferences.uid!!)
                    ?: emptyList()

                var average = 0f
                list.forEach {
                    average += it.currentValue ?: 0f
                }

                average /= list.size

                average
            }
        }

    @Subscribe
    fun onUpdateUserInterestEvent(e: UpdateUserInterestEvent) {
        GlobalScope.launch {
            val userInterest = App.database.userInterestsDao().getById(e.interestId)

            if (userInterest != null) {
                userInterest.currentValue = userInterest.currentValue?.plus(e.amount)

                if (userInterest.currentValue!! > 10f) userInterest.currentValue = 10f
                if (userInterest.currentValue!! < 0f) userInterest.currentValue = 0f

                App.database.userInterestsDao().update(userInterest)
//                updateInterestsLiveData()
            }
        }
    }

    private fun Interest.toUserInterest() =
        UserInterest(
            interestId = id,
            name = name,
            description = description,
            startValue = startValue,
            currentValue = currentValue,
            logoId = logoId!!.toInt(),
            dateLastUpdate = System.currentTimeMillis(),
            userId = App.preferences.uid!!
        )

    fun List<Interest>.toUserInterestsList(): MutableList<UserInterest> {
        val list = mutableListOf<UserInterest>()

        forEach { interest ->
            list.add(interest.toUserInterest())
        }

        return list
    }


}

internal fun List<UserInterest>.toInterestsList(): MutableList<Interest> {
    val list = mutableListOf<Interest>()

    forEach { userInterest ->
        list.add(userInterest.toInterest())
    }

    return list
}

fun UserInterest.toInterest() =
    UserCustomInterest(
        id = interestId,
        name = name,
        description = description,
        startValue = startValue,
        currentValue = currentValue,
        dateLastUpdate = dateLastUpdate.toString(),
        logoId = logoId.toString(),
    )