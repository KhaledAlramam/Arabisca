package com.sedra.goiptv.view.login

import android.app.AlertDialog
import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sedra.goiptv.R
import com.sedra.goiptv.data.model.Account
import com.sedra.goiptv.utils.*
import com.sedra.goiptv.utils.Status.*
import dagger.hilt.android.AndroidEntryPoint
import dmax.dialog.SpotsDialog
import okhttp3.internal.and
import java.net.NetworkInterface
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    val viewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var preferences: SharedPreferences
    var progressDialog: AlertDialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val loginButton: Button = view.findViewById(R.id.loginButton)
        val codeEt: EditText = view.findViewById(R.id.codeEt)
        val macAddressTv: TextView = view.findViewById(R.id.macAddressTv)
        val macAdd = getMacAddress()
        macAddressTv.text = getMacAddress()
        progressDialog = SpotsDialog.Builder()
            .setContext(requireContext())
            .setMessage("Please Wait...")
            .setCancelable(false)
            .setTheme(R.style.CustomProgressDialogTheme)
            .build()

        loginButton.setOnClickListener {
            val code = codeEt.text.toString()
            getAccounts(code, macAdd)
        }
    }

    private fun getAccounts(code: String, macAdd: String) {
        viewModel.getAccounts(code, macAdd).observe(viewLifecycleOwner) {
            it?.let { resource ->
                when (resource.status) {
                    SUCCESS -> {
                        progressDialog?.dismiss()
                        if (resource.data == null) {
                        } else {
                            if (resource.data.data == null) {
                                Toast.makeText(context, resource.data.message, Toast.LENGTH_SHORT)
                                    .show()

                            } else {
                                val editor = preferences.edit()
                                editor.putString(PREF_CODE, code)
                                editor.putString(PREF_MAC, macAdd)
                                editor.apply()
                                if (resource.data.data.size > 1) {
                                    showAccountPicker(resource.data.data)
                                } else {
                                    saveLogin(resource.data.data[0])
                                }

                            }
                        }
                    }
                    ERROR -> {
                        progressDialog?.dismiss()
                        Toast.makeText(context, resource.message, Toast.LENGTH_SHORT).show()
                    }
                    LOADING -> {
                        progressDialog?.show()
                    }
                }
            }
        }
    }

    private fun showAccountPicker(accounts: List<Account>) {
        val myDialog = Dialog(requireContext())
        myDialog.apply {
            setContentView(R.layout.dialog_select_server)
            setCancelable(true)
            setCanceledOnTouchOutside(true)
        }
        val accountsRv = myDialog.findViewById<RecyclerView>(R.id.accountRv)
        val accountsAdapter = AccountsAdapter(accounts, object : PositionOnClick {
            override fun onClick(view: View, position: Int) {
                myDialog.dismiss()
                saveLogin(accounts[position])
            }
        })
        accountsRv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = accountsAdapter
        }
        myDialog.show()
        val window = myDialog.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    private fun saveLogin(account: Account) {
        val editor = preferences.edit()
        val wholeUrl = account.server.let {
            it.replace("http://", "")
        }
        val link = wholeUrl.split(":")[0]
        val port = wholeUrl.split(":")[1].split("/")[0]
        editor.putString(PREF_URL, link)
        editor.putString(PREF_PORT, port)
        editor.putInt(PREF_ACCOUNT_ID, account.id)
        editor.putString(PREF_USER_NAME, account.username)
        editor.putString(PREF_PASSWORD, account.password)
        editor.apply()
        findNavController().navigate(R.id.action_loginFragment_to_nameFragment)
    }

    fun getMacAddress(): String {
        try {
            val networkInterfaceList: List<NetworkInterface> =
                Collections.list(NetworkInterface.getNetworkInterfaces())
            var stringMac = ""
            for (networkInterface in networkInterfaceList) {
//                if (networkInterface.name.equals("wlon0", true));
//                run {
//                    for (i in networkInterface.hardwareAddress.indices) {
//                        var stringMacByte = Integer.toHexString(networkInterface.hardwareAddress[i].and(0xFF))
//                        if (stringMacByte.length == 1) {
//                            stringMacByte = "0$stringMacByte"
//                        }
//                        stringMac = stringMac + stringMacByte.toUpperCase() + ":"
//                    }
//                }
//                break
                if (networkInterface.name.equals("wlon0", true)) {
                    for (element in networkInterface.hardwareAddress) {
                        var stringMacByte =
                            Integer.toHexString(element and 0xFF)
                        if (stringMacByte.length == 1) {
                            stringMacByte = "0$stringMacByte"
                        }
                        stringMac = stringMac + stringMacByte.uppercase(Locale.getDefault()) + ":"
                    }
                    break
                }
            }
            if (stringMac.isEmpty()){
                return "N/A"
            }
            return stringMac
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "N/A"
    }

    override fun onDestroy() {
        progressDialog = null
        super.onDestroy()
    }
}