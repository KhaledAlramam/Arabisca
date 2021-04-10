package com.sedra.goiptv.view.starting

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sedra.goiptv.R
import com.sedra.goiptv.data.model.AppSetting
import com.sedra.goiptv.data.model.UserInfo
import com.sedra.goiptv.utils.*
import com.sedra.goiptv.utils.PREF_PORT
import com.sedra.goiptv.view.login.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash) {


    @Inject
    lateinit var user: UserInfo
    @Inject
    lateinit var preferences: SharedPreferences
    private val viewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startAnimation(view)
        getSettings()

    }

    private fun getSettings() {
        viewModel.getSetting().observe(viewLifecycleOwner) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        if (resource.data == null) {
                            Toast.makeText(context, resource.message, Toast.LENGTH_SHORT).show()
                        } else {
                            saveSetting(resource.data.data)
                            performNav()
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

    private fun saveSetting(appSetting: AppSetting) {
        val editor = preferences.edit()
        editor.putString(PREF_APP_IMG, appSetting.image)
        editor.putString(PREF_BANNER, appSetting.long_bar)
        editor.apply()
    }

    private fun performNav() {
        if (user.username.isNullOrEmpty()) {
            findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
        } else {
            GoTo.goToMainActivity(requireActivity())
        }
    }

    private fun startAnimation(view: View) {
        val animation = AnimationUtils.loadAnimation(context, R.anim.spinner_rotation)
        view.findViewById<ImageView>(R.id.spinnerImg).startAnimation(animation)
    }
}