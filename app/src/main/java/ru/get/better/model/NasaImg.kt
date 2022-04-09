package ru.get.better.model

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AffirmationResponse(
    @field:JsonProperty("id") val id: String = "",
    @field:JsonProperty("image_l") val imageL: String = "",
    @field:JsonProperty("image_d") val imageD: String = "",
    @field:JsonProperty("ru") val ru: String = "",
    @field:JsonProperty("en") val en: String = "",
) : Parcelable