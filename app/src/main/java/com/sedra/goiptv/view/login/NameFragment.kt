package com.sedra.goiptv.view.login

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.google.gson.Gson
import com.sedra.goiptv.R
import com.sedra.goiptv.data.model.UserInfo
import com.sedra.goiptv.utils.*
import com.sedra.goiptv.utils.Status.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NameFragment : Fragment(R.layout.fragment_name) {

    @Inject
    lateinit var preferences: SharedPreferences
    val viewModel: LoginDataViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val saveName = view.findViewById<Button>(R.id.saveName)
        val nameEt = view.findViewById<EditText>(R.id.nameEt)
        saveName.setOnClickListener {
            val name = nameEt.text.toString()
            if (name.isEmpty()) return@setOnClickListener
            val editor = preferences.edit()
            editor.putString(PREF_NAME, name)
            editor.apply()
            login()
        }
    }


    private fun login() {
        viewModel.login(
                preferences.getString(PREF_USER_NAME, "") ?: "",
                preferences.getString(PREF_PASSWORD, "") ?: ""
        ).observe(viewLifecycleOwner) {
            it?.let {
                when (it.status) {
                    SUCCESS -> {
                        it.data?.let { data ->
                            if (data.user_info == null) {
                                Toast.makeText(
                                        context,
                                        "اسم المستخدم أو كلمة السر خاطئة",
                                        Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                saveUser(data.user_info)
                            }
                        }
                    }
                    ERROR -> {
                        Log.e("TAG", "onViewCreated: ,,,${it.message}")
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    }
                    LOADING -> {
                    }
                }
            }
        }
    }

    private fun saveUser(userInfo: UserInfo) {
        val gson = Gson()
        val hashMapString = gson.toJson(userInfo)
        val editor = preferences.edit()
        editor.putString(PREF_PARENT_USER, hashMapString)
        editor.apply()
        GoTo.goToMainActivity(requireActivity())
    }

}