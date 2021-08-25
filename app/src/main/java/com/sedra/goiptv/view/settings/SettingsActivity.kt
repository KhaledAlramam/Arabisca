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
    lateinit var manager: DownloadManager
    @Inject
    lateinit var preferences: SharedPreferences
    lateinit var downloadController: DownloadController
    @Inject
    lateinit var userInfo: UserInfo
    var progressDialog: AlertDialog? = null
    companion object {
        const val PERMISSION_REQUEST_STORAGE = 0
    }
//    private val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent) {
//            //Fetching the download id received with the broadcast
//            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
//            //Checking if the received broadcast is for our enqueued download by matching download id
////            DownloadManager.Query() is used to filter DownloadManager queries
//            val query = DownloadManager.Query()
//
//            query.setFilterById(id)
//
//            val cursor = manager.query(query)
//
//            if (cursor.moveToFirst()) {
//                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
//
//
//                when (status) {
//                    DownloadManager.STATUS_SUCCESSFUL -> {
//                        Log.e(TAG, "onReceive: succ")
//
//                    }
//                    DownloadManager.STATUS_FAILED -> {
//                        Log.e(
//                            TAG,
//                            "onReceive: ${cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON))}"
//                        )
//                    }
//
//                }
//                if (downloadID == id) {
//                    Toast.makeText(this@SettingsActivity, "Download Completed", Toast.LENGTH_SHORT)
//                        .show()
//                }
//            }
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
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
        getVersions()
    }

    private fun getVersions() {
        viewModel.getVersions().observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        val apkUrl = "http://mahmoude28.sg-host.com/go/public/uploads/apks/1629813593Go.apk"
                        downloadController = DownloadController(this, apkUrl)
                        progressDialog?.dismiss()
                        resource.data?.let { res ->
                            binding.showAllVersions.setOnClickListener {
                                showVersionPicker(res.items)
                            }
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_STORAGE) {
            // Request for camera permission.
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // start downloading
                downloadController.enqueueDownload()
            } else {
                // Permission request was denied.
//                mainLayout.showSnackbar(R.string.storage_permission_denied, Snackbar.LENGTH_SHORT)
            }
        }
    }
    private fun checkStoragePermission() {
        // Check if the storage permission has been granted
        if (checkSelfPermissionCompat(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            // start downloading
            downloadController.enqueueDownload()
        } else {
            // Permission is missing and must be requested.
            requestStoragePermission()
        }
    }
    private fun requestStoragePermission() {
        if (shouldShowRequestPermissionRationaleCompat(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            binding.root.showSnackbar(
                getString(R.string.storage_access_required),
                Snackbar.LENGTH_INDEFINITE, "Ok"
            ) {
                requestPermissionsCompat(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_STORAGE
                )
            }
        } else {
            requestPermissionsCompat(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_STORAGE
            )
        }
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String?>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) Toast.makeText(
//            this,
//            "Permission granted",
//            Toast.LENGTH_LONG
//        ).show()
//    }
//
//    private fun requestPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) requestPermissions(
//            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
//            REQUEST_WRITE_PERMISSION
//        )
//    }
//
//    private fun canReadWriteExternal(): Boolean {
//        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
//                ContextCompat.checkSelfPermission(
//                    this,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE
//                ) != PackageManager.PERMISSION_GRANTED
//    }

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


    private fun showVersionPicker(versions: List<Version>) {
        val myDialog = Dialog(this)
        myDialog.apply {
            setContentView(R.layout.dialog_select_server)
            setCancelable(true)
            setCanceledOnTouchOutside(true)
        }
        val accountsRv = myDialog.findViewById<RecyclerView>(R.id.accountRv)
        val accountsAdapter = VersionAdapter(versions, object : PositionOnClick {
            override fun onClick(view: View, position: Int) {
                myDialog.dismiss()
                downloadController.url =versions[position].link
                checkStoragePermission()

            }
        })
        accountsRv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@SettingsActivity)
            adapter = accountsAdapter
        }
        myDialog.show()
        val window = myDialog.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
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

    override fun onDestroy() {
        super.onDestroy()
    }
}