package com.fankonnect.app.login.data.api

import com.fankonnect.app.login.data.model.GetOtpResponse
import com.fankonnect.app.login.data.model.OtpVerifyResponse
import com.google.gson.JsonObject
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApiService {

    @POST("otp/get")
    suspend fun getOtpWithMobile(@Body body: JsonObject?): GetOtpResponse

    @POST("otp/verify")
    suspend fun verifyOtp(@Body body: JsonObject?): OtpVerifyResponse

    @POST("register")
    suspend fun registerUser(@Body body: JsonObject?): OtpVerifyResponse
}