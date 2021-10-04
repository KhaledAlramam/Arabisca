package com.sedra.goiptv

import android.app.Application
import com.rommansabbir.networkx.core.NetworkXCore
import com.rommansabbir.networkx.strategy.NetworkXObservingStrategy
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GoApp: Application() {


    override fun onCreate() {
        super.onCreate()
        NetworkXCore.init(this, NetworkXObservingStrategy.MEDIUM)
    }
}