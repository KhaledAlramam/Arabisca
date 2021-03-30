package com.sedra.goiptv.utils

import android.view.View
import com.sedra.goiptv.data.model.Category

interface CategoryOnClick {
    fun onClick(view: View, category: Category)
}