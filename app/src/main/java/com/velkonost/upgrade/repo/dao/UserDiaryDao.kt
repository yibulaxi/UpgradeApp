package com.velkonost.upgrade.repo.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.velkonost.upgrade.model.DiaryNote

@Dao
interface UserDiaryDao {
    @Query("SELECT * FROM user_diary_table")
    fun getAll(): LiveData<List<DiaryNote>>

    @Query("SELECT * FROM user_diary_table WHERE diaryNoteId = :id")
    fun getById(id: String): DiaryNote?

    @Query("SELECT * FROM user_diary_table WHERE diaryNoteId = :id")
    fun getByIdLiveData(id: String): LiveData<DiaryNote?>

    @Query("SELECT * FROM user_diary_table WHERE noteType = 4 AND isActiveNow = 1 LIMIT 1")
    fun getActiveTracker(): LiveData<DiaryNote?>

    @Query("SELECT * FROM user_diary_table WHERE noteType = 2")
    fun getHabits(): LiveData<List<DiaryNote?>>

    @Query("DELETE FROM user_diary_table")
    fun clear()

    @Query("DELETE FROM user_diary_table WHERE diaryNoteId = :id")
    suspend fun deleteNoteById(id: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(diaryNote: DiaryNote)

    @Update
    suspend fun update(diaryNote: DiaryNote)

    @Delete
    suspend fun delete(diaryNote: DiaryNote)
}