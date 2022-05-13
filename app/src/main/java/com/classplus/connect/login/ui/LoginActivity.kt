package com.classplus.connect.login.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.classplus.connect.R
import com.classplus.connect.base.ViewModelFactory
import com.classplus.connect.login.data.api.LoginApiService
import com.classplus.connect.login.data.model.GetOtpResponse
import com.classplus.connect.login.data.model.OtpVerifyData
import com.classplus.connect.login.data.repository.LoginDataRepository
import com.classplus.connect.login.viewmodel.LoginViewModel
import com.classplus.connect.network.RetrofitBuilder
import com.gauravposwal.testapplication.util.Status
import com.gauravposwal.testapplication.util.hide
import com.gauravposwal.testapplication.util.show
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.app_progress_bar.progressBar

class LoginActivity : AppCompatActivity() {
    private var isOtpReceived: Boolean = false
    lateinit var viewModel: LoginViewModel

    companion object {
        fun start(context: Context) {
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
//        verifyOtp("2188")
//        registerUser("abc@classplus.co", "Gaurav Poswal")
    }

    private fun registerUser(email: String, name: String) {
        viewModel.registerUser(name, email)
    }

    private fun verifyOtp(otp: String) {
        viewModel.verifyOtp(otp)
    }

    private fun setUpObservers() {
        llProceed.setOnClickListener{
            if(isOtpReceived){
                if(etOTP.text.toString().trim().isNotEmpty())
                    verifyOtp(etOTP.text.toString().trim())
                else
                    Toast.makeText(this, "Please enter Otp", Toast.LENGTH_SHORT).show()
            }
            else {
                if(etMobile.text.toString().trim().isNotEmpty())
                    getOtpForMobile(etMobile.text.toString().trim())
                else
                    Toast.makeText(this, "Please enter Mobile Number", Toast.LENGTH_SHORT).show()
            }

        }
        viewModel.getOtpResponse.observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        progressBar.hide()
                        handleSuccess(resource.data)
                    }
                    Status.ERROR -> {
                        progressBar.hide()
                        Toast.makeText(this, it.data?.message ?: "Some Error Ocurred!", Toast.LENGTH_SHORT).show()
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
                        if(resource.data?.data?.exists == 1){
                            WebViewActivity.startActivity(this, getFormattedUrl(resource.data.data))
                        }
                        else {
                            Toast.makeText(
                                this,
                                "Registration flow is under dev...",
                                Toast.LENGTH_SHORT
                            ).show()
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

        viewModel.registerUserResponse.observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        progressBar.hide()
                        Toast.makeText(this, "Api hit Successfully", Toast.LENGTH_SHORT).show()
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

    private fun getFormattedUrl(data: OtpVerifyData?) = "${data?.landingUrl}?token=${data?.token}"

    private fun handleSuccess(data: GetOtpResponse?) {
        data?.let {
            viewModel.sessionId = data.data.sessionId
            llProceed.text = "Verify OTP"
            isOtpReceived = true
        }
    }

    private fun initProperties() {
        val listingApiService = RetrofitBuilder.provideService(LoginApiService::class.java) as LoginApiService
        val factory = ViewModelFactory(LoginDataRepository(listingApiService))
        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]
    }

    private fun getOtpForMobile(mobileNo: String) {
        viewModel.getOtpWithMobile(mobileNo)
    }
}