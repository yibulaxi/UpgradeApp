package ru.get.better.rest

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query
import ru.get.better.BuildConfig
import ru.get.better.model.NasaImg

interface NasaService {

    @GET("planetary/apod")
    fun getSpaceImg(
        @Query("api_key") apiKey: String = BuildConfig.NASA_GOV_API_KEY,
    ): Single<NasaImg>

}