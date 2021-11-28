package com.sedra.goiptv.utils

import android.view.View
import com.sedra.goiptv.data.db.FavouriteItem

interface FavouriteItemOnClick {
    fun onClick(item: FavouriteItem, view: View)
}