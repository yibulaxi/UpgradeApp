package ru.get.better.repo.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.get.better.model.UserInterest

@Dao
interface UserInterestsDao {
    @Query("SELECT * FROM user_interests_table")
    fun getAll(): List<UserInterest>?

    @Query("SELECT * FROM user_interests_table WHERE userId = :userId")
    fun getByUserId(userId: String): List<UserInterest>?

    @Query("SELECT * FROM user_interests_table WHERE userId = :userId")
    fun getByUserIdLiveData(userId: String): LiveData<List<UserInterest>?>

    @Query("SELECT * FROM user_interests_table WHERE interestId = :id")
    fun getById(id: String): UserInterest?

    @Query("DELETE FROM user_interests_table")
    fun clear()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userInterest: UserInterest)

    @Update
    fun update(userInterest: UserInterest)

    @Delete
    fun delete(userInterest: UserInterest)
}