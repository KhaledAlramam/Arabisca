package com.sedra.goiptv.view.department

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.sedra.goiptv.R
import com.sedra.goiptv.data.model.Movie
import com.sedra.goiptv.data.model.Series
import com.sedra.goiptv.databinding.ListItemMovieSeriesBinding
import com.sedra.goiptv.utils.GoTo
import com.sedra.goiptv.utils.MOVIE_ID_PARAMETER
import com.sedra.goiptv.utils.SERIES_ID_PARAMETER
import com.sedra.goiptv.utils.STREAM_EXT
import com.sedra.goiptv.view.movie.MovieDetailsActivity

class SeriesAdapter : ListAdapter<Series, CustomViewHolder>(Companion) {

    companion object : DiffUtil.ItemCallback<Series>() {
        override fun areItemsTheSame(oldItem: Series, newItem: Series): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Series, newItem: Series): Boolean {
            return oldItem.series_id == newItem.series_id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemMovieSeriesBinding.inflate(inflater, parent, false)
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val context = holder.binding.root.context
        val currentSeries = getItem(position)
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
                .load(currentSeries.cover)
                .placeholder(R.drawable.logo)
                .into(itemBinding.imageView8)
        itemBinding.apply {
            ratingTv.text = currentSeries.rating
            movieName.text = currentSeries.name.replace(itemBinding.root.context.getString(R.string.dashed),
                    itemBinding.root.context.getString(R.string.space))
        }
        itemBinding.root.setOnClickListener {
            GoTo.goToSeriesDetails(it.context,currentSeries.series_id)

        }
    }
}