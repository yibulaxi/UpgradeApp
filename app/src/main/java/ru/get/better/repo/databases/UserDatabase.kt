package ru.get.better.repo.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dagger.Module
import dagger.Provides
import ru.get.better.model.*
import ru.get.better.repo.dao.UserDiaryDao
import ru.get.better.repo.dao.UserSettingsDao

@Module
@Database(entities = [UserSettings::class, DiaryNote::class], version = 20)
@TypeConverters(
    MediaConverters::class,
    DiaryNoteInterestConverters::class,
    DatesCompletionConverters::class,
//    TagsConverters::class
)
abstract class UserDatabase : RoomDatabase() {

    abstract val userSettingsDao: UserSettingsDao
    abstract val userDiaryDao: UserDiaryDao

    companion object {

        @Volatile
        private var INSTANCE: UserDatabase? = null

        @Provides
        fun getInstance(context: Context): UserDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        UserDatabase::class.java,
                        "user_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }

                return instance
            }
        }
    }

}