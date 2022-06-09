package com.fankonnect.app.login.data.model

import com.fankonnect.app.base.BaseAction

sealed class ListingActions : BaseAction {
    class UserClickedAction(val name: String) : ListingActions()
}