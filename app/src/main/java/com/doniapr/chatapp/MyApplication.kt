package com.doniapr.chatapp

import androidx.multidex.MultiDexApplication
import com.qiscus.sdk.chat.core.QiscusCore

class MyApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        QiscusCore.setup(this, "kong-el3brpcf0rvcovqx")

    }
}