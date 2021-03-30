package com.sedra.goiptv.view.series

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.sedra.goiptv.data.model.series_models.Season
import com.sedra.goiptv.databinding.AdapterSeasonTvBinding
import com.sedra.goiptv.utils.SeasonOnClick

class CustomViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root)

class SeasonAdapterOnTv(
        var seasonList: List<Season>,
        private val clickListener: SeasonOnClick
) : RecyclerView.Adapter<CustomViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterSeasonTvBinding.inflate(inflater, parent, false)

        return CustomViewHolder(binding)

    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val currentSeason = seasonList[position]
        Log.e("TAG", "onBindViewHolder: " + currentSeason.name)
        val itemBinding = holder.binding as AdapterSeasonTvBinding

        itemBinding.season = currentSeason
        itemBinding.root.setOnClickListener {
            clickListener.onClick(it, currentSeason)
        }
    }

    override fun getItemCount(): Int = seasonList.size

}