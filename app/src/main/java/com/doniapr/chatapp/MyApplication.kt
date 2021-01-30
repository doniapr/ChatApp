package com.doniapr.chatapp

import android.app.Application
import androidx.multidex.MultiDexApplication
import com.doniapr.chatapp.di.viewModelModule
import com.qiscus.sdk.chat.core.QiscusCore
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MyApplication: MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        QiscusCore.setup(this, "kong-el3brpcf0rvcovqx")

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@MyApplication)
            modules(
                listOf(
                    viewModelModule
                )
            )
        }
    }
}