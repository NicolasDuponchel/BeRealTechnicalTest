package com.ndup.berealtechnicaltest.domain

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
data class Item(
    val id: String,
    val parentId: String,
    val name: String,
    val isDir: Boolean,
    val modificationDate: String,
    val size: Long? = null,
    val contentType: String? = null,
) {
    companion object {
        fun fromJson(json: String): Item = Json.decodeFromString(json)
        fun fromListJson(json: String): Items = Json.decodeFromString(json)
    }
}

typealias Items = List<Item>