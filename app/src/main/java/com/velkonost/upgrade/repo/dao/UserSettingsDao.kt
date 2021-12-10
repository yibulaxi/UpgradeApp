package com.velkonost.upgrade.repo.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.velkonost.upgrade.model.UserSettings

@Dao
interface UserSettingsDao {

    @Query("SELECT * FROM user_settings_table")
    fun getAll(): LiveData<List<UserSettings>>

    @Query("SELECT * FROM user_settings_table WHERE userId = :id")
    fun getById(id: String): LiveData<UserSettings?>

    @Query("DELETE FROM user_settings_table")
    suspend fun clear()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userSettings: UserSettings)

    @Update
    suspend fun update(userSettings: UserSettings)
}