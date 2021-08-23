package com.sedra.goiptv.view.radio

import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.sedra.goiptv.R
import com.sedra.goiptv.utils.getRadioStations


class RadioActivity : AppCompatActivity() {

    val adapter = RadioAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_radio)
        adapter.submitList(getRadioStations())
        val rv = findViewById<RecyclerView>(R.id.radioRv)
        rv.adapter = adapter
        rv.setHasFixedSize(true)
    }

    override fun onDestroy() {
        adapter.onDestroy()
        super.onDestroy()
    }
}