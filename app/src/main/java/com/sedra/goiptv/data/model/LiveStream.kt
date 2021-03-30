package com.sedra.goiptv.data.model

import com.squareup.moshi.Json
import java.io.Serializable


data class LiveStream(
    @field:Json(name = "num")
    val num: Int? = null,

    @field:Json(name = "name")
    val name: String? = null,

    @field:Json(name = "stream_type")
    val streamType: String? = null,

    @field:Json(name = "stream_id")
    var streamId: Int? = null,

    @field:Json(name = "stream_icon")
    val streamIcon: String? = null,

    @field:Json(name = "epg_channel_id")
    val epgChannelId: String? = null,

    @field:Json(name = "added")
    val added: String? = null,

    @field:Json(name = "is_adult")
    val isAdult: String? = null,

    @field:Json(name = "category_id")
    val categoryId: String? = null,

    @field:Json(name = "custom_sid")
    val customSid: String? = null,

    @field:Json(name = "tv_archive")
    val tvArchive: Int? = null,

    @field:Json(name = "direct_source")
    val directSource: String? = null,

    @field:Json(name = "tv_archive_duration")
    val tvArchiveDuration: Int? = null
) : Serializable