package com.doniapr.chatapp.group

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.doniapr.chatapp.R
import com.doniapr.chatapp.databinding.ItemAccountListForGroupBinding
import com.qiscus.sdk.chat.core.data.model.QiscusAccount

class AccountListForGroupAdapter(private val listener: (QiscusAccount, Boolean) -> Unit) :
    RecyclerView.Adapter<AccountListForGroupAdapter.AccountListViewHolder>() {
    private var accounts = ArrayList<QiscusAccount>()

    fun setData(accountList: List<QiscusAccount>) {
        accounts.addAll(accountList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountListViewHolder {
        val binding = ItemAccountListForGroupBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AccountListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AccountListViewHolder, position: Int) {
        holder.bindItem(accounts[position], listener)
    }

    override fun getItemCount(): Int = accounts.size

    class AccountListViewHolder(private val binding: ItemAccountListForGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("ResourceAsColor")
        fun bindItem(qiscusAccount: QiscusAccount, listener: (QiscusAccount, Boolean) -> Unit) {
            var isSelected = false
            with(binding) {
                root.setOnClickListener {
                    isSelected = if (isSelected) {
                        cvAccount.setBackgroundColor(android.R.color.darker_gray)
                        !isSelected
                    } else {
                        cvAccount.setBackgroundColor(R.color.teal_700)
                        !isSelected
                    }
                    listener(qiscusAccount, isSelected)
                }
                tvNameUser.text = qiscusAccount.username
            }
        }
    }
}