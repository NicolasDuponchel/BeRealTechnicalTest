package com.ndup.berealtechnicaltest.domain

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class User(
    val firstName: String,
    val lastName: String,
    val rootItem: RootItem,
) {

    val json get() = jsonSerializer.encodeToString(this)

    companion object {
        private val jsonSerializer = Json {
            prettyPrint = true
        }
    }
}

@Serializable
data class RootItem(
    val id: String,
    val parentId: String,
    val name: String,
    val isDir: Boolean,
    val modificationDate: String,
)
