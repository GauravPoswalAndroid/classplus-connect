package com.fankonnect.app.login.data.model

import android.os.Parcelable
import com.fankonnect.app.base.BaseViewItem
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserViewItem(
    val userName : String,
    val userAvator : String,
    override val viewType: Int?
):Parcelable, BaseViewItem