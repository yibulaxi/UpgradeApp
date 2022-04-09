package ru.get.better.rest

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query
import ru.get.better.BuildConfig
import ru.get.better.model.AffirmationResponse

interface AffirmationService {

    @GET("/affirmations/affirmationsJson.json")
    fun getAffirmations(): Single<List<AffirmationResponse>>

}