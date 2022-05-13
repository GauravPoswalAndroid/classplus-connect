package com.classplus.connect.login.data.model

import com.classplus.connect.base.BaseAction

sealed class ListingActions : BaseAction {
    class UserClickedAction(val name: String) : ListingActions()
}