package com.sedra.goiptv.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Movie(
        val added: String,
        val category_id: String,
        val direct_source: String,
        val name: String,
        val num: Int,
        val rating: String,
        val rating_5based: Double,
        val stream_icon: String,
        val stream_id: Int,
        val stream_type: String
) : Parcelable