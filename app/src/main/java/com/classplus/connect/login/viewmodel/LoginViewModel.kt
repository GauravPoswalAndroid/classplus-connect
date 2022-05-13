package com.classplus.connect.login.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.classplus.connect.login.data.model.GetOtpResponse
import com.classplus.connect.login.data.model.OtpVerifyResponse
import com.classplus.connect.login.data.repository.LoginDataRepository
import com.gauravposwal.testapplication.util.Resource
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel(
    private val listingRepository: LoginDataRepository,
) : ViewModel() {

    lateinit var userMobile: String
    lateinit var sessionId: String
    lateinit var otp: String

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
        _getOtpResponse.value = Resource.loading(null)
        try {
            _getOtpResponse.value = Resource.success(listingRepository.getOtpWithMobile(mobileNo))
        } catch (throwable: Throwable) {
            when (throwable) {
                is HttpException -> {
                    _getOtpResponse.value =
                        Resource.error(null, throwable.message())
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
            _verifyOtpResponse.value =
                Resource.success(listingRepository.verifyOtp(otp, userMobile, sessionId))
        } catch (throwable: Throwable) {
            when (throwable) {
                is HttpException -> {
                    _verifyOtpResponse.value =
                        Resource.error(null, throwable.message())
                }
                else -> {
                    _verifyOtpResponse.value = Resource.error(null, "Something went wrong!")

                }
            }
        }
    }

    fun registerUser(name: String, email: String) = viewModelScope.launch {
        _registerUserResponse.value = Resource.loading(null)
        try {
            _registerUserResponse.value = Resource.success(
                listingRepository.registerUser(otp, userMobile, sessionId, name, email)
            )
        } catch (throwable: Throwable) {
            when (throwable) {
                is HttpException -> {
                    _registerUserResponse.value =
                        Resource.error(null, throwable.message())
                }
                else -> {
                    _registerUserResponse.value = Resource.error(null, "Something went wrong!")

                }
            }
        }
    }
}