package com.classplus.connect.login.ui

import android.graphics.Color
import android.os.Bundle
import android.text.*
import android.text.Annotation
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
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
import com.gauravposwal.testapplication.util.Status
import com.gauravposwal.testapplication.util.hide
import com.gauravposwal.testapplication.util.show
import kotlinx.android.synthetic.main.fragment_login_signup.*
import kotlinx.android.synthetic.main.fragment_login_signup.tv_error_info
import kotlinx.android.synthetic.main.fragment_login_signup.tv_login_heading
import kotlinx.android.synthetic.main.loading_button.view.*

class LoginSignupFragment : Fragment() {

    lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_login_signup, null, false)

        initVariables()
        setupUi(view)
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

    private fun setupUi(view: View) {

    }

    private fun setupVisibility() {
        tv_login_heading.text = getString(R.string.login_or_signup)
        ll_proceed.enableDisableButton(false)
        ll_proceed.updateButtonState(
            false,
            getString(R.string.proceed_securely),
            R.drawable.ic_lock_new
        )
        et_mobile.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null && s.toString().contains("+")) {
                    et_mobile.setText(s.toString().replace("+", ""));
                }
            }

            override fun afterTextChanged(s: Editable?) {
                enableProceedButton()
            }
        })

    }

    private fun clickListeners() {
        ll_proceed.setOnClickListener {
            verifyNumberOrEmail()
        }
    }

    private fun enableProceedButton() {
        if (et_mobile.text.toString().isNotEmpty()) {
            tv_error_info.visibility = View.GONE
            ll_proceed.enableDisableButton(true)
        } else {
            ll_proceed.enableDisableButton(false)
        }
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
                            }

                            override fun updateDrawState(paint: TextPaint) {
                                super.updateDrawState(paint)
                                paint.isUnderlineText = true
                                paint.color =
                                    ContextCompat.getColor(requireContext(), R.color.accent_color)
                            }
                        },
                        tncText.getSpanStart(annotation),
                        tncText.getSpanEnd(annotation),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
        }
        tv_terms_conditions.text = ssb
        tv_terms_conditions.movementMethod = LinkMovementMethod.getInstance()
        tv_terms_conditions.highlightColor = Color.TRANSPARENT
    }

    private fun checkLoginFlow(value: Int) {
//        tv_cc_initial.text = "+${viewModel.dataManager.cachedOrgSettings?.data?.countryISO ?: ""}"
        continueWithEmailOrMobile(value)
//          binding.etMobile.setText("")
        et_mobile.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(50))
        et_mobile.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        et_mobile.hint = getString(R.string.email_id_hint)
    }

    private fun continueWithEmailOrMobile(value: Int) {
//        var ssb: SpannableStringBuilder? = null
//            if (toShowAlternateOption.isFalse())
//                tv_enter_number_email.text = getString(R.string.enter_your_email_address)
//            else
//                ssb = getSpannableStringBuilder(true)
//        if (ssb != null) tv_enter_number_email.text = ssb
//        tv_enter_number_email.movementMethod = LinkMovementMethod.getInstance()
//        tv_enter_number_email.highlightColor = Color.TRANSPARENT
    }

//    private fun getSpannableStringBuilder(isContinueWithMobile: Boolean): SpannableStringBuilder {
//        val continueText = (if (isContinueWithMobile) getText(R.string.please_enter_your_email_address) else getText(R.string.please_enter_your_mobile_number)) as SpannedString
//        val ssb = SpannableStringBuilder(continueText)
//        val annotations = continueText.getSpans(0, continueText.length, Annotation::class.java)
//        if (annotations != null && annotations.isNotEmpty()) {
//            for (annotation in annotations) {
//                if (annotation.key == "click") {
//                    ssb.setSpan(object : ClickableSpan() {
//                        override fun onClick(widget: View) {
//                            if (isContinueWithMobile) {
//                                tv_cc_initial.visibility = View.VISIBLE
//                                divider.visibility = View.VISIBLE
//                                tv_error_info.visibility = View.GONE
//                                et_mobile.inputType = InputType.TYPE_CLASS_PHONE
//                                et_mobile.keyListener = DigitsKeyListener.getInstance("0123456789")
//                                et_mobile.hint = getString(R.string.label_mobile_number_hint)
//                                loginType = 1 - loginType
//                                checkLoginFlow(loginType)
//                            } else {
//                                tv_error_info.visibility = View.GONE
//                                tv_cc_initial.visibility = View.GONE
//                                divider.visibility = View.GONE
//                                et_mobile.setText("")
//                                et_mobile.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(50))
//                                et_mobile.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
//                                et_mobile.hint = getString(R.string.label_email_id_hint)
//                                loginType = 1 - loginType
//                                checkLoginFlow(loginType)
//                            }
//                        }
//
//                        override fun updateDrawState(paint: TextPaint) {
//                            super.updateDrawState(paint)
//                            paint.isUnderlineText = true
//                            paint.color = ContextCompat.getColor(requireContext(), R.color.colorAccent)
//                        }
//                    }, continueText.getSpanStart(annotation), continueText.getSpanEnd(annotation), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//                }
//            }
//        }
//        return ssb
//    }

    private fun handleSuccess(data: GetOtpResponse?) {
        data?.let {
            viewModel.sessionId = data.data.sessionId
            viewModel._isOtpRequestSent.value = true
//            llProceed.text = "Verify OTP"
//            isOtpReceived = true
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

        viewModel.verifyOtpResponse.observe(requireActivity()) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        ll_proceed.progress_bar.hide()
                        if (resource.data?.data?.exists == 1) {
                            WebViewActivity.startActivity(context, getFormattedUrl(resource.data.data))
                        } else {
                            Toast.makeText(
                                context,
                                "Registration flow is under dev...",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

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
        /**
         * These two values are used to handle the back navigation from registration screen.
         * It was a use case by Design team, that previous screen should be filled with data if navigate back.
         * But the same flow was being used for Signin with prefilled number/email. These two seperate the flows.
         */
        const val NORMAl_NAVIGATION_FLOW = 100
        const val BACK_NAVIGATION_FLOW = 101

        private const val TAG = "LoginSignupFragment"

        fun newInstance(): LoginSignupFragment {
            val fragment = LoginSignupFragment()
            return fragment
        }

    }
}