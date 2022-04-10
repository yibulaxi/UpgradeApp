package ru.get.better.rest.di

import android.content.Context
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import ru.get.better.BuildConfig
import ru.get.better.rest.Json
import timber.log.Timber
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit
import javax.net.ssl.HttpsURLConnection

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
