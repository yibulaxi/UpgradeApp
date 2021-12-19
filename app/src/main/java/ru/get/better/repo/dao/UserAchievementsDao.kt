package ru.get.better.repo.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.get.better.model.UserAchievements

@Dao
interface UserAchievementsDao {

    @Query("SELECT * FROM user_achievements WHERE userId = :id")
    suspend fun getById(id: String): UserAchievements?

    @Query("SELECT * FROM user_achievements WHERE userId = :id")
    fun getByIdLiveData(id: String): LiveData<UserAchievements?>

    @Query("DELETE FROM user_achievements")
    suspend fun clear()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userAchievements: UserAchievements)

    @Update
    suspend fun update(userAchievements: UserAchievements)

}