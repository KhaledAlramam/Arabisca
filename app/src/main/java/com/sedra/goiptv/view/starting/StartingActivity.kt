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

//    fun comp(a: IntArray?, b: IntArray?): Boolean {
//        //your code here
//        if (a ==null || b == null) return false
//        a.forEach { first ->
//            if (!b.contains(first * first)){
//                return false
//            }
//            b.red
//        }
//        return true
//    }
}