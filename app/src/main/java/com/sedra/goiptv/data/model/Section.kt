package com.sedra.goiptv.data.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class Section(
        val id: Int,
        val image: String,
        val name: String,
        val type: String? = "",
        @DrawableRes val resourceId: Int? = null
): Parcelable