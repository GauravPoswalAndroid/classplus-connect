package com.fankonnect.app.login.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.*
import android.text.Annotation
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.fankonnect.app.R
import com.fankonnect.app.base.ViewModelFactory
import com.fankonnect.app.login.data.api.LoginApiService
import com.fankonnect.app.login.data.model.GetOtpResponse
import com.fankonnect.app.login.data.model.LoginState
import com.fankonnect.app.login.data.model.OtpVerifyData
import com.fankonnect.app.login.data.repository.LoginDataRepository
import com.fankonnect.app.login.viewmodel.LoginViewModel
import com.fankonnect.app.network.RetrofitBuilder
import com.fankonnect.app.util.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.app_progress_bar.*
import java.text.DecimalFormat
import java.text.NumberFormat

class LoginActivity : AppCompatActivity() {
    lateinit var viewModel: LoginViewModel
    private var currentLoginState: LoginState = LoginState.MobileState()
    private val countDownTimer = object : CountDownTimer(30000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            val f: NumberFormat = DecimalFormat("00")
            val min = millisUntilFinished / 60000 % 60
            val sec = millisUntilFinished / 1000 % 60
            "${f.format(min)}:${f.format(sec)}".also { tvOtpTimer.text = it }
        }

        override fun onFinish() {
            tvSendOtpAgain.show()
            tvOtpTimerLabel.hide()
            tvOtpTimer.hide()
            tvOtpTimer.text = getString(R.string.otp_timer_text)
        }
    }

    companion object {
        private const val TAG = "LoginActivity"
        fun startActivity(context: Context) {
            Intent(context, LoginActivity::class.java).apply {}
                .also {
                    context.startActivity(it)
                }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initProperties()
        setUpObservers()
        setUpToolbar()
        setTermsConditionsText()
        setUpViews()
        updateLoginState(LoginState.MobileState())
    }

    private fun setUpViews() {
        etMobile.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                enableDisableButton(s.toString().length < 10)
            }

        })
        tvLoginSignUp.setOnClickListener {
            when (currentLoginState) {
                is LoginState.MobileState -> {
                    if (isMobileValid()) {
                        viewModel.getOtpWithMobile((etMobile.text.toString()))
                    }
                }
                is LoginState.OtpState -> {
                    if (isOtpValid()) {
                        viewModel.verifyOtp((otpView.text.toString()))
                    }
                }
            }
        }
        otpView.setOtpCompletionListener {
            enableDisableButton(false)
            tvErrorOtp.hide()
            if (currentLoginState is LoginState.OtpState && isOtpValid()) {
                viewModel.verifyOtp((it))
            }
        }
        otpView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                enableDisableButton(s.toString().length < 4)
            }

        })
        tvEdit.setOnClickListener {
            updateLoginState(LoginState.MobileState())
        }
        tvSendOtpAgain.setOnClickListener{
            if (isMobileValid()) {
                countDownTimer.onFinish()
                viewModel.getOtpWithMobile((etMobile.text.toString()))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer.cancel()
    }

    private fun isMobileValid() =
        if (etMobile.text.toString().isEmpty() || etMobile.text.toString().length < 10) {
            tvErrorMobile.show()
            false
        } else {
            tvErrorMobile.hide()
            true
        }

    private fun isOtpValid() =
        if (otpView.text.toString().isEmpty() || otpView.text.toString().length < 4) {
            tvErrorOtp.show()
            false
        } else {
            tvErrorOtp.hide()
            true
        }


    private fun enableDisableButton(shouldBeInActive: Boolean) {
        tvLoginSignUp.background = ResourcesCompat.getDrawable(
            resources,
            if (shouldBeInActive) R.drawable.bg_round_corners_primary_light else R.drawable.bg_button_click_color_primary,
            theme
        )
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_left_black)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.login_or_signup)
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

    private fun setTermsConditionsText() {
        val tncText = getText(R.string.str_terms_and_conditions_new) as SpannedString
        val ssb = SpannableStringBuilder(tncText)
        val annotations = tncText.getSpans(0, tncText.length, Annotation::class.java)
        if (annotations != null && annotations.isNotEmpty()) {
            for (annotation in annotations) {
                if (annotation.key == "click") {
                    ssb.setSpan(
                        object : ClickableSpan() {
                            override fun onClick(widget: View) {
                                Utility.openWebPage(
                                    this@LoginActivity,
                                    AppConstants.FAN_CONNECT_PRIVACY_POLICY_URL
                                )
                            }

                            override fun updateDrawState(paint: TextPaint) {
                                super.updateDrawState(paint)
                                paint.isUnderlineText = true
                                paint.color =
                                    ContextCompat.getColor(this@LoginActivity, R.color.accent_color)
                            }
                        },
                        tncText.getSpanStart(annotation),
                        tncText.getSpanEnd(annotation),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
        }
        tvTermsConditions.text = ssb
        tvTermsConditions.movementMethod = LinkMovementMethod.getInstance()
        tvTermsConditions.highlightColor = Color.TRANSPARENT
    }

    private fun setUpObservers() {
        viewModel.getOtpResponse.observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        progressBar.hide()
                        handleSuccess(resource.data)
                    }
                    Status.ERROR -> {
                        progressBar.hide()
                        Toast.makeText(
                            this,
                            it.message ?: "Some Error Occurred!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    Status.LOADING -> {
                        progressBar.show()
                    }
                }
            }
        }
        viewModel.verifyOtpResponse.observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        progressBar.hide()
                        if (resource.data?.data?.exists == 1) {
                            SharedPreferenceHelper.saveToken(
                                this,
                                resource.data.data.token
                            )
                            SharedPreferenceHelper.saveLandingUrl(
                                this,
                                resource.data.data.landingUrl
                            )
                            WebViewActivity.startActivity(
                                this,
                                getFormattedUrl(resource.data.data)
                            )
                            finish()
                        } else {
                            SignUpActivity.start(
                                this,
                                viewModel.userMobile,
                                viewModel.otp,
                                viewModel.sessionId
                            )
                            updateLoginState(LoginState.MobileState())
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

    private fun getFormattedUrl(data: OtpVerifyData?) = "${data?.landingUrl}${data?.token}"

    private fun handleSuccess(data: GetOtpResponse?) {
        data?.let {
            viewModel.sessionId = data.data.sessionId
            updateLoginState(LoginState.OtpState())
            countDownTimer.start()
        }
    }

    private fun updateLoginState(state: LoginState) {
        currentLoginState = state
        when (state) {
            is LoginState.MobileState -> {
                tvEnterMobile.text = getString(R.string.enter_your_mobile_number)
                llEnterNo.background = ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.shape_rectangle_filled_white_outline_gray_r6,
                    theme
                )
                etMobile.isEnabled = true
                tvEdit.hide()
                tvEnterOtp.hide()
                otpView.hide()
                otpView.setText("")
                tvOtpTimerLabel.hide()
                tvOtpTimer.hide()
                etMobile.requestFocus()
                countDownTimer.cancel()
                tvSendOtpAgain.hide()
                tvLoginSignUp.text = getString(R.string.proceed)
                enableDisableButton(false)
            }
            is LoginState.OtpState -> {
                tvEnterMobile.text = getString(R.string.sent_otp_to_number)
                llEnterNo.background = ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.bg_round_corners_grey,
                    theme
                )
                etMobile.isEnabled = false
                tvSendOtpAgain.hide()
                tvEdit.show()
                tvEnterOtp.show()
                otpView.setText("")
                otpView.show()
                tvOtpTimerLabel.show()
                tvOtpTimer.show()
                tvLoginSignUp.text = getString(R.string.verify)
                enableDisableButton(true)
            }
        }
    }


    private fun initProperties() {
        val listingApiService =
            RetrofitBuilder.provideService(LoginApiService::class.java) as LoginApiService
        val factory = ViewModelFactory(LoginDataRepository(listingApiService))
        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]
    }
}