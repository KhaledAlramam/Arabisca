package com.sedra.goiptv.view.starting

import android.Manifest
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sedra.goiptv.R
import com.sedra.goiptv.data.model.AppSetting
import com.sedra.goiptv.utils.*
import com.sedra.goiptv.view.login.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity

import com.google.android.material.snackbar.Snackbar
import com.sedra.goiptv.BuildConfig
import com.sedra.goiptv.data.model.LatestApkDetails
import com.sedra.goiptv.databinding.FragmentSplashBinding


@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash) {


    var binding: FragmentSplashBinding? =null
    @Inject
    lateinit var preferences: SharedPreferences
    private val viewModel: AuthViewModel by viewModels()
    companion object {
        const val PERMISSION_REQUEST_STORAGE = 0
    }
    lateinit var downloadController: DownloadController


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSplashBinding.bind(view)
        startAnimation(view)
        getSettings()

    }

    private fun getSettings() {
        val apkUrl = "http://mahmoude28.sg-host.com/go/public/uploads/apks/1629813593Go.apk"
        downloadController = DownloadController(requireContext(), apkUrl)
        viewModel.getSetting().observe(viewLifecycleOwner) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        if (resource.data == null) {
                            Toast.makeText(context, resource.message, Toast.LENGTH_SHORT).show()
                        } else {
                            saveSetting(resource.data.data)
                            checkForUpdateOrNavAway(resource.data.data.apk)
                        }
                    }
                    Status.ERROR -> {
                        Toast.makeText(context, resource.message, Toast.LENGTH_SHORT).show()
                    }
                    Status.LOADING -> {
                    }
                }
            }
        }
    }

    private fun checkForUpdateOrNavAway(apkDetails: LatestApkDetails) {
        val versionCode: Int = BuildConfig.VERSION_CODE
        if (apkDetails.version> versionCode){
            downloadController.url = apkDetails.link
            checkStoragePermission()
            Toast.makeText(
                context,
                "You version is old, We are downloading out latest version",
                Toast.LENGTH_SHORT
            ).show()
        }else{
            performNav()
        }

    }

    private fun saveSetting(appSetting: AppSetting) {
        val editor = preferences.edit()
        editor.putString(PREF_APP_IMG, appSetting.setting.image)
        editor.putString(PREF_BANNER, appSetting.setting.long_bar)
        editor.apply()
    }

    private fun performNav() {
        if (preferences.getString(PREF_NAME,"").isNullOrEmpty()) {
            findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
        } else {
            GoTo.goToMainActivity(requireActivity())
        }
    }

    private fun startAnimation(view: View) {
        val animation = AnimationUtils.loadAnimation(context, R.anim.spinner_rotation)
        view.findViewById<ImageView>(R.id.spinnerImg).startAnimation(animation)
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
                performNav()
            } else {
                // Permission request was denied.
                performNav()
                binding?.root?.showSnackbar(R.string.storage_permission_denied, Snackbar.LENGTH_SHORT)
            }
        }
    }
    private fun checkStoragePermission() {
        // Check if the storage permission has been granted
        if ((activity as AppCompatActivity).checkSelfPermissionCompat(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            // start downloading
            downloadController.enqueueDownload()
            performNav()
        } else {
            // Permission is missing and must be requested.
            requestStoragePermission()
        }
    }
    private fun requestStoragePermission() {
        if ((activity as AppCompatActivity).shouldShowRequestPermissionRationaleCompat(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            binding?.root?.showSnackbar(
                getString(R.string.storage_access_required),
                Snackbar.LENGTH_INDEFINITE, "Ok"
            ) {
                (activity as AppCompatActivity).requestPermissionsCompat(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_STORAGE
                )
            }
        } else {
            (activity as AppCompatActivity).requestPermissionsCompat(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_STORAGE
            )
        }
    }
}