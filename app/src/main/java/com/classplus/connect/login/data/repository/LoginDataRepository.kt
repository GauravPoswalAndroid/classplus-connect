package com.classplus.connect.login.data.repository

import com.classplus.connect.base.BaseRepository
import com.classplus.connect.login.data.api.LoginApiService

class LoginDataRepository(private val loginApiService: LoginApiService) : BaseRepository() {

    suspend fun getUsersList(
        offset: Int,
        pagedListSize: Int
    ) = hitApiCall {
        loginApiService.getUsersListing(
            offset,
            pagedListSize
        )
    }
}