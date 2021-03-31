package com.sedra.goiptv.view.department

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.sedra.goiptv.R
import com.sedra.goiptv.data.model.Movie
import com.sedra.goiptv.databinding.ListItemMovieSeriesBinding
import com.sedra.goiptv.utils.MOVIE_ID_PARAMETER
import com.sedra.goiptv.utils.STREAM_EXT
import com.sedra.goiptv.view.movie.MovieDetailsActivity

class MovieAdapter : ListAdapter<Movie, CustomViewHolder>(Companion) {

    companion object : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.stream_id == newItem.stream_id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemMovieSeriesBinding.inflate(inflater, parent, false)
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val currentMovie = getItem(position)
        val itemBinding = holder.binding as ListItemMovieSeriesBinding
        Glide.with(holder.itemView)
                .load(currentMovie.stream_icon)
                .placeholder(R.drawable.logo)
                .into(itemBinding.imageView8)
        itemBinding.apply {
            ratingTv.text = currentMovie.rating
            movieName.text = currentMovie.name.replace(itemBinding.root.context.getString(R.string.dashed),
                    itemBinding.root.context.getString(R.string.space))
        }
        itemBinding.root.setOnClickListener {
            val intent = Intent(it.context, MovieDetailsActivity::class.java)
            intent.putExtra(MOVIE_ID_PARAMETER, currentMovie.stream_id)
            it.context.startActivity(intent)
        }
    }
}