package com.sedra.goiptv.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class Category(
        val category_id: String,
        val category_name: String,
        val parent_id: Int,
        var movies: List<Movie>? = null,
        var series: List<Series>? = null,
        var channels: List<LiveStream>? = null
) : Parcelable