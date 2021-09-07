package com.sedra.goiptv

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sedra.goiptv.utils.GoTo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UpdateContentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_content)
        CoroutineScope(Dispatchers.Main).launch {
            delay(3000)
            GoTo.goToMainActivity(this@UpdateContentActivity)
        }
    }

}