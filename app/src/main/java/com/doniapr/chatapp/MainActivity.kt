package com.doniapr.chatapp

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
    private var userid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val qiscusAccount = intent.getParcelableExtra<QiscusAccount>("TAG")

        val accountAdapter = AccountAdapter()

        QiscusApi.getInstance().getUsers("")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { qiscusAccounts: List<QiscusAccount>? ->
                    Log.e("TAG", qiscusAccounts.toString())
                    if (qiscusAccounts != null) {
                        accountAdapter.setData(qiscusAccounts)
                        userid = qiscusAccounts[0].id.toString()
                    }
                },
                { t: Throwable? -> Log.e("TAG", t?.message, t) })

        with(binding.rvUserList){
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter = accountAdapter
        }
    }

    fun test(){
        QiscusApi.getInstance()
            .chatUser(userid, null)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ Log.e("TAG", it.name) }, { Log.e("TAG", it.message, it) })

        var qiscusChatRooms = QiscusCore.getDataStore().getChatRooms(100)

        Log.e("TAG", qiscusChatRooms.toString())
    }
}