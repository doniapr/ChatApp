package com.doniapr.chatapp.group

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.doniapr.chatapp.chatroom.ChatRoomActivity
import com.doniapr.chatapp.databinding.ActivityCreateGroupBinding
import com.qiscus.sdk.chat.core.data.model.QiscusAccount
import com.qiscus.sdk.chat.core.data.remote.QiscusApi
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class CreateGroupActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateGroupBinding
    private var selectedAccount = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val accountListForGroupAdapter = AccountListForGroupAdapter{
            Log.e("SELECTED", it.username)
            selectedAccount.add(it.email.toString())
        }

        QiscusApi.getInstance().getUsers("")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { qiscusAccounts: List<QiscusAccount>? ->
                    if (qiscusAccounts != null) {
                        accountListForGroupAdapter.setData(qiscusAccounts)
                    }
                },
                { t: Throwable? -> Log.e("TAG", t?.message, t) })

        with(binding.rvUserListGroup){
            layoutManager = LinearLayoutManager(this@CreateGroupActivity, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter = accountListForGroupAdapter
        }

        binding.btnCreateGroup.setOnClickListener {
            if (binding.etGroupName.text.isNullOrEmpty()){
                binding.etGroupName.error = "Group name must be set"
                binding.etGroupName.requestFocus()

                return@setOnClickListener
            }
            val name:String = binding.etGroupName.text.toString()

            QiscusApi.getInstance().createGroupChat(name, selectedAccount, "https://d1edrlpyc25xu0.cloudfront.net/kiwari-prod/image/upload/75r6s_jOHa/1507541871-avatar-mine.png", null)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        Log.e("Create Group", it.toString())
                        val intent = Intent(applicationContext, ChatRoomActivity::class.java)
                        intent.putExtra("chat_room", it)

                        startActivity(intent)
                    },
                    { t: Throwable? ->
                        Log.e("Error Create Group", t?.message, t)
                    }
                )
        }
    }
}