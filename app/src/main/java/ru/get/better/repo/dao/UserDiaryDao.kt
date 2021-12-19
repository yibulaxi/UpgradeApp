package ru.get.better.repo.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.get.better.model.DiaryNote

@Dao
interface UserDiaryDao {
    @Query("SELECT * FROM user_diary_table")
    fun getAllLiveData(): LiveData<List<DiaryNote>>

    @Query("SELECT * FROM user_diary_table")
    suspend fun getAll(): List<DiaryNote?>

    @Query("SELECT * FROM user_diary_table WHERE diaryNoteId = :id")
    fun getById(id: String): DiaryNote?

    @Query("SELECT * FROM user_diary_table WHERE diaryNoteId = :id")
    fun getByIdLiveData(id: String): LiveData<DiaryNote?>

    @Query("SELECT * FROM user_diary_table WHERE noteType = 4 AND isActiveNow = 1 LIMIT 1")
    fun getActiveTracker(): LiveData<DiaryNote?>

    @Query("SELECT * FROM user_diary_table WHERE noteType = 2")
    fun getHabits(): LiveData<List<DiaryNote?>>

    @Query("DELETE FROM user_diary_table")
    suspend fun clear()

    @Query("DELETE FROM user_diary_table WHERE diaryNoteId = :id")
    suspend fun deleteNoteById(id: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(diaryNote: DiaryNote)

    @Update
    suspend fun update(diaryNote: DiaryNote)

    @Delete
    suspend fun delete(diaryNote: DiaryNote)

    @Query("SELECT COUNT(1) FROM user_diary_table")
    suspend fun allNotesAmount(): Int

    @Query("SELECT * FROM user_diary_table WHERE noteType = 2")
    suspend fun habits(): List<DiaryNote?>

    @Query("SELECT COUNT(1) FROM user_diary_table WHERE noteType = 1")
    suspend fun notesAmount(): Int

    @Query("SELECT COUNT(1) FROM user_diary_table WHERE noteType = 2")
    suspend fun habitsAmount(): Int

    @Query("SELECT COUNT(1) FROM user_diary_table WHERE noteType = 3")
    suspend fun goalsAmount(): Int

    @Query("SELECT COUNT(1) FROM user_diary_table WHERE noteType = 4")
    suspend fun trackersAmount(): Int
}