package com.sedra.goiptv.view.customsection

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.sedra.goiptv.R
import com.sedra.goiptv.data.model.CustomItem
import com.sedra.goiptv.databinding.AdapterChannelItemBinding
import com.sedra.goiptv.utils.PositionOnClick

class CustomChannelAdapter(
        val activity: Activity,
        val listener: PositionOnClick,
) : ListAdapter<CustomItem, CustomViewHolder>(Companion) {

    companion object : DiffUtil.ItemCallback<CustomItem>() {
        override fun areItemsTheSame(oldItem: CustomItem, newItem: CustomItem): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: CustomItem, newItem: CustomItem): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterChannelItemBinding.inflate(inflater, parent, false)
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val context = holder.binding.root.context
        val currentChannel = getItem(position)
        val itemBinding = holder.binding as AdapterChannelItemBinding
        Glide.with(holder.itemView)
                .load(currentChannel.image)
                .placeholder(R.drawable.logo)
                .into(itemBinding.channelIcon)
        itemBinding.apply {
            channelName.text = currentChannel.name?.replace(itemBinding.root.context.getString(R.string.dashed),
                    itemBinding.root.context.getString(R.string.space))?.replace(itemBinding.root.context.getString(R.string.under_score),
                    itemBinding.root.context.getString(R.string.space))
            channelNumberList.text = "${position + 1}"
        }
        itemBinding.root.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                itemBinding.channelName.setTextColor(context.resources.getColor(R.color.white))
            } else {
                itemBinding.channelName.setTextColor(context.resources.getColor(R.color.mainDark))
            }
        }

        itemBinding.root.setOnClickListener { v ->
            listener.onClick(v, position)
        }
    }
}