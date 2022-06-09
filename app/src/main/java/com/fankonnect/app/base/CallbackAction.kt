package com.fankonnect.app.base

interface CallbackAction<TYPE> {
    fun onAction(action: TYPE)
}