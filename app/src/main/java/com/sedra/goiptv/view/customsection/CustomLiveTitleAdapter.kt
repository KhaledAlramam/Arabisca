package com.sedra.goiptv.view.customsection

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sedra.goiptv.R
import com.sedra.goiptv.data.model.SubSection
import com.sedra.goiptv.databinding.AdapterChannelCategoryBinding


class CustomLiveTitleAdapter(
        val list: List<SubSection>,
        private val listener: OnDepartmentClicked
) : RecyclerView.Adapter<CustomViewHolder>() {
    var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterChannelCategoryBinding.inflate(inflater, parent, false)

        return CustomViewHolder(binding)

    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val context = holder.binding.root.context
        val currentDepartment = list[position]
        val itemBinding = holder.binding as AdapterChannelCategoryBinding
        itemBinding.category = currentDepartment.name
//        if (position == selectedPosition){
//            holder.itemView.setBackgroundResource(R.drawable.round_corners_8_white)
//            itemBinding.channelCategoryTv.setTextColor(context.resources.getColor(R.color.mainDark))
//        }else{
//            holder.itemView.setBackgroundColor(context.resources.getColor(android.R.color.transparent))
//            itemBinding.channelCategoryTv.setTextColor(context.resources.getColor(R.color.white))
//        }

        holder.itemView.setOnClickListener {
//            if (selectedPosition == position) return@setOnClickListener
//            val oldSelected = selectedPosition
//            selectedPosition = position
//            notifyItemChanged(selectedPosition)
//            notifyItemChanged(oldSelected)
            listener.onClick(it, position)
        }
        holder.binding.root.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                itemBinding.channelCategoryTv.setTextColor(context.resources.getColor(R.color.white))
            } else {
                itemBinding.channelCategoryTv.setTextColor(context.resources.getColor(R.color.mainDark))

            }
        }

    }

    override fun getItemCount(): Int = list.size
}