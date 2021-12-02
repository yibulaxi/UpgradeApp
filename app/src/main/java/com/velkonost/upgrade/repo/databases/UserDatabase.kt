package com.velkonost.upgrade.repo.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.velkonost.upgrade.model.*
import com.velkonost.upgrade.repo.dao.UserDiaryDao
import com.velkonost.upgrade.repo.dao.UserSettingsDao
import dagger.Module
import dagger.Provides

@Module
@Database(entities = [UserSettings::class, DiaryNote::class], version = 18)
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