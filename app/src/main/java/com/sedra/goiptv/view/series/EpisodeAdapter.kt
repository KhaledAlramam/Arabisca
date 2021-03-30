package com.sedra.goiptv.view.series

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sedra.goiptv.data.model.series_models.Episode
import com.sedra.goiptv.databinding.AdapterEpisodeItemBinding
import com.sedra.goiptv.utils.EpisodeOnClick

class EpisodeAdapter(
    var episodeList: List<Episode>,
    private val cover: String?,
    private val clickListener: EpisodeOnClick
) : RecyclerView.Adapter<CustomViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterEpisodeItemBinding.inflate(inflater, parent, false)

        return CustomViewHolder(binding)

    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val currentEpisode = episodeList[position]
        val itemBinding = holder.binding as AdapterEpisodeItemBinding
        itemBinding.episode = currentEpisode
        itemBinding.episodeParent.setOnClickListener {
            clickListener.onClick(it, currentEpisode)
        }
        Glide.with(holder.itemView)
            .load(cover)
            .into(itemBinding.episodeImg)
    }

    override fun getItemCount(): Int = episodeList.size

    fun setItems(newList: List<Episode>) {
        episodeList = newList
        notifyDataSetChanged()
    }

}