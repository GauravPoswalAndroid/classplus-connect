package com.fankonnect.app.login.data.model

sealed class LoginState{
    class MobileState: LoginState()
    class OtpState: LoginState()
}
