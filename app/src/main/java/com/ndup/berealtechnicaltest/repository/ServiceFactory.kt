package com.ndup.berealtechnicaltest.repository

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit


@OptIn(ExperimentalSerializationApi::class)
object ServiceFactory {

    private const val BaseUrl = "http://163.172.147.216:8080/"
    private const val DebugUserName = "noel"
    private const val DebugUserPassword = "foobar"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BaseUrl)
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .client(client)
        .build()

    private val client
        get() = OkHttpClient.Builder()
            .addInterceptor { authInterceptor(it, DebugUserName, DebugUserPassword) }
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }) // NONE
            .build()

    @Suppress("SameParameterValue")
    private fun authInterceptor(chain: Interceptor.Chain, user: String, password: String) = chain.proceed(
        chain
            .request()
            .newBuilder()
            .header("Authorization", Credentials.basic(user, password)).build()
    )


    val service: ApiServices = retrofit.create(ApiServices::class.java)

}