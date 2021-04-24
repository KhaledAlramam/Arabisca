package com.sedra.goiptv.view.customsection

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sedra.goiptv.data.model.CustomItem
import com.sedra.goiptv.databinding.AdapterEpisodeItemBinding
import com.sedra.goiptv.utils.EXTRA_ITEM

class CustomEpisodeAdapter(
        var episodeList: List<CustomItem>,
        private val cover: String?,
) : RecyclerView.Adapter<CustomViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterEpisodeItemBinding.inflate(inflater, parent, false)

        return CustomViewHolder(binding)

    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val currentEpisode = episodeList[position]
        val itemBinding = holder.binding as AdapterEpisodeItemBinding
        itemBinding.customTextView6.text = currentEpisode.name
        itemBinding.episodeParent.setOnClickListener {
            val intent = Intent(itemBinding.root.context, PlayItemActivity::class.java)
            intent.putExtra(EXTRA_ITEM, currentEpisode)
            itemBinding.root.context.startActivity(intent)
        }
        Glide.with(holder.itemView)
                .load(currentEpisode.image)
                .into(itemBinding.episodeImg)
    }

    override fun getItemCount(): Int = episodeList.size

    fun setItems(newList: List<CustomItem>) {
        episodeList = newList
        notifyDataSetChanged()
    }

}