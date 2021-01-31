package com.doniapr.chatapp.chatroom

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.doniapr.chatapp.databinding.*
import com.doniapr.chatapp.utils.PlaybackStateListener
import com.doniapr.chatapp.utils.Utils
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.qiscus.sdk.chat.core.data.model.QiscusComment

class ChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val INCOMING_MESSAGE = 1
    private val OUTGOING_MESSAGE = 2
    private val INCOMING_MESSAGE_IMAGE = 3
    private val OUTGOING_MESSAGE_IMAGE = 4
    private val INCOMING_MESSAGE_VIDEO = 5
    private val OUTGOING_MESSAGE_VIDEO = 6

    private var chatList = ArrayList<QiscusComment>()

    fun setData(chats: List<QiscusComment>) {
        if (chats.isNotEmpty())
            chatList.clear()
        chatList.addAll(chats)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (chatList[position].isMyComment) {
            when {
                chatList[position].isImage -> {
                    OUTGOING_MESSAGE_IMAGE
                }
                chatList[position].isVideo -> {
                    OUTGOING_MESSAGE_VIDEO
                }
                else -> {
                    OUTGOING_MESSAGE
                }
            }
        } else {
            when {
                chatList[position].isImage -> {
                    INCOMING_MESSAGE_IMAGE
                }
                chatList[position].isVideo -> {
                    INCOMING_MESSAGE_VIDEO
                }
                else -> {
                    INCOMING_MESSAGE
                }
            }
        }
    }


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
            OUTGOING_MESSAGE_IMAGE -> {
                val binding =
                    ItemOutgoingImageBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                OutgoingImageViewHolder(binding)
            }
            INCOMING_MESSAGE_IMAGE -> {
                val binding =
                    ItemIncomingImageBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                IncomingImageViewHolder(binding)
            }
            OUTGOING_MESSAGE_VIDEO -> {
                val binding =
                    ItemOutgoingVideoBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                OutgoingVideoViewHolder(binding)
            }
            INCOMING_MESSAGE_VIDEO -> {
                val binding =
                    ItemIncomingVideoBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                IncomingVideoViewHolder(binding)
            }
            else -> {
                throw IllegalStateException("Unexpected value: $viewType")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is OutgoingChatViewHolder -> {
                holder.bindItem(chatList[position])
            }
            is IncomingChatViewHolder -> {
                holder.bindItem(chatList[position])
            }
            is OutgoingImageViewHolder -> {
                holder.bindItem(chatList[position])
            }
            is IncomingImageViewHolder -> {
                holder.bindItem(chatList[position])
            }
            is OutgoingVideoViewHolder -> {
                holder.bindItem(chatList[position])
            }
            is IncomingVideoViewHolder -> {
                holder.bindItem(chatList[position])
            }
            else -> throw java.lang.IllegalStateException("Unexpected value")
        }
    }

    override fun getItemCount(): Int = chatList.size

    class IncomingChatViewHolder(private val binding: ItemIncomingChatBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        fun bindItem(qiscusComment: QiscusComment) {
            binding.tvIncomingChat.text = qiscusComment.message
        }
    }

    class OutgoingChatViewHolder(private val binding: ItemOutgoingChatBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        fun bindItem(qiscusComment: QiscusComment) {
            binding.tvOutgoingChat.text = qiscusComment.message
        }
    }

    class OutgoingImageViewHolder(private val binding: ItemOutgoingImageBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        fun bindItem(qiscusComment: QiscusComment) {
            Glide.with(binding.root.context).load(qiscusComment.attachmentUri)
                .into(binding.ivOutgoingImage)
        }
    }

    class IncomingImageViewHolder(private val binding: ItemIncomingImageBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        fun bindItem(qiscusComment: QiscusComment) {
            Glide.with(binding.root.context).load(qiscusComment.attachmentUri)
                .into(binding.ivIncomingImage)
        }
    }

    class IncomingVideoViewHolder(private val binding: ItemIncomingVideoBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        fun bindItem(qiscusComment: QiscusComment) {
            val playbackStateListener = PlaybackStateListener()
            val trackSelector = DefaultTrackSelector()
            trackSelector.setParameters(
                trackSelector.buildUponParameters().setMaxVideoSizeSd()
            )
            val player = ExoPlayerFactory.newSimpleInstance(itemView.context, trackSelector)
            binding.videoIncoming.hideController()
            binding.videoIncoming.player = player
            val mediaSource: MediaSource? =
                Utils.buildMediaSource(qiscusComment.attachmentUri, itemView.context)

            player.addListener(playbackStateListener)
            player.prepare(mediaSource, false, false)
        }
    }

    class OutgoingVideoViewHolder(private val binding: ItemOutgoingVideoBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        fun bindItem(qiscusComment: QiscusComment) {
            val playbackStateListener = PlaybackStateListener()
            val trackSelector = DefaultTrackSelector()
            trackSelector.setParameters(
                trackSelector.buildUponParameters().setMaxVideoSizeSd()
            )
            val player = ExoPlayerFactory.newSimpleInstance(itemView.context, trackSelector)
            binding.videoOutgoing.hideController()
            binding.videoOutgoing.player = player
            val mediaSource: MediaSource? =
                Utils.buildMediaSource(qiscusComment.attachmentUri, itemView.context)

            player.addListener(playbackStateListener)
            player.prepare(mediaSource, false, false)
        }
    }
}