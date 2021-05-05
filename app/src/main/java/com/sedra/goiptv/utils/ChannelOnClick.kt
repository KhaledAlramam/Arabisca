package com.sedra.goiptv.utils

import android.view.View
import com.sedra.goiptv.data.model.LiveStream

interface ChannelOnClick {
    fun onClick(view: View, clicked: Boolean, liveStream: LiveStream, position: Int)
}