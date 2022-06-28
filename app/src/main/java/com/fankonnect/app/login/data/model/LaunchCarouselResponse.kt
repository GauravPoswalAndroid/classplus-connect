package com.fankonnect.app.login.data.model

import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
@Keep
data class LaunchCarouselResponse(
	@SerializedName("data")
	val data: List<CarouselDataItem>,

	@SerializedName("message")
	val message: String? = null,

	@SerializedName("status")
	val status: String? = null
) : Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
@Keep
data class CarouselDataItem(
	@SerializedName("imageUrl")
	val imageUrl: String? = null,

	@SerializedName("text")
	val text: String? = null
) : Parcelable
