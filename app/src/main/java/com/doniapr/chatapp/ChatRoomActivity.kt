package com.doniapr.chatapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.doniapr.chatapp.databinding.ActivityChatRoomBinding
import com.qiscus.sdk.chat.core.data.model.QiscusAccount
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
    private var qiscusChatRoom: QiscusChatRoom = QiscusChatRoom()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getParcelableExtra<QiscusAccount>("user")

        QiscusApi.getInstance().chatUser(id?.email, null)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            Log.e("TAG", it.name)
                            qiscusChatRoom = it
                            sendMessage()
                        },
                        { t: Throwable? ->
                            Log.e("TAG", t?.message, t)
                        }
                )




    }

    private fun sendMessage(){
        val comment = QiscusComment.generateMessage(qiscusChatRoom.id, "test")
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