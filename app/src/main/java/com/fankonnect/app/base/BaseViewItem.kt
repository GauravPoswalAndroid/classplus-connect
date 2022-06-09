package com.fankonnect.app.base

import java.io.Serializable

interface BaseViewItem: Serializable {
    val viewType: Int?
}