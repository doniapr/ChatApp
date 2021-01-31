package com.doniapr.chatapp.chatroom

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.doniapr.chatapp.databinding.ActivityChatRoomBinding
import com.hbisoft.pickit.PickiT
import com.hbisoft.pickit.PickiTCallbacks
import com.qiscus.sdk.chat.core.data.model.QiscusChatRoom
import com.qiscus.sdk.chat.core.data.model.QiscusComment
import com.qiscus.sdk.chat.core.data.remote.QiscusApi
import com.qiscus.sdk.chat.core.event.QiscusCommentReceivedEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File

class ChatRoomActivity : AppCompatActivity(), PickiTCallbacks {
    private lateinit var binding: ActivityChatRoomBinding
    private var qiscusChatRoom: QiscusChatRoom? = QiscusChatRoom()
    private var listComment = ArrayList<QiscusComment>()
    private val chatAdapter = ChatAdapter()
    private lateinit var pickiT: PickiT
    private var isAttachmentOpen = false
    private var attachmentType = ""

    private val SELECT_REQUEST = 123
    private val PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE = 234
    private val MIME_TYPE_IMAGE = "image/*"
    private val MIME_TYPE_VIDEO = "video/*";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pickiT = PickiT(applicationContext, this, this)

        qiscusChatRoom = intent.getParcelableExtra("chat_room")

        supportActionBar?.title = qiscusChatRoom?.name
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        QiscusApi.getInstance().getChatRoomInfo(qiscusChatRoom!!.id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    getChatHistory(it.id)
                },
                { t: Throwable? ->
                    Log.e("onErrorGetChatRoom", t?.message, t)
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

        binding.btnAttachment.setOnClickListener {
            if (isAttachmentOpen) {
                binding.llAttachment.visibility = View.GONE
                isAttachmentOpen = !isAttachmentOpen
            } else {
                binding.llAttachment.visibility = View.VISIBLE
                isAttachmentOpen = !isAttachmentOpen
            }
        }

        binding.btnAttachImage.setOnClickListener {
            attachmentType = MIME_TYPE_IMAGE
            openGallery()
        }
        binding.btnAttachVideo.setOnClickListener {
            attachmentType = MIME_TYPE_VIDEO
            openGallery()
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
                    Log.e("onErrorChatHistory", t?.message, t)
                }
            )
    }

    private fun sendMessage(comment: QiscusComment) {
        QiscusApi.getInstance().sendMessage(comment)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Log.i("onSuccessSendMessage", it.toString())
                },
                {
                    Log.e("onErrorSendMessage", it.message, it)
                }
            )
    }

    @Subscribe
    fun onReceiveComment(event: QiscusCommentReceivedEvent) {
        listComment.add(event.qiscusComment)
        chatAdapter.setData(listComment)
        binding.rvChat.scrollToPosition(listComment.size - 1)
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        pickiT.deleteTemporaryFile(applicationContext)
    }

    private fun openGallery() {
        if (checkSelfPermission()) {
            val intent: Intent =
                if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                    Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                } else {
                    Intent(Intent.ACTION_PICK, MediaStore.Video.Media.INTERNAL_CONTENT_URI)
                }
            intent.type = attachmentType
            intent.action = Intent.ACTION_GET_CONTENT
            intent.putExtra("return-data", true)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivityForResult(
                intent,
                SELECT_REQUEST
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_REQUEST && resultCode == RESULT_OK && data != null) {
            pickiT.getPath(data.data, Build.VERSION.SDK_INT)
        }
    }

    private fun checkSelfPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //  Permissions was granted, open the gallery
                openGallery()
            } else {
                Toast.makeText(
                    applicationContext,
                    "No permission for " + Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun PickiTonUriReturned() {
        TODO("Not yet implemented")
    }

    override fun PickiTonStartListener() {
        TODO("Not yet implemented")
    }

    override fun PickiTonProgressUpdate(progress: Int) {
        TODO("Not yet implemented")
    }

    override fun PickiTonCompleteListener(
        path: String?,
        wasDriveFile: Boolean,
        wasUnknownProvider: Boolean,
        wasSuccessful: Boolean,
        Reason: String?
    ) {
        if (wasSuccessful) {
            val file = File(path)
            val comment = qiscusChatRoom?.let {
                QiscusComment.generateFileAttachmentMessage(
                    it.id,
                    path,
                    "",
                    file.name
                )
            }
            QiscusApi.getInstance()
                .sendFileMessage(comment, file) { total -> Log.e("Onprogress", total.toString()) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        Log.e("OnSuccessSendMessage", it.toString())
                    },
                    {
                        Log.e("OnErrorSendMessage", it.message, it)
                    }
                )
        }
    }
}