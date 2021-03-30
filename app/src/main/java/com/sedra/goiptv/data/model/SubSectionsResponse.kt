package com.sedra.goiptv.data.model

data class SubSectionsResponse(
        val data: List<SubSection>,
        val message: String,
        val status: Int
)