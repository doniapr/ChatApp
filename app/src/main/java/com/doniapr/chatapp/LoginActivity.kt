package com.doniapr.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.doniapr.chatapp.databinding.ActivityLoginBinding
import com.qiscus.sdk.chat.core.QiscusCore
import com.qiscus.sdk.chat.core.data.model.QiscusAccount

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var email = ""
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnLogin.setOnClickListener {
            if (QiscusCore.hasSetupAppID()) {
                if (isValidInput()){
                    QiscusCore.setUser(email, password)
                        .save(object : QiscusCore.SetUserListener{
                            override fun onSuccess(qiscusAccount: QiscusAccount?) {
                                successLogin(qiscusAccount)
                            }

                            override fun onError(throwable: Throwable?) {
                                Log.e("TAG", throwable?.message, throwable)
                                errorLogin(throwable?.message!!)
                            }

                        })
                }
            } else {
                Toast.makeText(this, "NOO", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun successLogin(qiscusAccount: QiscusAccount?){
        if (qiscusAccount != null){
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            intent.putExtra("TAG", qiscusAccount)
            startActivity(intent)
        }

    }

    private fun errorLogin(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun isValidInput(): Boolean{
        if (binding.etLoginEmail.text.isNullOrEmpty()){
            binding.etLoginEmail.error = "Email must be set"
            binding.etLoginEmail.requestFocus()

            return false
        }
        if (binding.etLoginPassword.text.isNullOrEmpty()) {
            binding.etLoginPassword.error = "Password must be set"
            binding.etLoginPassword.requestFocus()

            return false
        }

        email = binding.etLoginEmail.text.toString()
        password = binding.etLoginPassword.text.toString()

        return true
    }

}