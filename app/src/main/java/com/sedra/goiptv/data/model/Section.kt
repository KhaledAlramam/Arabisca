package com.sedra.goiptv.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Section(
        val id: Int,
        val image: String,
        val name: String,
        val type: String? = ""
): Parcelable