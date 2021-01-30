package com.doniapr.chatapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.doniapr.chatapp.databinding.ActivityLoginBinding
import com.doniapr.chatapp.utils.ParamPreferences
import com.qiscus.sdk.chat.core.QiscusCore
import com.qiscus.sdk.chat.core.data.model.QiscusAccount

class LoginActivity : AppCompatActivity() {
    private val TAG = LoginActivity::class.simpleName
    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences
    private var email = ""
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        sharedPreferences = getSharedPreferences(
            ParamPreferences.PREF_NAME, Context.MODE_PRIVATE)

        val existingUser: String? = sharedPreferences.getString(ParamPreferences.KEY_EMAIL, "")
        if (existingUser != null && existingUser.isNotEmpty()){
            openMainActivity()
        }

        binding.btnLogin.setOnClickListener {
            if (QiscusCore.hasSetupAppID()) {
                if (isValidInput()){
                    QiscusCore.setUser(email, password)
                        .save(object : QiscusCore.SetUserListener{
                            override fun onSuccess(qiscusAccount: QiscusAccount?) {
                                successLogin(qiscusAccount)
                            }

                            override fun onError(throwable: Throwable?) {
                                Log.e(TAG, throwable?.message, throwable)
                                errorLogin(throwable?.message!!)
                            }
                        })
                }
            } else {
                Toast.makeText(this, "App ID is not setup properly", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun successLogin(qiscusAccount: QiscusAccount?){
        if (qiscusAccount != null){
            with (sharedPreferences.edit()) {
                putString(ParamPreferences.KEY_USERNAME, qiscusAccount.username)
                putString(ParamPreferences.KEY_EMAIL, qiscusAccount.email)
                putString(ParamPreferences.KEY_TOKEN, qiscusAccount.token)
                putInt(ParamPreferences.KEY_ID, qiscusAccount.id)
                apply()
            }
            openMainActivity()
        }
    }

    private fun errorLogin(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun openMainActivity(){
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
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

    override fun onDestroy() {
        super.onDestroy()
    }

}