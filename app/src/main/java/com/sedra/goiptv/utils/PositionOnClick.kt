package com.sedra.goiptv.utils

import android.view.View
import com.sedra.goiptv.data.model.Category

interface PositionOnClick {
    fun onClick(view: View, position: Int)
}