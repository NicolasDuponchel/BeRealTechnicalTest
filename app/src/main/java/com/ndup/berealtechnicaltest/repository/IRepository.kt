package com.ndup.berealtechnicaltest.repository

import com.ndup.berealtechnicaltest.domain.Items
import com.ndup.berealtechnicaltest.domain.User


interface IRepository {
    suspend fun getCurrentUser(): User
    suspend fun getFolderContent(folderId: String): Items
}