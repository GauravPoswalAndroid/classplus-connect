package com.fankonnect.app.login.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fankonnect.app.login.data.model.GetOtpResponse
import com.fankonnect.app.login.data.model.OtpVerifyResponse
import com.fankonnect.app.login.data.repository.LoginDataRepository
import com.fankonnect.app.util.Resource
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel(
    private val listingRepository: LoginDataRepository,
) : ViewModel() {

    var isOtpPageShowing: Boolean = false
    lateinit var userMobile: String
    lateinit var sessionId: String
    lateinit var otp: String


    val updatePagerNavToPage = MutableLiveData<Int>()
    val isInvalidOtp = MutableLiveData<Boolean>()
    val showSendAgainProgress = MutableLiveData<Boolean>()

    private val _getOtpResponse = MutableLiveData<Resource<GetOtpResponse>>()
    val getOtpResponse: LiveData<Resource<GetOtpResponse>>
        get() = _getOtpResponse

    private val _verifyOtpResponse = MutableLiveData<Resource<OtpVerifyResponse>>()
    val verifyOtpResponse: LiveData<Resource<OtpVerifyResponse>>
        get() = _verifyOtpResponse

    private val _registerUserResponse = MutableLiveData<Resource<OtpVerifyResponse>>()
    val registerUserResponse: LiveData<Resource<OtpVerifyResponse>>
        get() = _registerUserResponse

    fun getOtpWithMobile(mobileNo: String) = viewModelScope.launch {
        userMobile = mobileNo
        _getOtpResponse.value = Resource.loading(null)
        try {
            _getOtpResponse.value = Resource.success(listingRepository.getOtpWithMobile(mobileNo))
        } catch (throwable: Throwable) {
            when (throwable) {
                is HttpException -> {
                    val errorBody = Gson().fromJson(throwable.response()?.errorBody()?.charStream(), GetOtpResponse::class.java)
                    _getOtpResponse.value =
                        Resource.error(null, errorBody.message)
                }
                else -> {
                    _getOtpResponse.value = Resource.error(null, "Something went wrong!")

                }
            }
        }
    }

    fun verifyOtp(otp: String) = viewModelScope.launch {
        _verifyOtpResponse.value = Resource.loading(null)
        try {
            this@LoginViewModel.otp = otp
            _verifyOtpResponse.value =
                Resource.success(listingRepository.verifyOtp(otp, userMobile, sessionId))
        } catch (throwable: Throwable) {
            when (throwable) {
                is HttpException -> {
                    if(throwable.code() == 400){
                        isInvalidOtp.value = true
                    }
                    val errorBody = Gson().fromJson(throwable.response()?.errorBody()?.charStream(), GetOtpResponse::class.java)
                    _verifyOtpResponse.value = Resource.error(null, errorBody.message)
                }
                else -> {
                    _verifyOtpResponse.value = Resource.error(null, "Something went wrong!")

                }
            }
        }
    }
}