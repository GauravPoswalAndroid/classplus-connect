package com.classplus.connect.login.data.model


import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
@Keep
data class OtpVerifyResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: OtpVerifyData
) : Parcelable



@SuppressLint("ParcelCreator")
@Parcelize
@Keep
data class OtpVerifyData(
    @SerializedName("token")
    val token: String,
    @SerializedName("exists")
    val exists: Int,
    @SerializedName("landing_url")
    val landingUrl: String
) : Parcelable