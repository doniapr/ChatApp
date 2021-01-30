package com.doniapr.chatapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.doniapr.chatapp.databinding.ItemAccountBinding
import com.qiscus.sdk.chat.core.data.model.QiscusAccount
import com.qiscus.sdk.chat.core.data.model.QiscusChatRoom

class ChatRoomAdapter: RecyclerView.Adapter<ChatRoomAdapter.AccountViewHolder>() {
    private var accounts = ArrayList<QiscusChatRoom>()

    fun setData(accountList: List<QiscusChatRoom>){
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
        fun bindItem(qiscusChatRoom: QiscusChatRoom){
            with(binding){
                root.setOnClickListener {
                    val intent = Intent(root.context, ChatRoomActivity::class.java)
                    intent.putExtra("user_id", qiscusChatRoom.id)
                    root.context.startActivity(intent)
                }

                tvAccountName.text = qiscusChatRoom.name
            }
        }

    }
}