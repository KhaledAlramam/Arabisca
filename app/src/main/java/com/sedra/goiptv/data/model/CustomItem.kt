package com.sedra.goiptv.data.model

data class CustomItem(
    val created_at: String,
    val id: Int,
    val image: String,
    val name: String,
    val section_id: Int,
    val subsection_id: Int,
    val updated_at: String,
    val video: String
)