package com.classplus.connect.login.data.api

import com.classplus.connect.login.data.model.GitHubUser
import retrofit2.http.GET
import retrofit2.http.Query

interface LoginApiService {

    @GET("users")
    suspend fun getUsersListing(
        @Query("since") since: Int,
        @Query("per_page") perPage: Int
    ): List<GitHubUser>
}