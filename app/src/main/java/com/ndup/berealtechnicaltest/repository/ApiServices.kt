package com.ndup.berealtechnicaltest.repository

import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiServices {

    @GET("me")
    suspend fun getCurrentUser(): Nothing


    @GET("items/{id}")
    suspend fun getFolderContent(@Path("id") folderId: String): Nothing

    @POST("items/{id}")
    suspend fun createNewItemInFolder(@Path("id") folderId: String): Nothing

    @DELETE("items/{id}")
    suspend fun deleteItemOrFolder(@Path("id") itemId: String): Nothing


    @GET("items/{id}/data")
    suspend fun downloadItem(@Path("id") itemId: String): Nothing

}