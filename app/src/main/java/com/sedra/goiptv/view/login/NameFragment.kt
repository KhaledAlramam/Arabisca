package com.sedra.goiptv.view.login

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.sedra.goiptv.R
import com.sedra.goiptv.utils.GoTo
import com.sedra.goiptv.utils.PREF_NAME
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NameFragment : Fragment(R.layout.fragment_name) {

    @Inject
    lateinit var preferences: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val saveName= view.findViewById<Button>(R.id.saveName)
        val nameEt= view.findViewById<EditText>(R.id.nameEt)
        saveName.setOnClickListener {
            val name = nameEt.text.toString()
            val editor = preferences.edit()
            editor.putString(PREF_NAME, name)
            editor.apply()
            GoTo.goToMainActivity(requireActivity())
        }
    }
}