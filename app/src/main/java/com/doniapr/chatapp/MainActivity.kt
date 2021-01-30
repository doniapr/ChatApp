package com.doniapr.chatapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.doniapr.chatapp.databinding.ActivityMainBinding
import com.qiscus.sdk.chat.core.QiscusCore
import com.qiscus.sdk.chat.core.data.model.QiscusAccount
import com.qiscus.sdk.chat.core.data.model.QiscusChatRoom
import com.qiscus.sdk.chat.core.data.remote.QiscusApi
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Action1
import rx.schedulers.Schedulers


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val chatRoomAdapter = ChatRoomAdapter()

        QiscusApi.getInstance()
            .getAllChatRooms(true, false, false,0,100)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Log.e("TAG", it.toString())
                    if (it != null) {
                        chatRoomAdapter.setData(it)
                    }
                },
                { t: Throwable? -> Log.e("TAG", t?.message, t) }
            )

        with(binding.rvChatRoomList){
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter = chatRoomAdapter
        }

        binding.fabAddChatRoom.setOnClickListener {
            val intent = Intent(applicationContext, UserListActivity::class.java)
            startActivity(intent)
        }
    }
}