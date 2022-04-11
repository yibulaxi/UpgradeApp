package ru.get.better.rest.di

import android.content.Context
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import ru.get.better.rest.Json
import java.util.concurrent.TimeUnit

fun createRetrofit(baseUrl: String, context: Context): Retrofit {
    val clientBuilder = OkHttpClient.Builder().apply {
        connectTimeout(60, TimeUnit.SECONDS)
        writeTimeout(60, TimeUnit.SECONDS)
        readTimeout(60, TimeUnit.SECONDS)
    }

    val okHttpClient = clientBuilder.build()

    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
        .addConverterFactory(JacksonConverterFactory.create(Json.createObjectMapper()))
        .client(okHttpClient)
        .build()
}
