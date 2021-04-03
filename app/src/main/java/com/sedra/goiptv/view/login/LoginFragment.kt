package com.sedra.goiptv.view.login

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.sedra.goiptv.R
import com.sedra.goiptv.data.model.LoginResponse
import com.sedra.goiptv.utils.*
import com.sedra.goiptv.utils.Status.*
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.internal.and
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    val viewModel: AuthViewModel by viewModels()
    @Inject
    lateinit var preferences: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val loginButton: Button = view.findViewById(R.id.loginButton)
        val codeEt: EditText = view.findViewById(R.id.codeEt)
        loginButton.setOnClickListener {
            val code = codeEt.text.toString()
            login(code)
        }
        val macAdd = getMacAddress() //call the method that return mac address

        Toast.makeText(requireContext(), macAdd, Toast.LENGTH_SHORT).show()
    }

    private fun login(code: String) {
        viewModel.login(code).observe(viewLifecycleOwner){
            it?.let { resource ->
                when(resource.status){
                    SUCCESS -> {
                        if (resource.data?.server_info == null) {
                            Toast.makeText(context, "Login Error", Toast.LENGTH_SHORT).show()
                        } else {
                            saveLogin(resource.data)
                            findNavController().navigate(R.id.action_loginFragment_to_nameFragment)
                        }
                    }
                    ERROR -> {
                        Toast.makeText(context, resource.message, Toast.LENGTH_SHORT).show()
                    }
                    LOADING -> {
                    }
                }
            }
        }
    }

    private fun saveLogin(data: LoginResponse) {
        val gson = Gson()
        val hashMapString = gson.toJson(data.user_info)
        val editor = preferences.edit()
        editor.putString(PREF_URL, data.server_info.url)
        editor.putString(PREF_PORT, data.server_info.port)
        editor.putString(PREF_PARENT_USER, hashMapString)
        editor.apply()
    }

    fun getMacAddress(): String {
        try {
            val networkInterfaceList: List<NetworkInterface> = Collections.list(NetworkInterface.getNetworkInterfaces())
            var stringMac = ""
            for (networkInterface in networkInterfaceList) {
                if (networkInterface.name.equals("wlon0", true));
                run {
                    for (i in networkInterface.hardwareAddress.indices) {
                        var stringMacByte = Integer.toHexString(networkInterface.hardwareAddress[i].and(0xFF))
                        if (stringMacByte.length == 1) {
                            stringMacByte = "0$stringMacByte"
                        }
                        stringMac = stringMac + stringMacByte.toUpperCase() + ":"
                    }
                }
                break
            }
            return stringMac
        } catch (e: SocketException) {
            e.printStackTrace()
        }
        return "0"
    }

}