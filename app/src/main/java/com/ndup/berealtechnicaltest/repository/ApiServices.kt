package com.ndup.berealtechnicaltest.repository

import com.ndup.berealtechnicaltest.domain.FolderNameDto
import com.ndup.berealtechnicaltest.domain.Item
import com.ndup.berealtechnicaltest.domain.Items
import com.ndup.berealtechnicaltest.domain.User
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiServices {

    @GET("/me")
    suspend fun getCurrentUser(): User


    @GET("/items/{id}")
    suspend fun getFolderContent(@Path("id") folderId: String): Items

    @Headers("Content-Type: application/json")
    @POST("/items/{id}")
    suspend fun createNewFolder(@Path("id") folderId: String, @Body folderName: FolderNameDto): Item

    @Headers("Content-Type: application/octet-stream")
    @POST("/items/{id}")
    suspend fun upload(
        @Path("id") folderId: String,
        @Header("Content-Disposition") name: String,
        @Body bytes: RequestBody,
    ): Response<Item>

    @DELETE("/items/{id}")
    suspend fun deleteItemOrFolder(@Path("id") itemId: String): Response<Unit>

}