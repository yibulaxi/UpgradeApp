package com.velkonost.upgrade.repo.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.velkonost.upgrade.model.UserSettings
import com.velkonost.upgrade.repo.dao.UserSettingsDao
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
@Database(entities = [UserSettings::class], version = 1)
public abstract class UserSettingsDatabase: RoomDatabase() {

    abstract val userSettingsDao: UserSettingsDao

    companion object {

        @Volatile
        private var INSTANCE: UserSettingsDatabase? = null

        @Provides
        fun getInstance(context: Context): UserSettingsDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        UserSettingsDatabase::class.java,
                        "user_database"
                    )
                        .allowMainThreadQueries()
                        .build()

                    INSTANCE = instance
                }

                return instance
            }
        }
    }

}