package com.classplus.connect.login.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.classplus.connect.R
import com.classplus.connect.login.data.model.OtpVerifyData
import com.classplus.connect.login.viewmodel.LoginViewModel
import com.classplus.connect.util.SharedPreferenceHelper
import com.classplus.connect.util.Status
import com.classplus.connect.util.hide
import com.classplus.connect.util.show
import kotlinx.android.synthetic.main.fragment_login_signup.*
import kotlinx.android.synthetic.main.fragment_otp.*
import kotlinx.android.synthetic.main.loading_button.view.*

class OtpFragment : Fragment() {

    lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_otp, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[LoginViewModel::class.java]
        verify_otp.enableDisableButton(false)
        setObservers()
    }
    private fun setObservers() {
        verify_otp.setOnClickListener {
            viewModel.verifyOtp(et_enter_otp.text.toString())
        }
        et_enter_otp.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                verify_otp.enableDisableButton(s.toString().length >= 4)
            }

        })
        iv_back.setOnClickListener {
            viewModel.updatePagerNavToPage.value = 0
        }
        tv_send_again.setOnClickListener {
            viewModel.getOtpWithMobile(viewModel.userMobile)
        }



        viewModel.isInvalidOtp.observe(viewLifecycleOwner) {
            tv_send_again.visibility = View.GONE
            tv_otp_error.visibility = View.VISIBLE
            tv_otp_error.text = "*Invalid OTP. Please check and try again."
        }
        viewModel.verifyOtpResponse.observe(viewLifecycleOwner) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        verify_otp.progress_bar.hide()
                        if(resource.data?.data?.exists == 1){
                            SharedPreferenceHelper.saveToken(requireContext(), resource.data.data.token)
                            SharedPreferenceHelper.saveLandingUrl(requireContext(), resource.data.data.landingUrl)
                            WebViewActivity.startActivity(requireActivity(), getFormattedUrl(resource.data.data))
                            requireActivity().finish()
                        }
                        else {
                            SignUpActivity.start(
                                requireContext(),
                                viewModel.userMobile,
                                viewModel.otp,
                                viewModel.sessionId
                            )
                        }

                    }
                    Status.ERROR -> {
                        verify_otp.progress_bar.hide()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    Status.LOADING -> {
                        verify_otp.progress_bar.show()

                    }
                }
            }
        }
    }

    private fun getFormattedUrl(data: OtpVerifyData?) = "${data?.landingUrl}${data?.token}"

    companion object {

        fun newInstance(): OtpFragment {
            return OtpFragment()
        }

    }
}