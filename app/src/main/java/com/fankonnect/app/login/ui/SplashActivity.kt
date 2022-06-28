package com.fankonnect.app.login.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.fankonnect.app.R
import com.fankonnect.app.util.SharedPreferenceHelper
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Glide.with(this).load(R.drawable.splash_logo).into(splashAnim)
        Handler(Looper.getMainLooper()).postDelayed({
            val token: String? = SharedPreferenceHelper.getToken(this)
            val landingUrl: String? = SharedPreferenceHelper.getLandingUrl(this)
            if (true == token?.isNotEmpty() && true == landingUrl?.isNotEmpty())
                WebViewActivity.startActivity(this, "$landingUrl$token")
            else
                LaunchCarouselActivity.startActivity(this)
            finish()
        }, 3000)
    }
}