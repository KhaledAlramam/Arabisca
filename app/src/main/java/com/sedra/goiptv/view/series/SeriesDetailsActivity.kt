package com.sedra.goiptv.view.series

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sedra.goiptv.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SeriesDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_series_details)
    }
}