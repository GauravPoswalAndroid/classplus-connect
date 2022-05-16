package com.classplus.connect.login.data.model


import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
@Keep
data class GetOtpResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: GetOtpData
) : Parcelable



@SuppressLint("ParcelCreator")
@Parcelize
@Keep
data class GetOtpData(
    @SerializedName("sessionId")
    val sessionId: String
) : Parcelable