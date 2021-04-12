package com.sedra.goiptv.view.sections

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sedra.goiptv.data.model.Section
import com.sedra.goiptv.databinding.ListItemMainCategoriesBinding
import com.sedra.goiptv.utils.GoTo
import com.sedra.goiptv.utils.STREAM_ID_INTENT_EXTRA
import com.sedra.goiptv.utils.STREAM_IMG
import com.sedra.goiptv.view.channels.PlayChannelsNewActivity
import com.sedra.goiptv.view.department.DepartmentActivity
import com.sedra.goiptv.view.series.CustomViewHolder

class CustomViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root)

class SectionsAdapter(
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
        Glide.with(context)
                .load(currentSection.image)
                .into(itemBinding.obliqueImage)
        itemBinding.textView6.text = currentSection.name
        itemBinding.root.setOnClickListener {
            if (currentSection.id < 0)
                if (currentSection.id == DepartmentActivity.CHANNELS_ID){
                    GoTo.goToPlayChannelActivity(context)
                }else
                GoTo.goToDepartmentActivity(context, currentSection.id, currentSection.name)
            else
                GoTo.goToCustomSectionActivity(context, currentSection.id, currentSection.name)
        }
        itemBinding.executePendingBindings()
    }

    override fun getItemCount(): Int = list.size


}