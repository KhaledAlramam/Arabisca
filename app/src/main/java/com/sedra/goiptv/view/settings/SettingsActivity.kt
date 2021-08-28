package com.sedra.goiptv.view.settings

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.app.DownloadManager
import android.content.*
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sedra.goiptv.R
import com.sedra.goiptv.data.model.Section
import com.sedra.goiptv.data.model.UserInfo
import com.sedra.goiptv.data.model.Version
import com.sedra.goiptv.databinding.ActivitySettingsBinding
import com.sedra.goiptv.utils.*
import com.sedra.goiptv.view.starting.StartingActivity
import dagger.hilt.android.AndroidEntryPoint
import dmax.dialog.SpotsDialog
import java.io.File
import java.util.*
import javax.inject.Inject

import android.content.Intent

import android.content.BroadcastReceiver
import android.os.Build
import androidx.core.content.FileProvider
import com.sedra.goiptv.BuildConfig
import android.content.pm.PackageManager

import com.sedra.goiptv.view.sections.MainActivity

import androidx.core.content.ContextCompat

import androidx.annotation.NonNull
import com.google.android.material.snackbar.Snackbar


private const val TAG = "SettingsActivity"
@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    lateinit var binding: ActivitySettingsBinding
    val viewModel: SettingsViewModel by viewModels()
    @Inject
    lateinit var preferences: SharedPreferences
    @Inject
    lateinit var userInfo: UserInfo
    var progressDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        progressDialog = SpotsDialog.Builder()
            .setContext(this)
            .setMessage("Please Wait...")
            .setCancelable(false)
            .setTheme(R.style.CustomProgressDialogTheme)
            .build()
        binding.apply {
            code = preferences.getString(PREF_CODE, "Error")
            mac = preferences.getString(PREF_MAC, "Error")
            try {
                expiry = getFormattedExpiryDate(userInfo.exp_date?.toLong())
            } catch (e: Exception) {
            }
            logout.setOnClickListener {
                preferences.edit().clear().apply()
                val intent = Intent(this@SettingsActivity, StartingActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        getSections()
    }


    private fun getSections() {
        viewModel.getSections().observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        progressDialog?.dismiss()
                        resource.data?.let { data ->
                            showSections(data.data)
                        }
                    }
                    Status.ERROR -> {
                        progressDialog?.dismiss()
                        Log.e("TAG", "getSections: ${resource.message}")
                    }
                    Status.LOADING -> {
                        progressDialog?.show()
                    }
                }
            }
        }
    }

    private fun showSections(sections: List<Section>) {
        val fixedList = listOf(
            Section(
                -3,
                "https://www.logomoose.com/wp-content/uploads/2016/01/GoMovies.jpg",
                getString(R.string.channels)
            ),
            Section(
                -1,
                "https://www.logomoose.com/wp-content/uploads/2016/01/GoMovies.jpg",
                getString(R.string.movies)
            ),
            Section(
                -2,
                "https://www.logomoose.com/wp-content/uploads/2016/01/GoMovies.jpg",
                getString(R.string.series)
            ),
        ) + sections
        val sectionsAdapter = SettingsSectionsAdapter(preferences, fixedList)
        binding.settingsSectionsRv.apply {
            adapter = sectionsAdapter
            layoutManager = LinearLayoutManager(this@SettingsActivity)
            setHasFixedSize(true)
        }

    }

    fun getFormattedExpiryDate(date: Long?): String {
        if (date == null) return ""
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = (1000L * date)
        return "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${
            calendar.get(
                Calendar.YEAR
            )
        }"
    }

}