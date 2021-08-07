package com.sedra.goiptv.data.model

import com.squareup.moshi.Json

data class VersionsResponse(
    val status: Int,
    val message: String,
    @field:Json(name = "data")
    val items: List<Version>
)