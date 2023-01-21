package com.ndup.berealtechnicaltest.repository

import com.ndup.berealtechnicaltest.domain.Either
import com.ndup.berealtechnicaltest.domain.Failure
import com.ndup.berealtechnicaltest.domain.FolderNameDto
import com.ndup.berealtechnicaltest.domain.Success
import com.ndup.berealtechnicaltest.domain.User
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream
import javax.inject.Inject

class Repository @Inject constructor(
    private val service: ApiServices,
) : IRepository {
    override suspend fun getCurrentUser(): Either<User> = safeCallToApi { service.getCurrentUser() }

    override suspend fun getFolderContent(folderId: String) = safeCallToApi { service.getFolderContent(folderId) }

    override suspend fun createNewFolder(folderId: String, folderName: String) =
        safeCallToApi { service.createNewFolder(folderId, FolderNameDto(folderName)) }

    override suspend fun uploadStream(
        folderId: String,
        fileName: String,
        inputStream: InputStream,
    ) = safeCallToApi {
        val requestBody: RequestBody = inputStream.readBytes().toRequestBody("application/octet-stream".toMediaTypeOrNull())
        service.upload(
            folderId = folderId,
            name = "attachment;filename*=utf-8''$fileName.jpg",
            bytes = requestBody,
        ).body()
    }

    override suspend fun deleteItem(folderId: String) = safeCallToApi {
        service.deleteItemOrFolder(folderId).isSuccessful
    }
}

/**
 * Small little fun I'm very proud of :P
 * It's wrapping possible errors from retrofit and expose it to the consumer via Either<left/right> pattern.
 * Of course, we should improve this in a real case with correct handling depending on the error type/code etc.
 */
private inline fun <reified T> safeCallToApi(block: () -> T): Either<T> = try {
    Success(block())
} catch (e: Exception) {
    Failure(e)
}
