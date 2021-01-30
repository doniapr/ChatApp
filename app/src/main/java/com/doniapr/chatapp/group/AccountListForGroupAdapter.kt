package com.doniapr.chatapp.group

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.doniapr.chatapp.databinding.ItemAccountListForGroupBinding
import com.qiscus.sdk.chat.core.data.model.QiscusAccount

class AccountListForGroupAdapter(private val listener: (QiscusAccount) -> Unit): RecyclerView.Adapter<AccountListForGroupAdapter.AccountListViewHolder>() {
    private var accounts = ArrayList<QiscusAccount>()

    fun setData(accountList: List<QiscusAccount>){
        accounts.addAll(accountList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountListViewHolder {
        val binding = ItemAccountListForGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AccountListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AccountListViewHolder, position: Int) {
        holder.bindItem(accounts[position], listener)
    }

    override fun getItemCount(): Int = accounts.size

    class AccountListViewHolder(val binding: ItemAccountListForGroupBinding):RecyclerView.ViewHolder(binding.root) {
        fun bindItem(qiscusAccount: QiscusAccount, listener: (QiscusAccount) -> Unit){
            with(binding){
                root.setOnClickListener {
                    listener(qiscusAccount)
                }

                tvNameUser.text = qiscusAccount.username

            }
        }
    }
}