package com.sedra.goiptv.view.department

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.sedra.goiptv.R
import com.sedra.goiptv.data.model.LiveStream
import com.sedra.goiptv.databinding.ListItemMovieSeriesBinding
import com.sedra.goiptv.utils.*

class ChannelAdapter(
        val listener: ChannelOnClick
) : ListAdapter<LiveStream, CustomViewHolder>(Companion) {

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
        val binding = ListItemMovieSeriesBinding.inflate(inflater, parent, false)
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val currentChannel = getItem(position)
        val itemBinding = holder.binding as ListItemMovieSeriesBinding
        Glide.with(holder.itemView)
                .load(currentChannel.streamIcon)
                .placeholder(R.drawable.logo)
                .into(itemBinding.imageView8)
        itemBinding.apply {
            ratingTv.isVisible = false
            movieName.text = currentChannel.name?.replace(itemBinding.root.context.getString(R.string.dashed),
                    itemBinding.root.context.getString(R.string.space))
        }
        itemBinding.root.setOnClickListener {
            listener.onClick(it, currentChannel)
        }
    }
}