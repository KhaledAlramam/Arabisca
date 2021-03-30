package com.sedra.goiptv.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sedra.goiptv.data.model.UserInfo

class PreferenceUtils private constructor() {
    fun clearAllPreferences() {
        prefsEditor = myPrefs!!.edit()
        prefsEditor!!.clear()
        prefsEditor!!.commit()
    }

    fun clearPreferences(key: String?) {
        prefsEditor!!.remove(key)
        prefsEditor!!.commit()
    }

    fun setIntValue(Tag: String?, value: Int) {
        prefsEditor!!.putInt(Tag, value)
        prefsEditor!!.apply()
    }

    fun setLongValue(Tag: String?, value: Long) {
        prefsEditor!!.putLong(Tag, value)
        prefsEditor!!.apply()
    }

    fun getIntValue(Tag: String?): Int {
        return myPrefs!!.getInt(Tag, 0)
    }

    fun getLongValue(Tag: String?): Long {
        return myPrefs!!.getLong(Tag, 0)
    }

    fun setStringValue(Tag: String?, token: String?) {
        prefsEditor!!.putString(Tag, token)
        prefsEditor!!.commit()
    }

    fun getValue(Tag: String?): String? {
        return myPrefs!!.getString(Tag, null)
    }

    fun getBooleanValue(Tag: String?): Boolean {
        return myPrefs!!.getBoolean(Tag, false)
    }

    fun setBooleanValue(Tag: String?, token: Boolean) {
        prefsEditor!!.putBoolean(Tag, token)
        prefsEditor!!.commit()
    }

    var parentUser: UserInfo?
        get() {
            val obj = myPrefs!!.getString(PREF_PARENT_USER, "defValue")
            return if (obj == "defValue") {
                null
            } else {
                val gson = Gson()
                val storedHashMapString =
                    myPrefs!!.getString(PREF_PARENT_USER, "")
                val type = object : TypeToken<UserInfo>() {}.type
                gson.fromJson(storedHashMapString, type)
            }
        }
        set(userInfo) {
            val gson = Gson()
            val hashMapString = gson.toJson(userInfo)
            prefsEditor!!.putString(PREF_PARENT_USER, hashMapString)
            prefsEditor!!.apply()
        }

    companion object {
        var myPrefs: SharedPreferences? = null
        var prefsEditor: SharedPreferences.Editor? = null
        var myObj: PreferenceUtils? = null
        fun getInstance(ctx: Context?): PreferenceUtils {
            if (myObj == null) {
                myObj = PreferenceUtils()
                myPrefs = PreferenceManager.getDefaultSharedPreferences(ctx)
                prefsEditor = myPrefs!!.edit()
            }
            return myObj!!
        }
    }
}