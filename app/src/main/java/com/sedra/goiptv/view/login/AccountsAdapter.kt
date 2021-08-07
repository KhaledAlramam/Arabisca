package com.sedra.goiptv.view.login

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sedra.goiptv.data.model.Account
import com.sedra.goiptv.databinding.ListItemAccountSelectBinding
import com.sedra.goiptv.utils.PositionOnClick

class AccountsAdapter(
        val list: List<Account>,
        val listener: PositionOnClick
) : RecyclerView.Adapter<AccountsAdapter.AccountsViewHolder>() {

    class AccountsViewHolder(val itemBinding: ListItemAccountSelectBinding) : RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemAccountSelectBinding.inflate(inflater, parent, false)
        return AccountsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AccountsViewHolder, position: Int) {
        val context = holder.itemBinding.root.context
        val currentAccount = list[position]
        val binding = holder.itemBinding
        binding.apply {
            account = currentAccount
            binding.root.setOnClickListener {
                listener.onClick(it, position)
            }
            textView26.text = list[position].name
        }
    }

    override fun getItemCount(): Int =
            list.size
}