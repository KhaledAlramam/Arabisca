package com.sedra.goiptv.view.starting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sedra.goiptv.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StartingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_starting)
    }
}