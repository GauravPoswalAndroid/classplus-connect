package com.classplus.connect.views

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.classplus.connect.R

class SplashActivity : AppCompatActivity() {
    companion object {
        const val DESTINATION_URL = "https://web.classplusapp.com/"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler(Looper.getMainLooper()).postDelayed({
            WebViewActivity.startActivity(this, DESTINATION_URL)
            finish()
        }, 3000)
    }
}