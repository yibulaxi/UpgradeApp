package ru.get.better.vm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import ru.get.better.App
import ru.get.better.model.*
import ru.get.better.repo.UserAchievementsRepository
import ru.get.better.repo.UserDiaryRepository
import ru.get.better.rest.UserAchievementsFields
import ru.get.better.rest.UserAchievementsTable
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.*
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.pow

class UserAchievementsViewModel @Inject constructor(
    private val userSettingsViewModel: UserSettingsViewModel,
    private val userAchievementsRepository: UserAchievementsRepository,
    private val userDiaryRepository: UserDiaryRepository,
    var userInterestsViewModel: UserInterestsViewModel
) : BaseViewModel() {

    init {
        EventBus.getDefault().register(this)
    }

    private fun setUserAchievements(
        documentSnapshot: DocumentSnapshot
    ) {
        val firestoreUserAchievements =
            UserAchievements(
                userId = documentSnapshot.getString(UserAchievementsTable().tableFields[UserAchievementsFields.UserId]!!)!!,
                userLvl = documentSnapshot.getString(UserAchievementsTable().tableFields[UserAchievementsFields.UserLvl]!!)!!,
                userExp = documentSnapshot.getString(UserAchievementsTable().tableFields[UserAchievementsFields.UserExp]!!)!!,
                efficiency = documentSnapshot.getString(UserAchievementsTable().tableFields[UserAchievementsFields.Efficiency]!!)!!,
                completedTasks =
                if (documentSnapshot.get(UserAchievementsTable().tableFields[UserAchievementsFields.CompletedTasks]!!) == null) arrayListOf()
                else (documentSnapshot.get(UserAchievementsTable().tableFields[UserAchievementsFields.CompletedTasks]!!) as ArrayList<*>)
                    .toCompletedTasks(),
                completedAchievements =
                if (documentSnapshot.get(UserAchievementsTable().tableFields[UserAchievementsFields.CompletedAchievements]!!) == null) arrayListOf()
                else (documentSnapshot.get(UserAchievementsTable().tableFields[UserAchievementsFields.CompletedAchievements]!!) as ArrayList<*>)
                    .toCompletedAchievements(),
            )

        updateUserAchievements(firestoreUserAchievements)
    }

    private fun updateUserAchievements(
        userAchievements: UserAchievements
    ) = viewModelScope.launch {
        userAchievementsRepository.insertOrUpdate(userAchievements)
    }

    fun completeAchievement(achievement: CompletedAchievement) {

    }

    fun completeTask(task: Task) {

    }

    val achievementsLiveData = MutableLiveData<List<Achievement>>()
    val lvlLiveData = MutableLiveData<Int>()
    val lvlPercentLiveData = MutableLiveData<Float>()
    val efficiencyLiveData = MutableLiveData<Int>()

    suspend fun getAchievements() {
        val list = getInitialAchievements()

        val notesInRow = calculateNotesInRow()
        val efficiency = calculateEfficiency()

        list.forEach { achievement ->
            achievement.isCompleted = when (achievement.achievementId) {
                AchievementId.CreateEachTypeNote.id -> {
                    userDiaryRepository.checkAchievementEachTypeNote()
                }
                AchievementId.Create50Notes.id -> {
                    userDiaryRepository.checkAchievement50Notes()
                }
                AchievementId.Create100Notes.id -> {
                    userDiaryRepository.checkAchievement100Notes()
                }
                AchievementId.Create200Notes.id -> {
                    userDiaryRepository.checkAchievement200Notes()
                }
                AchievementId.Complete1Habit.id -> {
                    userDiaryRepository.checkAchievement1HabitCompleted()
                }
                AchievementId.Complete3Habit.id -> {
                    userDiaryRepository.checkAchievement3HabitCompleted()
                }
                AchievementId.Complete9Habit.id -> {
                    userDiaryRepository.checkAchievement9HabitCompleted()
                }
                AchievementId.Get10PointsInterest.id -> {
                    userInterestsViewModel.getInterests().any { it.currentValue == 10f }
                }
                AchievementId.CreateNotes3Days.id -> {
                    notesInRow >= 3
//                    userAchievementsRepository.getById(App.preferences.uid!!)!!.completedAchievements
//                        .firstOrNull { it.achievementId == AchievementId.CreateNotes3Days.id }
//                        .let { completedAchievement ->
//                            if (completedAchievement != null) true
//                            else notesInRow >= 3
//                        }
                }
                AchievementId.CreateNotes7Days.id -> {
                    notesInRow >= 7
                }
                AchievementId.CreateNotes21Days.id -> {
                    notesInRow >= 21
                }
                AchievementId.Get50Efficiency.id -> {
                    efficiency >= 50
                }
                AchievementId.Get70Efficiency.id -> {
                    efficiency >= 70
                }
                AchievementId.Get100Efficiency.id -> {
                    efficiency >= 100
                }
                else -> {
                    false
                }
            }
        }

        var experience = 0
        list.filter { it.isCompleted }.forEach { experience += it.experience }
        calculateExp(experience)

        achievementsLiveData.postValue(list)
    }

    private suspend fun calculateEfficiency(): Int {
        var efficiency = 0

        val currentTime = System.currentTimeMillis()
        val currentDayEnd: Long = (currentTime / 86400000) * 86400000 + 86400000

        val timeStart: Long = currentDayEnd - 86400000 * 30L

        val allNotes = userDiaryRepository.getAll()
        val notesWithoutHabits = allNotes.filter { it!!.noteType != NoteType.Habit.id && it.date.toLong() >= timeStart }
        val habits = allNotes.filter { it!!.noteType == NoteType.Habit.id }.getHabitsRealization().filter { it.date.toLong() >= timeStart }

        notesWithoutHabits.forEach { note ->
            when (note!!.noteType) {
                NoteType.Note.id -> efficiency += 3
                NoteType.Goal.id -> efficiency += 3
                NoteType.Tracker.id -> efficiency += 5
            }
        }

        efficiency += habits.size

        if (efficiency > 100) efficiency = 100

        efficiencyLiveData.postValue(efficiency)
        return efficiency
    }

    private suspend fun calculateNotesInRow(): Int {
        var inRowAmount = 0
        var prevDayStart: Long = 0
        var prevDayEnd: Long = 0

        val notes = userDiaryRepository.getAll()
        if (!notes.isNullOrEmpty()) {
            Collections.sort(notes, DateComparator())

            notes.forEach { note ->
                val time = note!!.date.toLong()
                val dayStart: Long = (time / 86400000) * 86400000
                val dayEnd: Long = dayStart + 86400000

                if (inRowAmount == 0)
                    inRowAmount = 1
                else {
                    if (dayStart == prevDayStart && dayEnd == prevDayEnd) {

                    }
                    else if (abs(dayStart - prevDayEnd) < 10000) {
                        inRowAmount++
                    } else {
                        inRowAmount = 0
                    }
                }

                prevDayStart = dayStart
                prevDayEnd = dayEnd
            }
            return inRowAmount
        } else {
            return inRowAmount
        }
    }


    private fun calculateExp(experience: Int) {
        var lvl = 1.0
        val q = 1.6
        while (experience > (q.pow(lvl) - 1) / (q - 1)) {
            lvl++
        }

        lvl -= 1
        val remainExp = experience - (q.pow(lvl) - 1) / (q - 1)
        val lvlPercent = remainExp / q.pow(lvl)

        lvlLiveData.postValue(lvl.toInt())
        lvlPercentLiveData.postValue(lvlPercent.toFloat())
    }


    internal fun getUserAchievements() {
        cloudFirestoreDatabase.collection(UserAchievementsTable().tableName)
            .document(App.preferences.uid!!)
            .get()
            .addOnSuccessListener {
                setUserAchievements(it)
            }
            .addOnFailureListener { }
    }

    private fun ArrayList<*>.toCompletedTasks(): ArrayList<CompletedTask> {
        val list = arrayListOf<CompletedTask>()

        forEach {
            list.add(
                CompletedTask(
                    taskId =
                    (it as HashMap<String, String>)
                            [UserAchievementsTable().tableFields[UserAchievementsFields.TaskId]]
                        .toString(),
                    datetime =
                    it
                            [UserAchievementsTable().tableFields[UserAchievementsFields.Datetime]]
                        .toString(),
                )
            )
        }

        return list
    }

    private fun ArrayList<*>.toCompletedAchievements(): ArrayList<CompletedAchievement> {
        val list = arrayListOf<CompletedAchievement>()

        forEach {
            list.add(
                CompletedAchievement(
                    achievementId =
                    (it as HashMap<String, String>)
                            [UserAchievementsTable().tableFields[UserAchievementsFields.AchievementId]]
                        .toString(),
                    datetime =
                    (it as HashMap<String, String>)
                            [UserAchievementsTable().tableFields[UserAchievementsFields.Datetime]]
                        .toString(),
                )
            )
        }

        return list
    }
}