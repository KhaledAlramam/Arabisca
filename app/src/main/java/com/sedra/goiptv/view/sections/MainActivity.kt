package com.sedra.goiptv.view.sections

import android.app.AlertDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.sedra.goiptv.R
import com.sedra.goiptv.data.model.Section
import com.sedra.goiptv.databinding.ActivityMainBinding
import com.sedra.goiptv.utils.*
import com.sedra.goiptv.utils.Status.*
import dagger.hilt.android.AndroidEntryPoint
import dmax.dialog.SpotsDialog
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    var binding: ActivityMainBinding? = null

    @Inject
    lateinit var preferences: SharedPreferences
    private val viewModel by viewModels<MainViewModel>()
    var progressDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        progressDialog = SpotsDialog.Builder()
            .setContext(this)
            .setMessage("Please Wait...")
            .setCancelable(false)
            .setTheme(R.style.CustomProgressDialogTheme)
            .build()
        binding?.apply {
            userName = preferences.getString(PREF_NAME, "User")
            val txt = "${
                preferences.getString(
                    PREF_BANNER,
                    "No Text"
                )
            }                              ${
                preferences.getString(
                    PREF_BANNER,
                    "No Text"
                )
            }                              ${
                preferences.getString(
                    PREF_BANNER,
                    "No Text"
                )
            }"
            val imageLink = preferences.getString(
                PREF_APP_IMG,
                ""
            )
            Glide.with(this@MainActivity)
                .load(imageLink)
                .into(imageView11)
            textView3.text = txt
            textView3.isSelected = true
        }
        getSections()
    }

    private fun getSections() {
        viewModel.getSections().observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    SUCCESS -> {
                        progressDialog?.dismiss()
                        resource.data?.let { data ->
                            showSections(data.data)
                        }
                    }
                    ERROR -> {
                        progressDialog?.dismiss()
                        Log.e("TAG", "getSections: ${resource.message}")
                    }
                    LOADING -> {
                        progressDialog?.show()
                    }
                }
            }
        }
    }

    private fun showSections(sections: List<Section>) {
        val fixedList = listOf(
            Section(-3, "", getString(R.string.channels), resourceId = R.drawable.go_live),
            Section(-1, "", getString(R.string.movies), resourceId = R.drawable.go_movies),
            Section(-2, "", getString(R.string.series), resourceId = R.drawable.go_series),
            Section(-5, "", getString(R.string.catch_up), resourceId = R.drawable.time_small)
        )+ sections + Section(-4, "", getString(R.string.setting))

        val sectionsAdapter = SectionsAdapter(preferences, fixedList)
        binding!!.sectionsRv.apply {
            adapter = sectionsAdapter
            layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }


}