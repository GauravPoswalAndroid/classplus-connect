package com.classplus.connect.login.data.repository

import com.classplus.connect.base.BaseRepository
import com.classplus.connect.login.data.api.LoginApiService
import com.google.gson.JsonObject

class LoginDataRepository(private val loginApiService: LoginApiService) : BaseRepository() {

    suspend fun getOtpWithMobile(
        mobileNo: String
    ) = hitApiCall {
        loginApiService.getOtpWithMobile(getJsonBodyForGetOtp(mobileNo))
    }

    suspend fun verifyOtp(
        otp: String,
        mobile: String,
        sessionId: String
    ) = hitApiCall {
        loginApiService.verifyOtp(getJsonBodyForVerifyOtp(otp, mobile, sessionId))
    }

    suspend fun registerUser(
        otp: String,
        mobile: String,
        sessionId: String,
        name: String,
        email: String
    ) = hitApiCall {
        loginApiService.registerUser(
            getJsonBodyForUserRegistration(
                name,
                sessionId,
                otp,
                mobile,
                email,
            )
        )
    }

    private fun getJsonBodyForVerifyOtp(
        otp: String,
        mobile: String,
        sessionId: String
    ) = JsonObject().apply {
        addProperty("mobile", mobile)
        addProperty("otp", otp)
        addProperty("sessionId", sessionId)
    }

    private fun getJsonBodyForGetOtp(mobileNo: String) = JsonObject().apply {
        addProperty("mobile", mobileNo)
    }

    private fun getJsonBodyForUserRegistration(
        name: String,
        sessionId: String,
        otp: String,
        mobile: String,
        email: String
    ) = JsonObject().apply {
        addProperty("name", name)
        addProperty("sessionId", sessionId)
        addProperty("otp", otp)
        add("contact", JsonObject().apply {
            addProperty("email", email)
            addProperty("mobile", mobile)
        })
    }
}