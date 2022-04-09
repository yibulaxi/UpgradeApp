package ru.get.better.repo.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.get.better.model.UserAchievements

@Dao
interface UserAchievementsDao {

    @Query("SELECT * FROM user_achievements WHERE userId = :id")
    fun getById(id: String): UserAchievements?

    @Query("DELETE FROM user_achievements")
    fun clear()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userAchievements: UserAchievements)

    @Update
    fun update(userAchievements: UserAchievements)

}