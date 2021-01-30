package com.doniapr.chatapp

import androidx.lifecycle.ViewModel
import com.qiscus.sdk.chat.core.QiscusCore

class LoginViewModel: ViewModel() {
    fun login(email: String, password: String){
        QiscusCore.setUser(email, password)
    }
}