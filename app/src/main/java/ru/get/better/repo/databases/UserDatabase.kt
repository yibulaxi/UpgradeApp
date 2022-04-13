package ru.get.better.repo.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dagger.Module
import ru.get.better.model.*
import ru.get.better.repo.dao.UserAchievementsDao
import ru.get.better.repo.dao.UserDiaryDao
import ru.get.better.repo.dao.UserInterestsDao
import ru.get.better.repo.dao.UserSettingsDao

@Module
@Database(
    entities = [
        UserSettings::class,
        DiaryNote::class,
        UserAchievements::class,
        UserInterest::class
    ], version = 24
)
@TypeConverters(
    MediaConverters::class,
    DiaryNoteInterestConverters::class,
    DatesCompletionConverters::class,
    CompletedTasksConverters::class,
    CompletedAchievementsConverters::class
//    TagsConverters::class
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userSettingsDao(): UserSettingsDao
    abstract fun userDiaryDao(): UserDiaryDao
    abstract fun userAchievementsDao(): UserAchievementsDao
    abstract fun userInterestsDao(): UserInterestsDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context,
            AppDatabase::class.java, "get_better_dbb.db"
        )
            .build()
    }

}