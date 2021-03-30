package com.sedra.goiptv.data.model.series_models

import com.squareup.moshi.Json
import java.io.Serializable


data class Episode(
    @field:Json(name = "id")
    var id: String? = null,

    @field:Json(name = "episode_num")
    var episodeNum: Int? = null,

    @field:Json(name = "title")
    var title: String? = null,

    @field:Json(name = "container_extension")
    var containerExtension: String? = null,


    @field:Json(name = "custom_sid")
    var customSid: String? = null,

    @field:Json(name = "added")
    var added: String? = null,

    @field:Json(name = "season")
    var season: Int? = null,

    @field:Json(name = "direct_source")
    var directSource: String? = null
) : Serializable