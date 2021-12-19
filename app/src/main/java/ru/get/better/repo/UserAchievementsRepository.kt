package ru.get.better.repo

import dagger.Module
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.get.better.model.UserAchievements
import ru.get.better.repo.dao.UserAchievementsDao
import ru.get.better.repo.databases.UserDatabase
import javax.inject.Inject

@Module
class UserAchievementsRepository @Inject constructor(
    private val database: UserDatabase
) {
    private val userAchievementsDao: UserAchievementsDao = database.userAchievementsDao

    suspend fun insertOrUpdate(userAchievements: UserAchievements) {
        userAchievementsDao.getByIdLiveData(userAchievements.userId).observeForever {
            if (it == null) CoroutineScope(Dispatchers.IO).launch { insert(userAchievements) }
            else CoroutineScope(Dispatchers.IO).launch { update(userAchievements) }
        }
    }

    suspend fun insert(userAchievements: UserAchievements) {
        userAchievementsDao.insert(userAchievements)
    }

    suspend fun update(userAchievements: UserAchievements) {
        userAchievementsDao.update(userAchievements)
    }

    suspend fun clear() {
        userAchievementsDao.clear()
    }

    suspend fun getById(id: String): UserAchievements? {
        return userAchievementsDao.getById(id)
    }
}