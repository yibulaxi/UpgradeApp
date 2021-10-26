package com.velkonost.upgrade.rest.di

import android.content.Context
import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.velkonost.upgrade.BuildConfig
import com.velkonost.upgrade.di.scope.AppScope
import dagger.Module
import dagger.Provides
import retrofit2.Converter
import retrofit2.converter.jackson.JacksonConverterFactory

@Module(includes = [(JsonModule::class)])
class RetrofitModule(val context: Context) {

//    @AppScope
//    @Provides
//    fun retrofitService(): RestService =
//        createRetrofit(BuildConfig.REST_BASE_URL, context).create(RestService::class.java)
}

@Module
class JsonModule {

    @Provides
    @AppScope
    fun objectMapper(): ObjectMapper {
        val objectMapper = ObjectMapper()
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
        return objectMapper
    }

    @Provides
    @AppScope
    fun converterFactory(mapper: ObjectMapper): Converter.Factory = JacksonConverterFactory.create(mapper)
}
