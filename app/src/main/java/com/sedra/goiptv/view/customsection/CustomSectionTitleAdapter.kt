package com.sedra.goiptv.view.customsection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sedra.goiptv.R
import com.sedra.goiptv.data.model.Category
import com.sedra.goiptv.data.model.Section
import com.sedra.goiptv.data.model.SubSection
import com.sedra.goiptv.databinding.ListItemDepartmentBinding
import com.sedra.goiptv.databinding.ListItemMainCategoriesBinding
import com.sedra.goiptv.utils.GoTo

class CustomViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root)

class CustomSectionTitleAdapter(
        val list: List<SubSection>,
        private val listener: OnDepartmentClicked
): RecyclerView.Adapter<CustomViewHolder>() {
    var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemDepartmentBinding.inflate(inflater, parent, false)

        return CustomViewHolder(binding)

    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val context= holder.binding.root.context
        val currentDepartment = list[position]
        val itemBinding = holder.binding as ListItemDepartmentBinding
        itemBinding.departmentName.text = currentDepartment.name
        if (position == selectedPosition){
            holder.itemView.setBackgroundResource(R.drawable.round_corners_8_white)
            itemBinding.departmentName.setTextColor(context.resources.getColor(R.color.mainDark))
        }else{
            holder.itemView.setBackgroundColor(context.resources.getColor(android.R.color.transparent))
            itemBinding.departmentName.setTextColor(context.resources.getColor(R.color.white))
        }
        holder.itemView.setOnClickListener {
            if (selectedPosition == position) return@setOnClickListener
            val oldSelected = selectedPosition
            selectedPosition = position
            notifyItemChanged(selectedPosition)
            notifyItemChanged(oldSelected)
            listener.onClick(it, position)
        }
    }

    override fun getItemCount(): Int = list.size
}
interface OnDepartmentClicked{
    fun onClick(view: View, position: Int)
}