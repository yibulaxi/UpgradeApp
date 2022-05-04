package ru.get.better.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ListStories")
data class Article(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "_id")
    val _id: Int,

    @ColumnInfo(name = "Name")
    val name: String?,

    @ColumnInfo(name = "MenuId")
    val menuId: Int?,

    @ColumnInfo(name = "Text")
    val text: String?,

    @ColumnInfo(name = "Position")
    val position: Int?,

    @ColumnInfo(name = "Flag")
    var flag: Int?,

    @ColumnInfo(name = "Img")
    val img: String?,

    @ColumnInfo(name = "ReadTime")
    val readTime: Int
)

enum class ArticleType(val flag: Int) {
    SHORT(1),
    MID(2),
    LONG(3)
}

enum class ArticleState(val flag: Int) {
    UNREAD(0),
    OPENED(1),
    READ(2)
}