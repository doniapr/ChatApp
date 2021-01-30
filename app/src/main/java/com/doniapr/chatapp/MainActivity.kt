package com.doniapr.chatapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.doniapr.chatapp.databinding.ActivityMainBinding
import com.doniapr.chatapp.group.CreateGroupActivity
import com.qiscus.sdk.chat.core.QiscusCore
import com.qiscus.sdk.chat.core.data.model.QiscusAccount
import com.qiscus.sdk.chat.core.data.model.QiscusChatRoom
import com.qiscus.sdk.chat.core.data.remote.QiscusApi
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Action1
import rx.schedulers.Schedulers


class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.simpleName
    private lateinit var binding: ActivityMainBinding
    private var isFabVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val chatRoomAdapter = ChatRoomAdapter()
        binding.pbMain.visibility = View.VISIBLE

        QiscusApi.getInstance()
            .getAllChatRooms(true, false, false,0,100)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    if (it != null) {
                        chatRoomAdapter.setData(it)
                        binding.pbMain.visibility = View.GONE
                    }
                },
                { t: Throwable? -> Log.e(TAG, t?.message, t) }
            )

        with(binding.rvChatRoomList){
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter = chatRoomAdapter
        }

        binding.fabAdd.setOnClickListener {
            if (isFabVisible){
                binding.fabAddGroupChat.visibility = View.GONE
                binding.fabAddPersonalChat.visibility = View.GONE
                isFabVisible = !isFabVisible
            } else {
                binding.fabAddGroupChat.visibility = View.VISIBLE
                binding.fabAddPersonalChat.visibility = View.VISIBLE
                isFabVisible = !isFabVisible
            }
        }

        binding.fabAddPersonalChat.setOnClickListener {
            val intent = Intent(applicationContext, UserListActivity::class.java)
            startActivity(intent)
        }

        binding.fabAddGroupChat.setOnClickListener {
            val intent = Intent(applicationContext, CreateGroupActivity::class.java)
            startActivity(intent)
        }
    }
}