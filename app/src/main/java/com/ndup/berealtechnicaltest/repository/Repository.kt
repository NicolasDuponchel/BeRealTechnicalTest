package com.ndup.berealtechnicaltest.repository

import com.ndup.berealtechnicaltest.domain.Items
import com.ndup.berealtechnicaltest.domain.User

class Repository(
    private val service: ApiServices,
) : IRepository {
    override suspend fun getCurrentUser(): User = service.getCurrentUser()
    override suspend fun getFolderContent(folderId: String): Items = service.getFolderContent(folderId)
}