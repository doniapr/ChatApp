package com.doniapr.chatapp.personalchat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.doniapr.chatapp.chatroom.ChatRoomActivity
import com.doniapr.chatapp.databinding.ActivityUserListBinding
import com.qiscus.sdk.chat.core.data.model.QiscusAccount
import com.qiscus.sdk.chat.core.data.remote.QiscusApi
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class UserListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val accountAdapter = UserListAdapter{qiscusAccount ->
            binding.rvUserList.visibility = View.GONE
            binding.pbCreateGroup.visibility = View.VISIBLE

            QiscusApi.getInstance().chatUser(qiscusAccount.email, null).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        val intent = Intent(applicationContext, ChatRoomActivity::class.java)
                        intent.putExtra("chat_room", it)
                        startActivity(intent)
                    },
                    {t ->
                        Log.e("onErrorCreateChatRoom", t.message, t)
                    }
                )
        }

        QiscusApi.getInstance().getUsers("")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { qiscusAccounts: List<QiscusAccount>? ->
                    if (qiscusAccounts != null) {
                        accountAdapter.setData(qiscusAccounts)
                    }
                },
                { t: Throwable? -> Log.e("onErrorGetUser", t?.message, t) })

        with(binding.rvUserList) {
            layoutManager =
                LinearLayoutManager(this@UserListActivity, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter = accountAdapter
        }
    }
}