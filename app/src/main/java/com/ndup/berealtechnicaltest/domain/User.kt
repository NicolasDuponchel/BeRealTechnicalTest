package com.ndup.berealtechnicaltest.domain

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val firstName: String,
    val lastName: String,
    val rootItem: RootItem,
)

@Serializable
data class RootItem(
    val id: String,
    val parentId: String,
    val name: String,
    val isDir: Boolean,
    val modificationDate: String,
)
