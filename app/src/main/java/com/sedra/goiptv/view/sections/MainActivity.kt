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
import com.jakewharton.processphoenix.ProcessPhoenix
import com.rommansabbir.networkx.core.NetworkXCore
import com.rommansabbir.networkx.dialog.NoInternetDialog
import com.sedra.goiptv.R
import com.sedra.goiptv.data.model.Section
import com.sedra.goiptv.data.model.UserInfo
import com.sedra.goiptv.databinding.ActivityMainBinding
import com.sedra.goiptv.utils.*
import com.sedra.goiptv.utils.Status.*
import dagger.hilt.android.AndroidEntryPoint
import dmax.dialog.SpotsDialog
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var preferences: SharedPreferences
    private val viewModel by viewModels<MainViewModel>()
    var progressDialog: AlertDialog? = null

    @Inject
    lateinit var userInfo: UserInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        NetworkXCore.getNetworkX().isInternetConnectedLiveData().observe(this) {
            when (it) {
                true -> {
                    /**
                     * Do your stuff here when internet is connected
                     */
                }
                else -> {
                    NoInternetDialog
                        .Companion
                        .Builder()
                        // Provide activity reference
                        .withActivity(this)
                        // Provide custom title
                        .withTitle("No internet!")
                        // Provide custom mesage
                        .withMessage("Your device is not connected to the internet!")
                        // Register for callback
                        .withActionCallback {
                            // User clicked `Retry` button
                        }
                        .build()
                        .show()

                }
            }
        }


        progressDialog = SpotsDialog.Builder()
            .setContext(this)
            .setMessage("Please Wait...")
            .setCancelable(false)
            .setTheme(R.style.CustomProgressDialogTheme)
            .build()
        binding.apply {
            restartApp.setOnClickListener {
                ProcessPhoenix.triggerRebirth(this@MainActivity)
            }
            userName = userInfo.username ?: "User"
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
            goToSetting.setOnClickListener {
                GoTo.goToSettings(this@MainActivity)
            }
            goToSetting.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    goToSetting.setColorFilter(
                            ContextCompat.getColor(
                                    this@MainActivity,
                                    R.color.mainDark
                            ), android.graphics.PorterDuff.Mode.MULTIPLY
                    )
                } else {
                    goToSetting.setColorFilter(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.white
                        ), android.graphics.PorterDuff.Mode.MULTIPLY
                    )
                }
            }

            restartApp.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    restartApp.setColorFilter(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.mainDark
                        ), android.graphics.PorterDuff.Mode.MULTIPLY
                    )
                } else {
                    restartApp.setColorFilter(
                        ContextCompat.getColor(
                            this@MainActivity,
                            R.color.white
                        ), android.graphics.PorterDuff.Mode.MULTIPLY
                    )
                }
            }

        }
        getSections()
        try {
            binding.imageView7.text =
                "Expire On: \n${getFormattedExpiryDate(userInfo.exp_date?.toLong())}"
        } catch (e: Exception) {
        }
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
            Section(-5, "", getString(R.string.catch_up), resourceId = R.drawable.time_small),
            Section(-6, "", "Favourites", resourceId = R.drawable.time_small),
        ) + sections.reversed()
//        + Section(-4, "", getString(R.string.setting))

        val sectionsAdapter = SectionsAdapter(preferences, fixedList)
        binding.sectionsRv.apply {
            adapter = sectionsAdapter
            layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            scrollToPosition(0)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        NetworkXCore.getNetworkX().cancelObservation()
    }


}