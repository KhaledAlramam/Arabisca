package com.sedra.goiptv.data.model.series_models

import com.squareup.moshi.Json


class SeriesDetailsResponse(
    @field:Json(name = "seasons")
    var seasons: List<Season>? = null,

    @field:Json(name = "info")
    var info: SeriesInfo? = null,

    @field:Json(name = "episodes")
    var episodes: Map<String, List<Episode>>? = null
)