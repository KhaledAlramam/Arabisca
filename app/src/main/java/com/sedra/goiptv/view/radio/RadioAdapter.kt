package com.sedra.goiptv.view.radio

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.webkit.WebSettings
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.sedra.goiptv.R
import com.sedra.goiptv.data.model.Movie
import com.sedra.goiptv.data.model.RadioStation
import com.sedra.goiptv.databinding.AdapterRadioListItemBinding
import com.sedra.goiptv.databinding.ListItemMovieSeriesBinding
import com.sedra.goiptv.utils.MOVIE_ID_PARAMETER
import com.sedra.goiptv.utils.STREAM_EXT
import com.sedra.goiptv.utils.getRadioStations
import com.sedra.goiptv.view.customsection.CustomViewHolder
import com.sedra.goiptv.view.movie.MovieDetailsActivity

class RadioAdapter : ListAdapter<String, CustomViewHolder>(Companion) {

    companion object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterRadioListItemBinding.inflate(inflater, parent, false)
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val currentRadioStation = getItem(position)
        val itemBinding = holder.binding as AdapterRadioListItemBinding
        itemBinding.apply {
            web.settings.javaScriptEnabled = true
            web.settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
            web.loadData(currentRadioStation, "text/html", null);
        }
    }
}