package com.classplus.connect.base

interface CallbackAction<TYPE> {
    fun onAction(action: TYPE)
}