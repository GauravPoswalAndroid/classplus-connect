package com.classplus.connect.login.ui

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
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
import kotlinx.android.synthetic.main.activity_signup.*
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
        setObservers()
    }
    private fun setObservers() {
        ll_verify_otp.setOnClickListener {
            viewModel.verifyOtp(et_enter_otp.text.toString())
        }
        iv_back_btn.setOnClickListener {
            viewModel.updatePagerNavToPage.value = 0
        }

        viewModel.verifyOtpResponse.observe(viewLifecycleOwner) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        ll_verify_otp.progress_bar.hide()
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
                        ll_verify_otp.progress_bar.hide()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    Status.LOADING -> {
                        ll_verify_otp.progress_bar.show()

                    }
                }
            }
        }
    }

    private fun getFormattedUrl(data: OtpVerifyData?) = "${data?.landingUrl}${data?.token}"

    fun setMobileNumber() {
        val mobileNo = "+91-${viewModel.userMobile}"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            tv_enter_number_email.text = Html.fromHtml(getString(R.string.please_enter_the_4_digit_verification_code_sent_to_you_at_91, mobileNo), Html.FROM_HTML_MODE_LEGACY)
        else
            @Suppress("DEPRECATION")
            tv_enter_number_email.text = Html.fromHtml(getString(R.string.please_enter_the_4_digit_verification_code_sent_to_you_at_91, mobileNo))
    }

    companion object {

        fun newInstance(): OtpFragment {
            return OtpFragment()
        }

    }
}