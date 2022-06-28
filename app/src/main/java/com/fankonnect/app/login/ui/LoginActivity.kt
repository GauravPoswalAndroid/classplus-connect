package com.fankonnect.app.login.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
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
import com.fankonnect.app.login.data.repository.LoginDataRepository
import com.fankonnect.app.login.viewmodel.LoginViewModel
import com.fankonnect.app.network.RetrofitBuilder
import com.fankonnect.app.util.*
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    lateinit var viewModel: LoginViewModel

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
    }

    private fun setUpViews() {
        etMobile.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                enableDisableButton(s.toString().length >= 10)
            }

        })
        tvLoginSignUp.setOnClickListener {
            if(isMobileValid()){
                viewModel.getOtpWithMobile((etMobile.text.toString()))
            }
        }
    }

    private fun isMobileValid() =
        if (etMobile.text.toString().isEmpty() || etMobile.text.toString().length > 10) {
            tvErrorMobile.show()
            false
        } else {
            tvErrorMobile.hide()
            true
        }


    private fun enableDisableButton(shouldBeActive: Boolean) {
        if (shouldBeActive) {
            tvLoginSignUp.background = ResourcesCompat.getDrawable(
                resources,
                R.drawable.bg_button_click_color_primary,
                theme
            )
        } else {
            tvLoginSignUp.background = ResourcesCompat.getDrawable(
                resources,
                R.drawable.bg_round_corners_primary_light,
                theme
            )
        }
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
                        handleSuccess(resource.data)
                        Toast.makeText(this, it.message ?: "OTP Sent Successfully", Toast.LENGTH_SHORT).show()
//                        if(viewModel.isOtpPageShowing) viewModel.showSendAgainProgress.value = false
                    }
                    Status.ERROR -> {
//                        if(viewModel.isOtpPageShowing) viewModel.showSendAgainProgress.value = false
                        Toast.makeText(this, it.message ?: "Some Error Occurred!", Toast.LENGTH_SHORT).show()
                    }
                    Status.LOADING -> {
//                        if(viewModel.isOtpPageShowing) viewModel.showSendAgainProgress.value = true
                    }
                }
            }
        }
    }

    private fun handleSuccess(data: GetOtpResponse?) {
        data?.let {
            viewModel.sessionId = data.data.sessionId
            viewModel.updatePagerNavToPage.value = 1
        }
    }


    private fun initProperties() {
        val listingApiService =
            RetrofitBuilder.provideService(LoginApiService::class.java) as LoginApiService
        val factory = ViewModelFactory(LoginDataRepository(listingApiService))
        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]
    }
}