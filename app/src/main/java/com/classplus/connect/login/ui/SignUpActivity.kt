package com.classplus.connect.login.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.classplus.connect.R

class SignUpActivity : AppCompatActivity() {
    companion object{
        fun start(context: Context){
            Intent(context, SignUpActivity::class.java).also {
                context.startActivity(it)
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
    }
}