package com.sedra.goiptv.data.model

data class MovieData(
    val added: String,
    val category_id: String,
    val container_extension: String?,
    val name: String,
    val stream_id: Int
)