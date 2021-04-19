package com.sedra.goiptv.view.sections

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sedra.goiptv.R
import com.sedra.goiptv.data.model.Section
import com.sedra.goiptv.databinding.ListItemMainCategoriesBinding
import com.sedra.goiptv.utils.GoTo
import com.sedra.goiptv.view.department.DepartmentActivity
import com.sedra.goiptv.view.series.CustomViewHolder


class CustomViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root)

class SectionsAdapter(
        val preferences: SharedPreferences,
        val list: List<Section>
) : RecyclerView.Adapter<CustomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemMainCategoriesBinding.inflate(inflater, parent, false)

        return CustomViewHolder(binding)

    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val context = holder.binding.root.context
        val currentSection = list[position]
        val itemBinding = holder.binding as ListItemMainCategoriesBinding
        holder.binding.root.translationX= (-80f * position)
        holder.binding.root.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus){
                val anim: Animation = AnimationUtils.loadAnimation(context, R.anim.transition_up)
                holder.binding.root.startAnimation(anim)
                anim.fillAfter = true
            }else{
                val anim: Animation = AnimationUtils.loadAnimation(context, R.anim.transition_down)
                holder.binding.root.startAnimation(anim)
                anim.fillAfter = true
            }
        }

        Glide.with(context)
                .load(currentSection.image)
                .into(itemBinding.obliqueImage)
        itemBinding.textView6.text = currentSection.name
        itemBinding.root.setOnClickListener {
            if (hasPassword(currentSection)){
                showPasswordDialog(currentSection, context)
            }else{
                handleNavigation(context, currentSection)
            }
        }
        itemBinding.executePendingBindings()
    }

    private fun hasPassword(currentSection: Section): Boolean {
        val password = preferences.getString(currentSection.id.toString(),"")
        return !password.isNullOrEmpty()
    }

    private fun handleNavigation(context: Context, currentSection: Section) {
        if (currentSection.id < 0)
            if (currentSection.id == DepartmentActivity.CHANNELS_ID){
                GoTo.goToPlayChannelActivity(context)
            }else
                GoTo.goToDepartmentActivity(context, currentSection.id, currentSection.name)
        else
            GoTo.goToCustomSectionActivity(context, currentSection.id, currentSection.name)
    }

    override fun getItemCount(): Int = list.size


    private fun showPasswordDialog(currentSection: Section, context: Context){
        val myDialog = Dialog(context)
        myDialog.apply {
            setContentView(R.layout.dialog_section_password)
            setCancelable(true)
            setCanceledOnTouchOutside(true)
        }
        val password = preferences.getString(currentSection.id.toString(),"")
        val confirmPassword = myDialog.findViewById<Button>(R.id.confirmPassword)
        val cancel = myDialog.findViewById<Button>(R.id.cancel)
        val sectionPassword = myDialog.findViewById<EditText>(R.id.sectionPassword)
        confirmPassword.setOnClickListener {
                if (sectionPassword.text.toString() == password){
                    myDialog.dismiss()
                    handleNavigation(context, currentSection)
                }else{
                    Toast.makeText(context, "Wrong Password", Toast.LENGTH_SHORT).show()
                }
        }
        cancel.setOnClickListener {
            myDialog.dismiss()
        }
        myDialog.show()
        val window = myDialog.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

}