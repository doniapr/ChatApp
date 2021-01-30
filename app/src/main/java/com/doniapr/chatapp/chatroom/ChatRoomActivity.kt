package com.doniapr.chatapp.chatroom

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.doniapr.chatapp.databinding.ActivityChatRoomBinding
import com.qiscus.sdk.chat.core.data.model.QiscusChatRoom
import com.qiscus.sdk.chat.core.data.model.QiscusComment
import com.qiscus.sdk.chat.core.data.remote.QiscusApi
import com.qiscus.sdk.chat.core.event.QiscusCommentReceivedEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


class ChatRoomActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatRoomBinding
    private var qiscusChatRoom: QiscusChatRoom? = QiscusChatRoom()
    private var listComment = ArrayList<QiscusComment>()
    private val chatAdapter = ChatAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        qiscusChatRoom = intent.getParcelableExtra<QiscusChatRoom>("chat_room")

        QiscusApi.getInstance().getChatRoomInfo(qiscusChatRoom!!.id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Log.e("NAME", it.name)
                    getChatHistory(it.id)
                },
                { t: Throwable? ->
                    Log.e("TAG", t?.message, t)
                }
            )

        with(binding.rvChat) {
            layoutManager =
                LinearLayoutManager(this@ChatRoomActivity, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter = chatAdapter
        }

        binding.btnSendMessage.setOnClickListener {
            if (binding.etMessage.text.isNullOrEmpty()) {
                binding.etMessage.error = "Email must be set"
                binding.etMessage.requestFocus()

                return@setOnClickListener
            }
            val comment = qiscusChatRoom?.id?.let {
                QiscusComment.generateMessage(
                    it,
                    binding.etMessage.text.toString()
                )
            }
            comment?.let { it1 -> sendMessage(it1) }

            binding.etMessage.text.clear()
        }

    }

    private fun getChatHistory(id: Long) {
        QiscusApi.getInstance().getPreviousMessagesById(id, 100)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    listComment.add(it)
                    chatAdapter.setData(listComment)
                    binding.rvChat.scrollToPosition(listComment.size - 1)
                },
                { t: Throwable? ->
                    Log.e("Chat history", t?.message, t)
                }
            )
    }

    private fun sendMessage(comment: QiscusComment) {
        QiscusApi.getInstance().sendMessage(comment)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Log.e("TAG", it.sender)
                },
                {
                    Log.e("TAG", it.message, it)
                }
            )
    }

    @Subscribe
    fun onReceiveComment(event: QiscusCommentReceivedEvent) {
        Log.e("KOMEN", event.qiscusComment.message)
        listComment.add(event.qiscusComment)
        chatAdapter.setData(listComment)
        binding.rvChat.scrollToPosition(listComment.size - 1)
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this);
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this);
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this);
    }
}