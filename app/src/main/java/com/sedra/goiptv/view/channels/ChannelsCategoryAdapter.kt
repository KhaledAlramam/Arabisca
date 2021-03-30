package com.sedra.goiptv.view.channels

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sedra.goiptv.data.model.Category
import com.sedra.goiptv.databinding.AdapterChannelCategoryBinding
import com.sedra.goiptv.utils.CategoryOnClick
import com.sedra.goiptv.view.department.CustomViewHolder

class ChannelsCategoryAdapter(
    private val onTv: Boolean,
    var categoryList: List<Category>,
    private val clickListener: CategoryOnClick
) : RecyclerView.Adapter<CustomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterChannelCategoryBinding.inflate(inflater, parent, false)
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val itemBinding = holder.binding as AdapterChannelCategoryBinding
        itemBinding.category = categoryList[position]
        itemBinding.root.setOnClickListener {
            clickListener.onClick(itemBinding.root, categoryList[position])
        }

    }

    override fun getItemCount(): Int = categoryList.size


    fun setItems(items: List<Category>) {
        categoryList = items
        notifyDataSetChanged()
    }
}