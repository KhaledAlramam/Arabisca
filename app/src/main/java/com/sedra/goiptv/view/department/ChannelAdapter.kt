package com.sedra.goiptv.view.department

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.sedra.goiptv.R
import com.sedra.goiptv.data.model.LiveStream
import com.sedra.goiptv.databinding.AdapterChannelItemBinding
import com.sedra.goiptv.utils.ChannelOnClick

class ChannelAdapter(
        val activity: Activity,
        val listener: ChannelOnClick,
) : ListAdapter<LiveStream, CustomViewHolder>(Companion) {

    var selectedItemIndex = 0

    companion object : DiffUtil.ItemCallback<LiveStream>() {
        override fun areItemsTheSame(oldItem: LiveStream, newItem: LiveStream): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: LiveStream, newItem: LiveStream): Boolean {
            return oldItem.streamId == newItem.streamId
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
        if (position == selectedItemIndex) {
            itemBinding.root.requestFocus()
        }
        itemBinding.apply {
            channelName.text = currentChannel.name?.replace(
                itemBinding.root.context.getString(R.string.dashed),
                itemBinding.root.context.getString(R.string.space)
            )?.replace(
                itemBinding.root.context.getString(R.string.under_score),
                itemBinding.root.context.getString(R.string.space)
            )
            channelNumberList.text = "${position + 1}"
        }
        itemBinding.root.setOnFocusChangeListener { v, hasFocus ->
            selectedItemIndex = position
            listener.onClick(v, true, currentChannel, position)
            notifyDataSetChanged()
        }
        if (selectedItemIndex == position) {
            itemBinding.imageView15.isVisible = true
        } else {
            holder.binding.root.setOnFocusChangeListener { v, hasFocus ->
                itemBinding.imageView15.isVisible = hasFocus
            }

        }

        itemBinding.root.setOnClickListener { v ->
            selectedItemIndex = position
            listener.onClick(v, true, currentChannel, position)
            notifyDataSetChanged()
        }
    }

    override fun submitList(list: MutableList<LiveStream>?) {
        super.submitList(list)
        selectedItemIndex = 0
    }

}