package com.ndup.berealtechnicaltest.repository

import com.ndup.berealtechnicaltest.domain.Either
import com.ndup.berealtechnicaltest.domain.Item
import com.ndup.berealtechnicaltest.domain.Items
import com.ndup.berealtechnicaltest.domain.User
import java.io.InputStream

interface IRepository {
    suspend fun getCurrentUser(): Either<User>
    suspend fun getFolderContent(folderId: String): Either<Items>
    suspend fun createNewFolder(folderId: String, folderName: String): Either<Item>
    suspend fun deleteItem(folderId: String): Either<Boolean>
    suspend fun uploadStream(
        folderId: String,
        fileName: String,
        inputStream: InputStream,
    ): Either<Item?>
}