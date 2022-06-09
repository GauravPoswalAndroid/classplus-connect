package com.fankonnect.app.login.ui

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fankonnect.app.R
import com.fankonnect.app.login.data.model.OtpVerifyData
import com.fankonnect.app.login.viewmodel.LoginViewModel
import com.fankonnect.app.util.SharedPreferenceHelper
import com.fankonnect.app.util.Status
import com.fankonnect.app.util.hide
import com.fankonnect.app.util.show
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
        et_enter_otp.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                verify_otp.enableDisableButton(s.toString().length >= 4)
            }

        })
        iv_back_btn.setOnClickListener {
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
        viewModel.showSendAgainProgress.observe(viewLifecycleOwner) { shouldShow ->
            if (shouldShow) {
                verify_otp.progress_bar.show()
            } else {
                verify_otp.progress_bar.hide()
            }
        }
        viewModel.verifyOtpResponse.observe(viewLifecycleOwner) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        verify_otp.progress_bar.hide()
                        if (resource.data?.data?.exists == 1) {
                            SharedPreferenceHelper.saveToken(
                                requireContext(),
                                resource.data.data.token
                            )
                            SharedPreferenceHelper.saveLandingUrl(
                                requireContext(),
                                resource.data.data.landingUrl
                            )
                            WebViewActivity.startActivity(
                                requireActivity(),
                                getFormattedUrl(resource.data.data)
                            )
                            requireActivity().finish()
                        } else {
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

    fun setMobileNumber() {
        val mobileNo = "+91-${viewModel.userMobile}"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            tv_enter_number_email.text = Html.fromHtml(
                getString(
                    R.string.formatter_string,
                    mobileNo
                ), Html.FROM_HTML_MODE_LEGACY
            )
        else
            @Suppress("DEPRECATION")
            tv_enter_number_email.text = Html.fromHtml(
                getString(
                    R.string.formatter_string,
                    mobileNo
                )
            )
    }

    companion object {

        fun newInstance(): OtpFragment {
            return OtpFragment()
        }

    }
}