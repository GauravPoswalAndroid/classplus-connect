package com.fankonnect.app.login.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Patterns
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
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
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.app_progress_bar.*

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
        setUpViews()
        setUpToolbar()
    }

    private fun setUpViews() {
        tvSignUp.setOnClickListener {
            if (isValidName(etName.text.toString().trim())) {
                tvErrorName.hide()
                viewModel.name = etName.text.toString().trim()

                if (isEmailValid(etEmail.text.toString().trim())) {
                    tvErrorEmail.hide()
                    viewModel.email = etEmail.text.toString().trim()
                    tvErrorName.hide()
                    tvErrorEmail.hide()
                    viewModel.registerUser()
                } else {
                    tvErrorEmail.show()
                    tvErrorEmail.text = getString(R.string.email_validation_err_msg)
                }
            } else {
                tvErrorName.show()
                tvErrorName.text = getString(R.string.name_validation_err_msg)
            }
        }
        val textWatcher = object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (etName.text.toString().isEmpty() || etEmail.text.toString().isEmpty()) {
                    tvSignUp.background = ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.bg_round_corners_primary_light,
                        theme
                    )
                } else tvSignUp.background = ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.bg_button_click_color_primary,
                    theme
                )
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

        }
        etName.addTextChangedListener(textWatcher)
        etEmail.addTextChangedListener(textWatcher)
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
