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

class SignUpViewModel(
    private val listingRepository: LoginDataRepository,
) : ViewModel() {

    lateinit var userMobile: String
    lateinit var name: String
    lateinit var email: String
    lateinit var otp: String
    lateinit var sessionId: String

    private val _registerUserResponse = MutableLiveData<Resource<OtpVerifyResponse>>()
    val registerUserResponse: LiveData<Resource<OtpVerifyResponse>>
        get() = _registerUserResponse


    fun registerUser() = viewModelScope.launch {
        _registerUserResponse.value = Resource.loading(null)
        try {
            _registerUserResponse.value = Resource.success(
                listingRepository.registerUser(otp, userMobile, sessionId, name, email)
            )
        } catch (throwable: Throwable) {
            when (throwable) {
                is HttpException -> {
                    val errorBody = Gson().fromJson(throwable.response()?.errorBody()?.charStream(), GetOtpResponse::class.java)
                    _registerUserResponse.value = Resource.error(null, errorBody.message)
                }
                else -> {
                    _registerUserResponse.value = Resource.error(null, "Something went wrong!")

                }
            }
        }
    }
}