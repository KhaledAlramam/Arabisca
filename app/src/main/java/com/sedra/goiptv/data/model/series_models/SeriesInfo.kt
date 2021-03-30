package com.sedra.goiptv.data.model.series_models

data class SeriesInfo(
    val cast: String,
    val category_id: String,
    val cover: String,
    val director: String,
    val genre: String,
    val last_modified: String,
    val name: String,
    val plot: String,
    val rating: String,
    val rating_5based: Float,
    val releaseDate: String
)