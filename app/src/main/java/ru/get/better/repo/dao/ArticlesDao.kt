package ru.get.better.repo.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import ru.get.better.model.Article
import ru.get.better.model.DiaryNote

@Dao
interface ArticlesDao {
    @Query("SELECT * FROM ListStories")
    fun getAll(): List<Article>?

    @Query("SELECT * FROM ListStories WHERE MenuId = :type")
    fun getAllByType(type: Int): List<Article>?

    @Query("SELECT * FROM ListStories WHERE _id = :id")
    fun getById(id: Int): Article

    @Update
    fun update(article: Article)
}