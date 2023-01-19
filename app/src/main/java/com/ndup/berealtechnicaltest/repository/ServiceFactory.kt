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

    private fun getRetrofit(
        baseUrl: String,
        debugUserName: String,
        debugUserPassword: String,
        logLevel: HttpLoggingInterceptor.Level,
    ) = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .client(getClient(debugUserName, debugUserPassword, logLevel))
        .build()

    private fun getClient(
        debugUserName: String,
        debugUserPassword: String,
        logLevel: HttpLoggingInterceptor.Level,
    ) = OkHttpClient.Builder()
        .addInterceptor { authInterceptor(it, debugUserName, debugUserPassword) }
        .addInterceptor(HttpLoggingInterceptor().apply { level = logLevel })
        .build()

    private fun authInterceptor(chain: Interceptor.Chain, user: String, password: String) = chain.proceed(
        chain
            .request()
            .newBuilder()
            .header("Authorization", Credentials.basic(user, password)).build()
    )


    fun service(
        baseUrl: String,
        userName: String,
        userPassword: String,
        logLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.NONE,
    ): ApiServices = getRetrofit(baseUrl, userName, userPassword, logLevel).create(ApiServices::class.java)

}