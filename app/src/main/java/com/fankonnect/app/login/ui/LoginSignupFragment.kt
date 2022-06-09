package com.fankonnect.app.login.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fankonnect.app.R
import com.fankonnect.app.base.ViewModelFactory
import com.fankonnect.app.login.data.api.LoginApiService
import com.fankonnect.app.login.data.model.GetOtpResponse
import com.fankonnect.app.login.data.repository.LoginDataRepository
import com.fankonnect.app.login.viewmodel.LoginViewModel
import com.fankonnect.app.network.RetrofitBuilder
import com.fankonnect.app.util.Status
import com.fankonnect.app.util.hide
import com.fankonnect.app.util.show
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
        ll_proceed.enableDisableButton(false)
        et_mobile.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                ll_proceed.enableDisableButton(s.toString().length >= 10)
            }

        })
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
                        if(viewModel.isOtpPageShowing) viewModel.showSendAgainProgress.value = false
                    }
                    Status.ERROR -> {
                        if(viewModel.isOtpPageShowing) viewModel.showSendAgainProgress.value = false
                        ll_proceed.progress_bar.hide()
                        Toast.makeText(context, it.message ?: "Some Error Occurred!", Toast.LENGTH_SHORT).show()
                    }
                    Status.LOADING -> {
                        ll_proceed.progress_bar.show()
                        if(viewModel.isOtpPageShowing) viewModel.showSendAgainProgress.value = true
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

    companion object {
        fun newInstance(): LoginSignupFragment {
            return LoginSignupFragment()
        }

    }
}