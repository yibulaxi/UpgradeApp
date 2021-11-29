package com.velkonost.upgrade.repo

import androidx.lifecycle.LiveData
import com.velkonost.upgrade.model.UserSettings
import com.velkonost.upgrade.repo.dao.UserSettingsDao
import com.velkonost.upgrade.repo.databases.UserDatabase
import dagger.Module
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@Module
class UserSettingsRepository @Inject constructor(
    private val database: UserDatabase
) {

    private val userSettingsDao: UserSettingsDao = database.userSettingsDao

    suspend fun insertOrUpdate(userSettings: UserSettings) {
        userSettingsDao.getById(userSettings.userId).observeForever {
            if (it == null) CoroutineScope(Dispatchers.IO).launch { insert(userSettings) }
            else CoroutineScope(Dispatchers.IO).launch { update(userSettings) }
        }
    }

    suspend fun insert(userSettings: UserSettings) {
        userSettingsDao.insert(userSettings)
    }

    suspend fun update(userSettings: UserSettings) {
        userSettingsDao.update(userSettings)
    }

    fun clear() {
        userSettingsDao.clear()
    }

    fun getById(id: String): LiveData<UserSettings?> {
        return userSettingsDao.getById(id)
    }
}