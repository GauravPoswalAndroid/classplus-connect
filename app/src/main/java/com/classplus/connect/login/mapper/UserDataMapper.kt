package com.classplus.connect.login.mapper

import com.classplus.connect.base.Mapper
import com.classplus.connect.login.data.model.GitHubUser
import com.classplus.connect.login.data.model.UserViewItem

class UserDataMapper : Mapper<List<GitHubUser>, List<UserViewItem>> {

    override fun map(srcObject: List<GitHubUser>): List<UserViewItem> {
        return srcObject.map {
            UserViewItem(
                it.login,
                it.avatarUrl,
                null
            )
        }
    }
}