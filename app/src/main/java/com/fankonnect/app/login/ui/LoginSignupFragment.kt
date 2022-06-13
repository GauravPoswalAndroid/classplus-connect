package com.fankonnect.app.login.ui

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
import com.fankonnect.app.R
import com.fankonnect.app.base.ViewModelFactory
import com.fankonnect.app.login.data.api.LoginApiService
import com.fankonnect.app.login.data.model.GetOtpResponse
import com.fankonnect.app.login.data.repository.LoginDataRepository
import com.fankonnect.app.login.viewmodel.LoginViewModel
import com.fankonnect.app.network.RetrofitBuilder
import com.fankonnect.app.util.*
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
        setTermsConditionsText()
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

    private fun setTermsConditionsText() {
        val tncText = getText(R.string.str_terms_and_conditions_new) as SpannedString
        val ssb = SpannableStringBuilder(tncText)
        val annotations = tncText.getSpans(0, tncText.length, Annotation::class.java)
        if (annotations != null && annotations.isNotEmpty()) {
            for (annotation in annotations) {
                if (annotation.key == "click") {
                    ssb.setSpan(object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            Utility.openWebPage(requireContext(), AppConstants.FAN_CONNECT_PRIVACY_POLICY_URL)
                        }

                        override fun updateDrawState(paint: TextPaint) {
                            super.updateDrawState(paint)
                            paint.isUnderlineText = true
                            paint.color = ContextCompat.getColor(requireContext(), R.color.accent_color)
                        }
                    }, tncText.getSpanStart(annotation), tncText.getSpanEnd(annotation), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
        }
        tv_terms_conditions.text = ssb
        tv_terms_conditions.movementMethod = LinkMovementMethod.getInstance()
        tv_terms_conditions.highlightColor = Color.TRANSPARENT
    }

    companion object {
        fun newInstance(): LoginSignupFragment {
            return LoginSignupFragment()
        }

    }
}