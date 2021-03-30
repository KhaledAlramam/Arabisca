package com.sedra.goiptv.view.login

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.sedra.goiptv.R
import com.sedra.goiptv.data.model.LoginResponse
import com.sedra.goiptv.utils.*
import com.sedra.goiptv.utils.Status.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    val viewModel: AuthViewModel by viewModels()
    @Inject
    lateinit var preferences: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val loginButton: Button= view.findViewById(R.id.loginButton)
        val codeEt: EditText= view.findViewById(R.id.codeEt)
        loginButton.setOnClickListener {
            val code = codeEt.text.toString()
            login(code)
        }
    }

    private fun login(code: String) {
        viewModel.login(code).observe(viewLifecycleOwner){
            it?.let { resource ->
                when(resource.status){
                    SUCCESS -> {
                        if (resource.data?.server_info == null) {
                            Toast.makeText(context, "Login Error", Toast.LENGTH_SHORT).show()
                        }else{
                            saveLogin(resource.data)
                            findNavController().navigate(R.id.action_loginFragment_to_nameFragment)
                        }
                    }
                    ERROR -> {
                        Toast.makeText(context, resource.message, Toast.LENGTH_SHORT).show()
                    }
                    LOADING -> {}
                }
            }
        }
    }

    private fun saveLogin(data: LoginResponse) {
        val gson = Gson()
        val hashMapString = gson.toJson(data.user_info)
        val editor = preferences.edit()
        editor.putString(PREF_URL, data.server_info?.url)
        editor.putString(PREF_PORT, data.server_info?.port)
        editor.putString(PREF_PARENT_USER, hashMapString)
        editor.apply()
    }


}