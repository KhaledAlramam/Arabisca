package com.sedra.goiptv.view.customsection

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.sedra.goiptv.R
import com.sedra.goiptv.data.model.CustomItem
import com.sedra.goiptv.databinding.ListItemMovieSeriesBinding
import com.sedra.goiptv.utils.EXTRA_ITEM

class ItemsAdapter : ListAdapter<CustomItem, CustomViewHolder>(Companion) {

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
        val binding = ListItemMovieSeriesBinding.inflate(inflater, parent, false)
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val context = holder.binding.root.context
        val currentMovie = getItem(position)
        val itemBinding = holder.binding as ListItemMovieSeriesBinding
        holder.binding.root.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus){
                val anim: Animation = AnimationUtils.loadAnimation(context, R.anim.scale_in)
                holder.binding.root.startAnimation(anim)
                anim.fillAfter = true
            }else{
                val anim: Animation = AnimationUtils.loadAnimation(context, R.anim.scale_out)
                holder.binding.root.startAnimation(anim)
                anim.fillAfter = true
            }
        }

        Glide.with(holder.itemView)
                .load(currentMovie.image)
                .placeholder(R.drawable.logo)
                .into(itemBinding.imageView8)
        itemBinding.apply {
            ratingTv.isVisible = false
            movieName.text = currentMovie.name.replace(itemBinding.root.context.getString(R.string.dashed),
                    itemBinding.root.context.getString(R.string.space))
        }
        itemBinding.root.setOnClickListener {
            val intent = Intent(it.context, PlayItemActivity::class.java)
            intent.putExtra(EXTRA_ITEM, currentMovie)
            it.context.startActivity(intent)
        }
    }
}