package com.sedra.goiptv.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Series(
        val cast: String,
        val category_id: String,
        val cover: String,
        val director: String,
//        val episode_run_time: String,
        val genre: String,
        val last_modified: String,
        val name: String,
        val num: Int,
        val plot: String,
        val rating: String,
        val rating_5based: Double,
        val releaseDate: String,
        val series_id: Int,
//        val youtube_trailer: String
): Parcelable