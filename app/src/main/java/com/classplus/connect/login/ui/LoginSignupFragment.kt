package com.classplus.connect.login.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.classplus.connect.R
import com.classplus.connect.base.ViewModelFactory
import com.classplus.connect.login.data.api.LoginApiService
import com.classplus.connect.login.data.model.GetOtpResponse
import com.classplus.connect.login.data.model.OtpVerifyData
import com.classplus.connect.login.data.repository.LoginDataRepository
import com.classplus.connect.login.viewmodel.LoginViewModel
import com.classplus.connect.network.RetrofitBuilder
import com.classplus.connect.util.Status
import com.classplus.connect.util.hide
import com.classplus.connect.util.show
import kotlinx.android.synthetic.main.fragment_login_signup.*
import kotlinx.android.synthetic.main.loading_button.view.*

class LoginSignupFragment : Fragment() {

    lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_login_signup, null, false)

        initVariables()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickListeners()
    }
    private fun initVariables() {
        val listingApiService = RetrofitBuilder.provideService(LoginApiService::class.java) as LoginApiService
        val factory = ViewModelFactory(LoginDataRepository(listingApiService))
        viewModel = ViewModelProvider(requireActivity() , factory)[LoginViewModel::class.java]
    }

    private fun clickListeners() {
        ll_proceed.setOnClickListener {
            verifyNumberOrEmail()
        }
    }

    private fun handleSuccess(data: GetOtpResponse?) {
        data?.let {
            viewModel.sessionId = data.data.sessionId
            viewModel.updatePagerNavToPage.value = 1
        }
    }

    private fun getOtpForMobile(mobileNo: String) {
        viewModel.getOtpWithMobile(mobileNo)
    }

    private fun verifyNumberOrEmail() {
        if (et_mobile.text.toString().trim().isNotEmpty())
            getOtpForMobile(et_mobile.text.toString().trim())
        else
            Toast.makeText(context, "Please enter Mobile Number", Toast.LENGTH_SHORT).show()

        viewModel.getOtpResponse.observe(requireActivity()) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        ll_proceed.progress_bar.hide()
                        handleSuccess(resource.data)
                    }
                    Status.ERROR -> {
                        ll_proceed.progress_bar.hide()
                        Toast.makeText(context, it.data?.message ?: "Some Error Ocurred!", Toast.LENGTH_SHORT).show()
                    }
                    Status.LOADING -> {
                        ll_proceed.progress_bar.show()
                    }
                }
            }
        }

        viewModel.registerUserResponse.observe(requireActivity()) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        ll_proceed.progress_bar.hide()
                        Toast.makeText(context, "Api hit Successfully", Toast.LENGTH_SHORT).show()
                    }
                    Status.ERROR -> {
                        ll_proceed.progress_bar.hide()
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    }
                    Status.LOADING -> {
                        ll_proceed.progress_bar.show()

                    }
                }
            }
        }
    }

    private fun getFormattedUrl(data: OtpVerifyData?) = "${data?.landingUrl}?token=${data?.token}"

    companion object {
        fun newInstance(): LoginSignupFragment {
            return LoginSignupFragment()
        }

    }
}