package com.ndup.berealtechnicaltest.repository

import com.ndup.berealtechnicaltest.domain.FolderNameDto
import com.ndup.berealtechnicaltest.domain.Items
import com.ndup.berealtechnicaltest.domain.User
import javax.inject.Inject

class Repository @Inject constructor(
    private val service: ApiServices,
) : IRepository {
    override suspend fun getCurrentUser(): User = service.getCurrentUser()
    override suspend fun getFolderContent(folderId: String): Items = service.getFolderContent(folderId)
    override suspend fun createNewItem(folderId: String, folderName: String) = service.createNewItem(folderId, FolderNameDto(folderName))
    override suspend fun deleteItem(folderId: String) {
        service.deleteItemOrFolder(folderId)
    }
}