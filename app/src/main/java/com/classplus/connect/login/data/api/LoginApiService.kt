package com.classplus.connect.login.data.api

import com.classplus.connect.login.data.model.GetOtpResponse
import com.classplus.connect.login.data.model.OtpVerifyResponse
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