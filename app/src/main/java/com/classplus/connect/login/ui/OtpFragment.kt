package com.classplus.connect.login.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.classplus.connect.R
import com.classplus.connect.login.data.model.OtpVerifyData
import com.classplus.connect.login.viewmodel.LoginViewModel
import com.classplus.connect.util.Status
import com.classplus.connect.util.hide
import com.classplus.connect.util.show
import kotlinx.android.synthetic.main.fragment_otp.*
import kotlinx.android.synthetic.main.loading_button.view.*

class OtpFragment : Fragment() {

    lateinit var viewModel: LoginViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_otp, null, false)
        return view
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

        viewModel.verifyOtpResponse.observe(viewLifecycleOwner) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        ll_verify_otp.progress_bar.hide()
                        if(resource.data?.data?.exists == 1){
                            WebViewActivity.startActivity(requireActivity(), getFormattedUrl(resource.data.data))
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

    companion object {

        fun newInstance(): OtpFragment {
            return OtpFragment()
        }

    }
}