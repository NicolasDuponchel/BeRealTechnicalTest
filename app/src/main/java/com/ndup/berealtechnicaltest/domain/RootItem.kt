package com.ndup.berealtechnicaltest.domain

import kotlinx.serialization.Serializable

@Serializable
data class Item(
    val id: String,
    val parentId: String,
    val name: String,
    val isDir: Boolean,
    val modificationDate: String,
    val size: Long? = null,
    val contentType: String? = null,
)

typealias Items = List<Item>