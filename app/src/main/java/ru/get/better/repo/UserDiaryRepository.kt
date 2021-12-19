package ru.get.better.repo

import android.util.Log
import androidx.lifecycle.LiveData
import dagger.Module
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.get.better.model.DiaryNote
import ru.get.better.repo.dao.UserDiaryDao
import ru.get.better.repo.databases.UserDatabase
import javax.inject.Inject

@Module
class UserDiaryRepository @Inject constructor(
    database: UserDatabase,
) {

    private val userDiaryDao: UserDiaryDao = database.userDiaryDao

    fun getAllLiveData(): LiveData<List<DiaryNote>> = userDiaryDao.getAllLiveData()

    suspend fun getAll(): List<DiaryNote?> = userDiaryDao.getAll()

    fun getById(id: String): DiaryNote? = userDiaryDao.getById(id)
    fun getByIdLiveData(id: String): LiveData<DiaryNote?> = userDiaryDao.getByIdLiveData(id)

    private fun insertOrUpdate(diaryNote: DiaryNote) {
        CoroutineScope(Dispatchers.IO).launch {
            getById(diaryNote.diaryNoteId).let {
                if (it == null) insert(diaryNote)
                else update(diaryNote)
            }
        }
    }

    fun insertOrUpdateList(diaryNotes: ArrayList<DiaryNote>) {
        diaryNotes.forEach {
            insertOrUpdate(it)
        }
    }

    fun deleteNoteById(id: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val i = userDiaryDao.deleteNoteById(id)
        }
    }

    fun getActiveTracker() =
        userDiaryDao.getActiveTracker()

    fun getHabits() =
        userDiaryDao.getHabits()

    suspend fun insert(diaryNote: DiaryNote) {
        userDiaryDao.insert(diaryNote)
    }

    suspend fun update(diaryNote: DiaryNote) {
        userDiaryDao.update(diaryNote)
    }

    suspend fun delete(diaryNote: DiaryNote) {
        userDiaryDao.delete(diaryNote)
    }

    suspend fun clear() {
        userDiaryDao.clear()
    }

    suspend fun checkAchievementEachTypeNote(): Boolean {
        val isNoteExist = userDiaryDao.notesAmount()
        Log.d("keke_note", isNoteExist.toString())

        val isGoalExist = userDiaryDao.goalsAmount()
        Log.d("keke_goal", isGoalExist.toString())

        val isHabitExist = userDiaryDao.habitsAmount()
        Log.d("keke_habit", isHabitExist.toString())

        val isTrackerExist = userDiaryDao.trackersAmount()
        Log.d("keke_tracker", isTrackerExist.toString())

        return (isNoteExist != 0)
                && (isGoalExist != 0)
                && (isHabitExist != 0)
                && (isTrackerExist != 0)
    }

    suspend fun checkAchievement50Notes(): Boolean {
        Log.d("keke_all_notes", userDiaryDao.allNotesAmount().toString())
        return userDiaryDao.allNotesAmount() >= 50
    }

    suspend fun checkAchievement100Notes(): Boolean {
        return userDiaryDao.allNotesAmount() >= 100
    }

    suspend fun checkAchievement200Notes(): Boolean {
        return userDiaryDao.allNotesAmount() >= 200
    }

    suspend fun checkAchievement1HabitCompleted(): Boolean {
        return userDiaryDao.habits().any { habit ->
            habit!!.datesCompletion!!.none { dateCompletion ->
                dateCompletion.datesCompletionIsCompleted == false
            }
        }
    }

    suspend fun checkAchievement3HabitCompleted(): Boolean {
        Log.d("keke_all_habits", userDiaryDao.habits().filter { habit ->
            habit!!.datesCompletion!!.none { dateCompletion ->
                dateCompletion.datesCompletionIsCompleted == false
            }
        }.size.toString())
        return userDiaryDao.habits().filter { habit ->
            habit!!.datesCompletion!!.none { dateCompletion ->
                dateCompletion.datesCompletionIsCompleted == false
            }
        }.size >= 3
    }

    suspend fun checkAchievement9HabitCompleted(): Boolean {
        return userDiaryDao.habits().filter { habit ->
            habit!!.datesCompletion!!.none { dateCompletion ->
                dateCompletion.datesCompletionIsCompleted == false
            }
        }.size >= 9
    }

}