package com.doniapr.chatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.doniapr.chatapp.databinding.ActivityChatRoomBinding
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

        val accountAdapter = AccountAdapter()

        QiscusApi.getInstance().getUsers("")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { qiscusAccounts: List<QiscusAccount>? ->
                    Log.e("TAG", qiscusAccounts.toString())
                    if (qiscusAccounts != null) {
                        accountAdapter.setData(qiscusAccounts)
                    }
                },
                { t: Throwable? -> Log.e("TAG", t?.message, t) })

        with(binding.rvUserList){
            layoutManager = LinearLayoutManager(this@UserListActivity, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter = accountAdapter
        }
    }
}