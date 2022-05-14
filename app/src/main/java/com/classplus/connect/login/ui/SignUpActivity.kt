package com.classplus.connect.login.ui

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.classplus.connect.R
import com.classplus.connect.base.ViewModelFactory
import com.classplus.connect.login.data.api.LoginApiService
import com.classplus.connect.login.data.model.OtpVerifyData
import com.classplus.connect.login.data.repository.LoginDataRepository
import com.classplus.connect.login.viewmodel.SignUpViewModel
import com.classplus.connect.network.RetrofitBuilder
import com.classplus.connect.util.Status
import com.classplus.connect.util.hide
import com.classplus.connect.util.show
import kotlinx.android.synthetic.main.activity_signup.*
class SignUpActivity : AppCompatActivity() {
    private lateinit var viewModel: SignUpViewModel

    companion object {
        private const val MOBILE = "MOBILE"
        private const val OTP = "OTP"
        private const val SESSION_ID = "SESSION_ID"
        fun start(
            context: Context,
            mobile: String,
            otp: String,
            sessionId: String,
        ) {
            Intent(context, SignUpActivity::class.java).apply {
                putExtra(MOBILE, mobile)
                putExtra(OTP, otp)
                putExtra(SESSION_ID, sessionId)
            }.also {
                context.startActivity(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        initProperties()
        setObservers()
    }

    private fun initProperties() {
        val listingApiService = RetrofitBuilder.provideService(LoginApiService::class.java) as LoginApiService
        val factory = ViewModelFactory(LoginDataRepository(listingApiService))
        viewModel = ViewModelProvider(this, factory)[SignUpViewModel::class.java]
        viewModel.userMobile = intent.getStringExtra(MOBILE) ?: ""
        viewModel.otp = intent.getStringExtra(OTP) ?: ""
        viewModel.sessionId = intent.getStringExtra(SESSION_ID) ?: ""

        val mobileNo = "91-${viewModel.userMobile}"
        val credentialColor = "<font color='#000000'>$mobileNo</font>"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            tvLoggingMessage.text = Html.fromHtml(getString(R.string.you_are_logging_in_using, credentialColor), Html.FROM_HTML_MODE_LEGACY)
        else
            @Suppress("DEPRECATION")
            tvLoggingMessage.text = Html.fromHtml(getString(R.string.you_are_logging_in_using, credentialColor))

    }

    private fun setObservers() {
        iv_back.setOnClickListener{
            finish()
        }
        btnNext.setOnClickListener {
            if (etName.text.toString().isNotEmpty()) {
                viewModel.name = etName.text.toString()

                if (etEmail.text.toString().isNotEmpty()) {
                    viewModel.email = etEmail.text.toString()

                    if (etTelegram.text.toString().isNotEmpty()) {
                        viewModel.tgUserName = etTelegram.text.toString()
                        viewModel.registerUser()
                    } else {
                        Toast.makeText(this, "Please enter a valid Telegram Username", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Please enter valid Email ID", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please Enter Name ", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.registerUserResponse.observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        progressBar.hide()
                        resource.data?.data?.let { data ->
                            WebViewActivity.startActivity(this, getFormattedUrl(data))
                        }
                    }
                    Status.ERROR -> {
                        progressBar.hide()
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    }
                    Status.LOADING -> {
                        progressBar.show()

                    }
                }
            }
        }
    }

    private fun getFormattedUrl(data: OtpVerifyData) = "${data.landingUrl}${data.token}"

}
