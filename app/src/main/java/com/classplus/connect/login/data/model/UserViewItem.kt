package com.classplus.connect.login.data.model

import android.os.Parcelable
import com.classplus.connect.base.BaseViewItem
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserViewItem(
    val userName : String,
    val userAvator : String,
    override val viewType: Int?
):Parcelable, BaseViewItem