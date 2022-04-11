package ru.get.better.vm

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import ru.get.better.App
import ru.get.better.model.*
import ru.get.better.rest.UserAchievementsFields
import ru.get.better.rest.UserAchievementsTable
import java.util.*
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.pow

class UserAchievementsViewModel @Inject constructor(
    private val userInterestsViewModel: UserInterestsViewModel
) : BaseViewModel() {

    init {
        EventBus.getDefault().register(this)
    }

    val achievementsLiveData = MutableLiveData<List<Achievement>>()
    val lvlLiveData = MutableLiveData<Int>()
    val lvlPercentLiveData = MutableLiveData<Float>()
    val efficiencyLiveData = MutableLiveData<Int>()

    fun getAchievements() {
        GlobalScope.launch {
            val list = getInitialAchievements()

            val notesInRow = calculateNotesInRow()
            val efficiency = calculateEfficiency()

            list.forEach { achievement ->
                achievement.isCompleted = when (achievement.achievementId) {
                    AchievementId.CreateEachTypeNote.id -> {
                        checkAchievementEachTypeNote()
                    }
                    AchievementId.Create50Notes.id -> {
                        checkAchievement50Notes()
                    }
                    AchievementId.Create100Notes.id -> {
                        checkAchievement100Notes()
                    }
                    AchievementId.Create200Notes.id -> {
                        checkAchievement200Notes()
                    }
                    AchievementId.Complete1Habit.id -> {
                        checkAchievement1HabitCompleted()
                    }
                    AchievementId.Complete3Habit.id -> {
                        checkAchievement3HabitCompleted()
                    }
                    AchievementId.Complete9Habit.id -> {
                        checkAchievement9HabitCompleted()
                    }
                    AchievementId.Get10PointsInterest.id -> {
                        userInterestsViewModel.getInterestsByUserId().any { it.currentValue == 10f }
                    }
                    AchievementId.CreateNotes3Days.id -> {
                        notesInRow >= 3
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
    }

    private suspend fun calculateEfficiency(): Int =
        coroutineScope {
            withContext(Dispatchers.IO) {
                var efficiency = 0

                val currentTime = System.currentTimeMillis()
                val currentDayEnd: Long = (currentTime / 86400000) * 86400000 + 86400000

                val timeStart: Long = currentDayEnd - 86400000 * 30L

                val allNotes = App.database.userDiaryDao().getAll() ?: emptyList()
                val notesWithoutHabits =
                    allNotes.filter { it.noteType != NoteType.Habit.id && it.date.toLong() >= timeStart }
                val habits =
                    allNotes.filter { it.noteType == NoteType.Habit.id }.getHabitsRealization()
                        .filter { it.date.toLong() >= timeStart }

                notesWithoutHabits.forEach { note ->
                    when (note.noteType) {
                        NoteType.Note.id -> efficiency += 3
                        NoteType.Goal.id -> efficiency += 3
                        NoteType.Tracker.id -> efficiency += 5
                    }
                }

                efficiency += habits.size

                if (efficiency > 100) efficiency = 100

                efficiencyLiveData.postValue(efficiency)
                efficiency
            }
        }


    private suspend fun calculateNotesInRow(): Int =
        coroutineScope {
            withContext(Dispatchers.IO) {
                var inRowAmount = 0
                var prevDayStart: Long = 0
                var prevDayEnd: Long = 0

                val notes = App.database.userDiaryDao().getAll()
                if (!notes.isNullOrEmpty()) {
                    Collections.sort(notes, DateComparator())

                    notes.forEach { note ->
                        val time = note.date.toLong()
                        val dayStart: Long = (time / 86400000) * 86400000
                        val dayEnd: Long = dayStart + 86400000

                        if (inRowAmount == 0)
                            inRowAmount = 1
                        else {
                            if (dayStart == prevDayStart && dayEnd == prevDayEnd) {

                            } else if (abs(dayStart - prevDayEnd) < 10000) {
                                inRowAmount++
                            } else {
                                inRowAmount = 0
                            }
                        }

                        prevDayStart = dayStart
                        prevDayEnd = dayEnd
                    }
                    inRowAmount
                } else {
                    inRowAmount
                }
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

    private suspend fun checkAchievementEachTypeNote(): Boolean =
        coroutineScope {
            withContext(Dispatchers.IO) {
                val isNoteExist = App.database.userDiaryDao().notesAmount()

                val isGoalExist = App.database.userDiaryDao().goalsAmount()

                val isHabitExist = App.database.userDiaryDao().habitsAmount()

                val isTrackerExist = App.database.userDiaryDao().trackersAmount()

                (isNoteExist != 0)
                        && (isGoalExist != 0)
                        && (isHabitExist != 0)
                        && (isTrackerExist != 0)
            }
        }

    private suspend fun checkAchievement50Notes(): Boolean =
        coroutineScope {
            withContext(Dispatchers.IO) {
                App.database.userDiaryDao().allNotesAmount() >= 50
            }
        }

    private suspend fun checkAchievement100Notes(): Boolean =
        coroutineScope {
            withContext(Dispatchers.IO) {
                App.database.userDiaryDao().allNotesAmount() >= 100
            }
        }

    private suspend fun checkAchievement200Notes(): Boolean =
        coroutineScope {
            withContext(Dispatchers.IO) {
                App.database.userDiaryDao().allNotesAmount() >= 200
            }
        }

    private suspend fun checkAchievement1HabitCompleted(): Boolean =
        coroutineScope {
            withContext(Dispatchers.IO) {
                App.database.userDiaryDao().habits().any { habit ->
                    habit!!.datesCompletion!!.none { dateCompletion ->
                        dateCompletion.datesCompletionIsCompleted == false
                    }
                }
            }
        }

    private suspend fun checkAchievement3HabitCompleted(): Boolean =
        coroutineScope {
            withContext(Dispatchers.IO) {
                App.database.userDiaryDao().habits().filter { habit ->
                    habit!!.datesCompletion!!.none { dateCompletion ->
                        dateCompletion.datesCompletionIsCompleted == false
                    }
                }.size >= 3
            }
        }

    private suspend fun checkAchievement9HabitCompleted(): Boolean =
        coroutineScope {
            withContext(Dispatchers.IO) {
                App.database.userDiaryDao().habits().filter { habit ->
                    habit!!.datesCompletion!!.none { dateCompletion ->
                        dateCompletion.datesCompletionIsCompleted == false
                    }
                }.size >= 9
            }
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
                    it
                            [UserAchievementsTable().tableFields[UserAchievementsFields.Datetime]]
                        .toString(),
                )
            )
        }

        return list
    }
}