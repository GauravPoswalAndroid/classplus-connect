package com.classplus.connect.base

import java.io.Serializable

interface BaseViewItem: Serializable {
    val viewType: Int?
}