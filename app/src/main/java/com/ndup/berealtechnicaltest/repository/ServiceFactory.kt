package com.ndup.berealtechnicaltest.repository

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.ndup.berealtechnicaltest.login.ApiModelObject
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit


@OptIn(ExperimentalSerializationApi::class)
object ServiceFactory {

    private val retrofit
        get() = Retrofit.Builder()
            .baseUrl(ApiModelObject.baseUrl)
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .client(client)
            .build()

    private val client
        get() = OkHttpClient.Builder()
            .addInterceptor { authInterceptor(it) }
            .addInterceptor(HttpLoggingInterceptor().apply { level = ApiModelObject.logLevel })
            .build()

    private fun authInterceptor(chain: Interceptor.Chain) = chain.proceed(
        chain
            .request()
            .newBuilder()
            .header(ApiModelObject.headerCredentialName, ApiModelObject.credential).build()
    )


    val service: ApiServices
        get() = retrofit.create(ApiServices::class.java)

}