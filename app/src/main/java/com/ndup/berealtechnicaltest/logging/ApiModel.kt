package com.ndup.berealtechnicaltest.logging

import okhttp3.Credentials
import okhttp3.logging.HttpLoggingInterceptor

object ApiModelObject {

    private data class ApiModel(
        val baseUrl: String = "http://163.172.147.216:8080",
        val name: String = "noel",
        val password: String = "foobar",
    )

    private var apiModel = ApiModel()

    fun updateCredentials(
        name: String,
        password: String,
    ) {
        apiModel = apiModel.copy(name = name, password = password)
    }

    val baseUrl = apiModel.baseUrl

    val credential: String get() = Credentials.basic(apiModel.name, apiModel.password)

    val logLevel = HttpLoggingInterceptor.Level.BODY

    fun getImageUrl(itemId: String) = "${baseUrl}/items/${itemId}/data"

    const val headerCredentialName = "Authorization"
}