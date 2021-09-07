package com.sedra.goiptv.view.settings

import android.app.AlertDialog
import android.app.Dialog
import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sedra.goiptv.R
import com.sedra.goiptv.data.model.Account
import com.sedra.goiptv.data.model.Section
import com.sedra.goiptv.data.model.UserInfo
import com.sedra.goiptv.databinding.ActivitySettingsBinding
import com.sedra.goiptv.utils.*
import com.sedra.goiptv.view.login.AccountsAdapter
import com.sedra.goiptv.view.login.AuthViewModel
import com.sedra.goiptv.view.starting.StartingActivity
import dagger.hilt.android.AndroidEntryPoint
import dmax.dialog.SpotsDialog
import java.util.*
import javax.inject.Inject


private const val TAG = "SettingsActivity"
@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    lateinit var binding: ActivitySettingsBinding
    val viewModel: SettingsViewModel by viewModels()
    val authViewModel: AuthViewModel by viewModels()

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
        val currentCode: String = preferences.getString(PREF_CODE, "Error") ?: ""
        val currentMac: String = preferences.getString(PREF_MAC, "Error") ?: ""
        binding.apply {
            code = currentCode
            mac = currentMac
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
            changeServer.setOnClickListener {
                getAccounts(currentCode, currentMac)
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

    private fun getAccounts(code: String, macAdd: String) {
        authViewModel.getAccounts(code, macAdd).observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        progressDialog?.dismiss()
                        if (resource.data == null) {
                        } else {
                            if (resource.data.data == null) {
                                Toast.makeText(this, resource.data.message, Toast.LENGTH_SHORT)
                                    .show()

                            } else {
                                val editor = preferences.edit()
                                editor.putString(PREF_CODE, code)
                                editor.putString(PREF_MAC, macAdd)
                                editor.apply()
                                if (resource.data.data.size > 1) {
                                    showAccountPicker(resource.data.data)
                                } else {
                                    Toast.makeText(
                                        this,
                                        "No Other server connected",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            }
                        }
                    }
                    Status.ERROR -> {
                        progressDialog?.dismiss()
                        Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                    }
                    Status.LOADING -> {
                        progressDialog?.show()
                    }
                }
            }
        }
    }

    private fun showAccountPicker(accounts: List<Account>) {
        val myDialog = Dialog(this)
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
            layoutManager = LinearLayoutManager(this@SettingsActivity)
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
        editor.putString(PREF_USER_NAME, account.username)
        editor.putString(PREF_PASSWORD, account.password)
        editor.apply()
        val i = Intent(this, StartingActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        i.putExtra(EXTRA_FROM_SETTING, true)
        startActivity(i)
    }

}