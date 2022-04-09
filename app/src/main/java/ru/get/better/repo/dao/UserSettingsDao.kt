package ru.get.better.repo.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.get.better.model.UserSettings

@Dao
interface UserSettingsDao {

    @Query("SELECT * FROM user_settings_table")
    fun getAll(): List<UserSettings>

    @Query("SELECT * FROM user_settings_table WHERE userId = :id")
    fun getById(id: String): UserSettings?

    @Query("DELETE FROM user_settings_table")
    fun clear()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userSettings: UserSettings)

    @Update
    fun update(userSettings: UserSettings)
}