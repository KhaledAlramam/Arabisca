package com.sedra.goiptv.view.settings

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sedra.goiptv.R
import com.sedra.goiptv.data.model.Section
import com.sedra.goiptv.databinding.ListItemCategorySettingBinding
import com.sedra.goiptv.view.series.CustomViewHolder


class CustomViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root)

class SettingsSectionsAdapter(
    val preferences: SharedPreferences,
    val list: List<Section>
) : RecyclerView.Adapter<CustomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemCategorySettingBinding.inflate(inflater, parent, false)

        return CustomViewHolder(binding)

    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val context = holder.binding.root.context
        val currentSection = list[position]
        val itemBinding = holder.binding as ListItemCategorySettingBinding
        Glide.with(context)
            .load(currentSection.image)
            .into(itemBinding.sectionImage)
        itemBinding.apply {
            textView13.text = currentSection.name
            imageView9.isVisible =
                preferences.getString(currentSection.id.toString(), "").isNullOrEmpty()
            imageView10.isVisible =
                !preferences.getString(currentSection.id.toString(), "").isNullOrEmpty()
            imageView9.setOnClickListener {
                showPasswordDialog(currentSection, true, context)

            }
            imageView10.setOnClickListener {
                showPasswordDialog(currentSection, false, context)
            }
            imageView10.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    itemBinding.imageView10.setColorFilter(
                        ContextCompat.getColor(
                            context,
                            R.color.mainDark
                        ), android.graphics.PorterDuff.Mode.MULTIPLY
                    );
                } else {
                    itemBinding.imageView10.setColorFilter(
                        ContextCompat.getColor(
                            context,
                            R.color.white
                        ), android.graphics.PorterDuff.Mode.MULTIPLY
                    )
                }
            }
            imageView9.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    itemBinding.imageView9.setColorFilter(
                        ContextCompat.getColor(
                            context,
                            R.color.mainDark
                        ), android.graphics.PorterDuff.Mode.MULTIPLY
                    );
                } else {
                    itemBinding.imageView9.setColorFilter(
                        ContextCompat.getColor(
                            context,
                            R.color.white
                        ), android.graphics.PorterDuff.Mode.MULTIPLY
                    )
                }
            }
        }
        itemBinding.executePendingBindings()
    }

    private fun showPasswordDialog(
        currentSection: Section,
        settingPassword: Boolean,
        context: Context
    ) {
        val myDialog = Dialog(context)
        myDialog.apply {
            setContentView(R.layout.dialog_section_password)
            setCancelable(true)
            setCanceledOnTouchOutside(true)
        }
        val confirmPassword = myDialog.findViewById<Button>(R.id.confirmPassword)
        val cancel = myDialog.findViewById<Button>(R.id.cancel)
        val sectionPassword = myDialog.findViewById<EditText>(R.id.sectionPassword)
        confirmPassword.setOnClickListener {
            if (settingPassword) {
                val editor = preferences.edit()
                editor.putString(currentSection.id.toString(), sectionPassword.text.toString())
                editor.apply()
                myDialog.dismiss()
                notifyDataSetChanged()
            } else {
                if (sectionPassword.text.toString() == preferences.getString(
                        currentSection.id.toString(),
                        ""
                    )
                ) {
                    val editor = preferences.edit()
                    editor.putString(currentSection.id.toString(), "")
                    editor.apply()
                    myDialog.dismiss()
                    notifyDataSetChanged()
                } else {
                    Toast.makeText(context, "Wrong Password", Toast.LENGTH_SHORT).show()
                }
            }
        }
        cancel.setOnClickListener {
            myDialog.dismiss()
        }
        myDialog.show()
        val window = myDialog.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun getItemCount(): Int = list.size


}