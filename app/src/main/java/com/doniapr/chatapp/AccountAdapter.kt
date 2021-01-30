package com.doniapr.chatapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.doniapr.chatapp.chatroom.ChatRoomActivity
import com.doniapr.chatapp.databinding.ItemAccountBinding
import com.qiscus.sdk.chat.core.data.model.QiscusAccount

class AccountAdapter: RecyclerView.Adapter<AccountAdapter.AccountViewHolder>() {
    private var accounts = ArrayList<QiscusAccount>()

    fun setData(accountList: List<QiscusAccount>){
        accounts.addAll(accountList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val binding = ItemAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AccountViewHolder(binding)

    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        holder.bindItem(accounts[position])
    }

    override fun getItemCount(): Int = accounts.size

    class AccountViewHolder(val binding: ItemAccountBinding): RecyclerView.ViewHolder(binding.root) {
        fun bindItem(qiscusAccount: QiscusAccount){
            with(binding){
                root.setOnClickListener {
                    val intent = Intent(root.context, ChatRoomActivity::class.java)
                    intent.putExtra("user", qiscusAccount)
                    root.context.startActivity(intent)
                }

                tvAccountName.text = qiscusAccount.username
            }
        }

    }
}