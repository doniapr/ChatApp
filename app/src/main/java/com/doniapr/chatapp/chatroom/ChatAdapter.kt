package com.doniapr.chatapp.chatroom

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.doniapr.chatapp.databinding.ItemIncomingChatBinding
import com.doniapr.chatapp.databinding.ItemOutgoingChatBinding
import com.qiscus.sdk.chat.core.data.model.QiscusComment

class ChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val INCOMING_MESSAGE = 1
    private val OUTGOING_MESSAGE = 2


    private var chatList = ArrayList<QiscusComment>()

    fun setData(chats: List<QiscusComment>) {
        if (chats.isNotEmpty())
            chatList.clear()
        chatList.addAll(chats)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int =
        if (chatList[position].isMyComment) OUTGOING_MESSAGE else INCOMING_MESSAGE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            OUTGOING_MESSAGE -> {
                val binding =
                    ItemOutgoingChatBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                OutgoingChatViewHolder(binding)
            }
            INCOMING_MESSAGE -> {
                val binding =
                    ItemIncomingChatBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                IncomingChatViewHolder(binding)
            }
            else -> {
                throw IllegalStateException("Unexpected value: $viewType")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is OutgoingChatViewHolder) {
            holder.bindItem(chatList[position])
        } else if (holder is IncomingChatViewHolder) {
            holder.bindItem(chatList[position])
        } else throw java.lang.IllegalStateException("Unexpected value")
    }

    override fun getItemCount(): Int = chatList.size

    class IncomingChatViewHolder(private val binding: ItemIncomingChatBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        fun bindItem(qiscusComment: QiscusComment) {
            with(binding) {
                binding.tvIncomingChat.text = qiscusComment.message
            }
        }
    }

    class OutgoingChatViewHolder(private val binding: ItemOutgoingChatBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        fun bindItem(qiscusComment: QiscusComment) {
            with(binding) {
                binding.tvOutgoingChat.text = qiscusComment.message
            }
        }
    }
}