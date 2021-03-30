package com.sedra.goiptv.view.starting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.sedra.goiptv.R
import com.sedra.goiptv.data.model.UserInfo
import com.sedra.goiptv.utils.GoTo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash) {


    @Inject
    lateinit var user: UserInfo

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startAnimation(view)
        lifecycleScope.launch {
            delay(2000)
            if (user.username.isNullOrEmpty()){
                findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
            }else{
                GoTo.goToMainActivity(requireActivity())
            }
        }
    }

    private fun startAnimation(view: View) {
        val animation = AnimationUtils.loadAnimation(context, R.anim.spinner_rotation)
        view.findViewById<ImageView>(R.id.spinnerImg).startAnimation(animation)
    }
}