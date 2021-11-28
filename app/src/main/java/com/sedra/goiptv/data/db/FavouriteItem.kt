package com.sedra.goiptv.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "arabisca_fav")
data class FavouriteItem(
    @PrimaryKey
    val id: Int,
    val title: String,
    val imageUrl: String,
    val type: Int
)