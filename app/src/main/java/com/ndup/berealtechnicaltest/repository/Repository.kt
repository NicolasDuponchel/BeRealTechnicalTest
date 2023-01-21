package com.ndup.berealtechnicaltest.repository

import com.ndup.berealtechnicaltest.domain.Either
import com.ndup.berealtechnicaltest.domain.Failure
import com.ndup.berealtechnicaltest.domain.FolderNameDto
import com.ndup.berealtechnicaltest.domain.Success
import com.ndup.berealtechnicaltest.domain.User
import javax.inject.Inject

class Repository @Inject constructor(
    private val service: ApiServices,
) : IRepository {
    override suspend fun getCurrentUser(): Either<User> = safeCallToApi { service.getCurrentUser() }

    override suspend fun getFolderContent(folderId: String) = safeCallToApi { service.getFolderContent(folderId) }

    override suspend fun createNewFolder(folderId: String, folderName: String) =
        safeCallToApi { service.createNewFolder(folderId, FolderNameDto(folderName)) }

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
    val result: T = block()
    Success(result)
} catch (e: Exception) {
    Failure(e)
}
