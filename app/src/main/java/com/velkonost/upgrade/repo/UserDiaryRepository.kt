package com.velkonost.upgrade.repo

import android.util.Log
import androidx.lifecycle.LiveData
import com.velkonost.upgrade.model.DiaryNote
import com.velkonost.upgrade.repo.dao.UserDiaryDao
import com.velkonost.upgrade.repo.databases.UserDatabase
import dagger.Module
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@Module
class UserDiaryRepository @Inject constructor(
    private val database: UserDatabase,
) {

    private val userDiaryDao: UserDiaryDao = database.userDiaryDao

    fun getAll(): LiveData<List<DiaryNote>> = userDiaryDao.getAll()

    fun getById(id: String): DiaryNote? = userDiaryDao.getById(id)
    fun getByIdLiveData(id: String): LiveData<DiaryNote?> = userDiaryDao.getByIdLiveData(id)

    fun insertOrUpdate(diaryNote: DiaryNote) {
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
            Log.d("kekeke", i.toString())
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
}