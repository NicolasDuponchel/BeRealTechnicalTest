package com.ndup.berealtechnicaltest.domain

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class FolderNameDto(val name: String) {
    val json get() = Json.encodeToString(this)
}
