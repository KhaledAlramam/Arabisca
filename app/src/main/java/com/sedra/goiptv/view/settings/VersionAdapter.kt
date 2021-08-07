package com.sedra.goiptv.view.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sedra.goiptv.data.model.Account
import com.sedra.goiptv.data.model.Version
import com.sedra.goiptv.databinding.ListItemAccountSelectBinding
import com.sedra.goiptv.utils.PositionOnClick

class VersionAdapter(
        val list: List<Version>,
        val listener: PositionOnClick
) : RecyclerView.Adapter<VersionAdapter.AccountsViewHolder>() {

    class AccountsViewHolder(val itemBinding: ListItemAccountSelectBinding) : RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemAccountSelectBinding.inflate(inflater, parent, false)
        return AccountsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AccountsViewHolder, position: Int) {
        val binding = holder.itemBinding
        binding.apply {
            root.setOnClickListener {
                listener.onClick(it, position)
            }
            textView26.text = list[position].name
        }
    }

    override fun getItemCount(): Int =
            list.size
}