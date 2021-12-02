package com.velkonost.upgrade.repo.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.velkonost.upgrade.model.UserSettings

@Dao
interface UserSettingsDao {

    @Query("SELECT * FROM user_settings_table")
    fun getAll(): LiveData<List<UserSettings>>

    @Query("SELECT * FROM user_settings_table WHERE userId = :id")
    fun getById(id: String): LiveData<UserSettings?>

    @Query("DELETE FROM user_settings_table")
    suspend fun clear()

    @Insert
    suspend fun insert(userSettings: UserSettings)

    @Update
    suspend fun update(userSettings: UserSettings)
}