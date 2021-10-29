package com.velkonost.upgrade.rest.di

import android.content.Context
import com.velkonost.upgrade.App
import com.velkonost.upgrade.BuildConfig
import com.velkonost.upgrade.rest.Json
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import timber.log.Timber
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit
import javax.net.ssl.HttpsURLConnection

fun createRetrofit(baseUrl: String, context: Context): Retrofit {
    val clientBuilder = OkHttpClient.Builder().apply {
        connectTimeout(60, TimeUnit.SECONDS)
        writeTimeout(60, TimeUnit.SECONDS)
        readTimeout(60, TimeUnit.SECONDS)
        addInterceptor(getHeaderInterceptor(context))

        if (BuildConfig.DEBUG) {
            addInterceptor(getLoggingInterceptor())
        }
    }

    val okHttpClient = clientBuilder.build()

    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
        .addConverterFactory(JacksonConverterFactory.create(Json.createObjectMapper()))
        .client(okHttpClient)
        .build()
}

fun createRetrofitWithoutHostInterceptor(baseUrl: String, context: Context): Retrofit {
    val clientBuilder = OkHttpClient.Builder().apply {
        connectTimeout(60, TimeUnit.SECONDS)
        writeTimeout(60, TimeUnit.SECONDS)
        readTimeout(60, TimeUnit.SECONDS)
        addInterceptor(getHeaderInterceptor(context))

        HttpsURLConnection.setDefaultHostnameVerifier { _, _ -> true }

        if (BuildConfig.DEBUG) addInterceptor(getLoggingInterceptor())

//        hostnameVerifier { hostname, session -> true }
    }.hostnameVerifier { _, _ -> true }

    val okHttpClient = clientBuilder.build()


    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
        .addConverterFactory(JacksonConverterFactory.create(Json.createObjectMapper()))
        .client(okHttpClient)
        .build()
}

private fun getHeaderInterceptor(context: Context) = Interceptor { chain ->
    val originalRequest = chain.request()
    with(originalRequest.newBuilder()) {

        if (App.preferences.authToken != null) {
            addHeader("Authorization", "Bearer ${App.preferences.authToken}")
        }

        originalRequest.headers.names().map {
            header(it, originalRequest.headers[it]!!)
        }

        val response: Response = chain.proceed(build())

        Timber.d("URL RESPONSE $response")

        val bufferValue: String? =
            response.body?.source()?.buffer?.clone()?.readString(Charset.defaultCharset())
//        EventBus.getDefault()
//            .post(
//                response.body()?.source()?.buffer?.let {
//                    RestResponseErrorHandlerEvent(response,
//                        it, bufferValue
//                    )
//                }
//            )

        response
    }
}

private fun getLoggingInterceptor(): Interceptor {
    val logging = HttpLoggingInterceptor()
    logging.level = HttpLoggingInterceptor.Level.BODY
    return logging
}