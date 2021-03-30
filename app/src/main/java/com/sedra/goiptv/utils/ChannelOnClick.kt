package com.sedra.goiptv.utils

import android.view.View
import com.sedra.goiptv.data.model.LiveStream
import com.sedra.goiptv.data.model.series_models.Episode

interface ChannelOnClick {
    fun onClick(view: View, liveStream: LiveStream)
}