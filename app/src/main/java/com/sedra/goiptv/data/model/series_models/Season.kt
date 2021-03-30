package com.sedra.goiptv.data.model.series_models

import com.squareup.moshi.Json
import java.io.Serializable


class Season : Serializable {
    @field:Json(name = "air_date")
    var airDate: String? = null

    @field:Json(name = "episode_count")
    var episodeCount: Int? = null

    @field:Json(name = "id")
    var id: Int? = null

    @field:Json(name = "name")
    var name: String? = null

    @field:Json(name = "overview")
    var overview: String? = null

    @field:Json(name = "season_number")
    var seasonNumber: Int? = null

    @field:Json(name = "cover")
    var cover: String? = null

    @field:Json(name = "cover_big")
    var coverBig: String? = null
}