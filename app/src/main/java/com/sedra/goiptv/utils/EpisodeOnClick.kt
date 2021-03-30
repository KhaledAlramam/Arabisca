package com.sedra.goiptv.utils

import android.view.View
import com.sedra.goiptv.data.model.series_models.Episode

interface EpisodeOnClick {
    fun onClick(view: View, episode: Episode)
}