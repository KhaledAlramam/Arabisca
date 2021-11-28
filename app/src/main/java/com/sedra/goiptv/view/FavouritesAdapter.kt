package com.sedra.goiptv.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sedra.goiptv.R
import com.sedra.goiptv.data.db.FavouriteItem
import com.sedra.goiptv.utils.FavouriteItemOnClick

class FavouritesAdapter(
    private var context: Context?,
    var favouriteItems: List<FavouriteItem>,
    private val clickListener: FavouriteItemOnClick
) : RecyclerView.Adapter<FavouritesAdapter.FavouritesViewHolder>() {


    class FavouritesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemTitle: TextView = view.findViewById(R.id.customTextView6)
        val itemImg: ImageView = view.findViewById(R.id.episodeImg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouritesViewHolder {
        return FavouritesViewHolder(
            LayoutInflater.from(context).inflate(R.layout.adapter_episode_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: FavouritesViewHolder, position: Int) {
        holder.itemTitle.text = favouriteItems[position].title
        if (context != null) {
            Glide.with(context!!)
                .load(favouriteItems[position].imageUrl)
                .placeholder(R.drawable.logo)
                .into(holder.itemImg)
        }
        holder.itemView.setOnClickListener {
            clickListener.onClick(favouriteItems[position], it)
        }
    }

    fun setItems(newFavouriteItems: List<FavouriteItem>) {
        favouriteItems = newFavouriteItems
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = favouriteItems.size


}