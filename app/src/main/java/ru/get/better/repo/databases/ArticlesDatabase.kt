package ru.get.better.repo.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dagger.Module
import ru.get.better.model.*
import ru.get.better.repo.dao.*

@Module
@Database(
    entities = [
        Article::class
    ], version = 2
)
abstract class ArticlesDatabase : RoomDatabase() {

    abstract fun articlesDao(): ArticlesDao

    companion object {
        @Volatile
        private var instance: ArticlesDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context,
            ArticlesDatabase::class.java, "get_better_articles.db"
        )
            .createFromAsset("database/articles.db")
            .build()
    }

}