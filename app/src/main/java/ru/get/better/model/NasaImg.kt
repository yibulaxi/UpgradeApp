package ru.get.better.model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NasaImg(
    @field:JsonProperty("copyright") val copyright: String = "",
    @field:JsonProperty("date") val date: String = "",
    @field:JsonProperty("hdurl") val hdUrl: String = "",
    @field:JsonProperty("media_type") val mediaType: String = "",
    @field:JsonProperty("title") val title: String = "",
    @field:JsonProperty("url") val url: String = "",
): Parcelable