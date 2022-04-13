package ru.get.better.repo.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.get.better.model.DiaryNote

@Dao
interface UserDiaryDao {
    @Query("SELECT * FROM user_diary_table")
    fun getAll(): List<DiaryNote>?

    @Query("SELECT * FROM user_diary_table")
    fun getAllLiveData(): LiveData<List<DiaryNote>?>

    @Query("SELECT * FROM user_diary_table WHERE noteType = :noteType")
    fun getAllFiltered(
        noteType: Int?
    ): List<DiaryNote>?

    @Query("SELECT * FROM user_diary_table WHERE diaryNoteId = :id")
    fun getById(id: String): DiaryNote?

    @Query("SELECT * FROM user_diary_table WHERE noteType = 4 AND isActiveNow = 1 LIMIT 1")
    fun getActiveTracker(): DiaryNote?

    @Query("SELECT * FROM user_diary_table WHERE noteType = 2")
    fun getHabits(): List<DiaryNote?>

    @Query("DELETE FROM user_diary_table")
    fun clear()

    @Query("DELETE FROM user_diary_table WHERE diaryNoteId = :id")
    fun deleteNoteById(id: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(diaryNote: DiaryNote)

    @Update
    fun update(diaryNote: DiaryNote)

    @Delete
    fun delete(diaryNote: DiaryNote)

    @Query("SELECT COUNT(1) FROM user_diary_table")
    fun allNotesAmount(): Int

    @Query("SELECT * FROM user_diary_table WHERE noteType = 2")
    fun habits(): List<DiaryNote?>

    @Query("SELECT COUNT(1) FROM user_diary_table WHERE noteType = 1")
    fun notesAmount(): Int

    @Query("SELECT COUNT(1) FROM user_diary_table WHERE noteType = 2")
    fun habitsAmount(): Int

    @Query("SELECT COUNT(1) FROM user_diary_table WHERE noteType = 3")
    fun goalsAmount(): Int

    @Query("SELECT COUNT(1) FROM user_diary_table WHERE noteType = 4")
    fun trackersAmount(): Int
}