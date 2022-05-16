package com.classplus.connect.login.data.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

class DeeplinkModel() : Parcelable {

    var variables: JsonObject? = null

    @SerializedName("screen")
    var screen: String? = null

    @SerializedName("paramOne")
    var paramOne: String? = null

    @SerializedName("paramTwo")
    var paramTwo: String? = null

    @SerializedName("paramThree")
    var paramThree: String? = null

    @SerializedName("paramFour")
    var paramFour: String? = null

    @SerializedName("paramFive")
    var paramFive: String? = null

    @SerializedName("paramSix")
    var paramSix: String? = null

    @SerializedName("paramTracking")
    var paramTracking: String? = null

    /**
     * paramSource is being used to transfer viewTag from DynamicCardScreen
     * for Differentiating Home and Store Screen
     */
    @SerializedName("paramSource")
    var paramSource: String? = null

    @SerializedName("clickSource")
    var clickSource: String? = null

    @SerializedName("paramUxcam")
    var paramUxcam: String? = null

    @SerializedName("paramClearHistory")
    var paramClearHistory: String? = null

    constructor(parcel: Parcel) : this() {
        screen = parcel.readString()
        paramOne = parcel.readString()
        paramTwo = parcel.readString()
        paramThree = parcel.readString()
        paramFour = parcel.readString()
        paramFive = parcel.readString()
        paramSix = parcel.readString()
        paramTracking = parcel.readString()
        paramSource = parcel.readString()
        clickSource = parcel.readString()
        paramUxcam = parcel.readString()
        paramClearHistory = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(screen)
        parcel.writeString(paramOne)
        parcel.writeString(paramTwo)
        parcel.writeString(paramThree)
        parcel.writeString(paramFour)
        parcel.writeString(paramFive)
        parcel.writeString(paramSix)
        parcel.writeString(paramTracking)
        parcel.writeString(paramSource)
        parcel.writeString(clickSource)
        parcel.writeString(paramUxcam)
        parcel.writeString(paramClearHistory)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DeeplinkModel> {
        override fun createFromParcel(parcel: Parcel): DeeplinkModel {
            return DeeplinkModel(parcel)
        }

        override fun newArray(size: Int): Array<DeeplinkModel?> {
            return arrayOfNulls(size)
        }
    }


}