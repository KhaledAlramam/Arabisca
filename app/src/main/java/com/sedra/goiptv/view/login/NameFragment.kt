package com.sedra.goiptv.view.login

import android.app.AlertDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
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
    var progressDialog: AlertDialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
        login()
//        val saveName = view.findViewById<Button>(R.id.saveName)
//        val nameEt = view.findViewById<EditText>(R.id.nameEt)
//        progressDialog = SpotsDialog.Builder()
//                .setContext(requireContext())
//                .setMessage("Please Wait...")
//                .setCancelable(false)
//                .setTheme(R.style.CustomProgressDialogTheme)
//                .build()
//
//        saveName.setOnClickListener {
//            val name = nameEt.text.toString()
//            if (name.isEmpty()) return@setOnClickListener
//            val editor = preferences.edit()
//            editor.putString(PREF_NAME, name)
//            editor.apply()
//            login()
//        }
    }


    private fun login() {
        viewModel.login(
                preferences.getString(PREF_USER_NAME, "") ?: "",
                preferences.getString(PREF_PASSWORD, "") ?: ""
        ).observe(viewLifecycleOwner) {
            it?.let {
                when (it.status) {
                    SUCCESS -> {
                        progressDialog?.dismiss()
                        it.data?.let { data ->
                            if (data.server_info == null) {
                                Toast.makeText(
                                        context,
                                        "الحساب خاطئ او غير مفعل",
                                        Toast.LENGTH_SHORT
                                ).show()
                                requireActivity().onBackPressed()
                            } else {
                                saveUser(data.user_info!!)
                            }
                        }
                    }
                    ERROR -> {
                        progressDialog?.dismiss()
                        Log.e("TAG", "onViewCreated: ,,,${it.message}")
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    }
                    LOADING -> {
                        progressDialog?.show()
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
        editor.putString(PREF_NAME, "User")
        editor.apply()
        GoTo.goToUpdateContent(requireActivity())
    }

    override fun onDestroy() {
        progressDialog= null
        super.onDestroy()
    }
}