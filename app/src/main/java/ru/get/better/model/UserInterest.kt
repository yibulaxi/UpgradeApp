package ru.get.better.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_interests_table")
data class UserInterest(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "interestId")
    val interestId: String = "",

    @ColumnInfo(name = "userId")
    var userId: String = "",

    @ColumnInfo(name = "name")
    var name: String? = "",

    @ColumnInfo(name = "description")
    var description: String? = "",

    @ColumnInfo(name = "startValue")
    var startValue: Float? = null,

    @ColumnInfo(name = "currentValue")
    var currentValue: Float? = null,

    @ColumnInfo(name = "dateLastUpdate")
    var dateLastUpdate: Long? = null,

    @ColumnInfo(name = "logoId")
    var logoId: Int = -1

)