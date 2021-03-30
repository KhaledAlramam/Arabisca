package com.sedra.goiptv.utils

import android.view.View
import com.sedra.goiptv.data.model.series_models.Season

interface SeasonOnClick {
    fun onClick(view: View, season: Season)
}