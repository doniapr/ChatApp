package com.doniapr.chatapp.personalchat

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.doniapr.chatapp.chatroom.ChatRoomActivity
import com.doniapr.chatapp.databinding.ItemAccountBinding
import com.qiscus.sdk.chat.core.data.model.QiscusAccount

class UserListAdapter(private val listener: (QiscusAccount) -> Unit) : RecyclerView.Adapter<UserListAdapter.UserListViewHolder>() {
    private var accounts = ArrayList<QiscusAccount>()

    fun setData(accountList: List<QiscusAccount>) {
        accounts.addAll(accountList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListViewHolder {
        val binding = ItemAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserListViewHolder(binding)

    }

    override fun onBindViewHolder(holder: UserListViewHolder, position: Int) {
        holder.bindItem(accounts[position], listener)
    }

    override fun getItemCount(): Int = accounts.size

    class UserListViewHolder(val binding: ItemAccountBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(qiscusAccount: QiscusAccount, listener: (QiscusAccount) -> Unit) {
            with(binding) {
                root.setOnClickListener {
                    listener(qiscusAccount)
                }

                tvAccountName.text = qiscusAccount.username
            }
        }

    }
}