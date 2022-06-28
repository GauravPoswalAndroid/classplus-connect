package com.fankonnect.app.login.ui

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.util.Patterns
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.fankonnect.app.R
import com.fankonnect.app.base.ViewModelFactory
import com.fankonnect.app.login.data.api.LoginApiService
import com.fankonnect.app.login.data.model.OtpVerifyData
import com.fankonnect.app.login.data.repository.LoginDataRepository
import com.fankonnect.app.login.viewmodel.SignUpViewModel
import com.fankonnect.app.network.RetrofitBuilder
import com.fankonnect.app.util.SharedPreferenceHelper
import com.fankonnect.app.util.Status
import com.fankonnect.app.util.hide
import com.fankonnect.app.util.show
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.activity_signup.toolbar

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
        setUpToolbar()
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_left_black)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.create_your_account)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initProperties() {
        val listingApiService = RetrofitBuilder.provideService(LoginApiService::class.java) as LoginApiService
        val factory = ViewModelFactory(LoginDataRepository(listingApiService))
        viewModel = ViewModelProvider(this, factory)[SignUpViewModel::class.java]
        viewModel.userMobile = intent.getStringExtra(MOBILE) ?: ""
        viewModel.otp = intent.getStringExtra(OTP) ?: ""
        viewModel.sessionId = intent.getStringExtra(SESSION_ID) ?: ""

        val mobileNo = "+91-${viewModel.userMobile}"
        val credentialColor = "<font color='#000000'>$mobileNo</font>"

    }

    private fun setObservers() {
        viewModel.registerUserResponse.observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        progressBar.hide()
                        resource.data?.data?.let { data ->
                            SharedPreferenceHelper.saveToken(this, data.token)
                            SharedPreferenceHelper.saveLandingUrl(this, data.landingUrl)
                            WebViewActivity.startActivity(this, getFormattedUrl(data))
                            finishAffinity()
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

    private fun isValidName(name: String): Boolean {
        return name.isNotEmpty() && name.length > 2 && !name.toCharArray()[0].isDigit()
    }

    private fun isEmailValid(email: String?): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun getFormattedUrl(data: OtpVerifyData) = "${data.landingUrl}${data.token}"

}
