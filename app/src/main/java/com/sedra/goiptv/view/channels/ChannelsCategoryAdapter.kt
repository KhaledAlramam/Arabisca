package com.sedra.goiptv.view.channels

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sedra.goiptv.data.model.Category
import com.sedra.goiptv.databinding.AdapterChannelCategoryBinding
import com.sedra.goiptv.utils.CategoryOnClick
import com.sedra.goiptv.view.department.CustomViewHolder

class ChannelsCategoryAdapter(
    var categoryList: List<Category>,
    private val clickListener: CategoryOnClick
) : RecyclerView.Adapter<CustomViewHolder>() {

    var selectedItemIndex = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterChannelCategoryBinding.inflate(inflater, parent, false)
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val context = holder.binding.root.context
        val itemBinding = holder.binding as AdapterChannelCategoryBinding
        itemBinding.category = categoryList[position].category_name
        itemBinding.root.setOnClickListener {
            selectedItemIndex = position
            clickListener.onClick(itemBinding.root, categoryList[position])
            notifyDataSetChanged()
        }
        if (selectedItemIndex == position) {
            itemBinding.imageView15.alpha = 0.9f
        } else {
            holder.binding.root.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    itemBinding.imageView15.alpha = 0.9f
                } else {
                    itemBinding.imageView15.alpha = 0.4f
                }
            }

        }
    }

    override fun getItemCount(): Int = categoryList.size

}