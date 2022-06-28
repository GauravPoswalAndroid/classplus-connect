package com.fankonnect.app.login.data.api

import com.fankonnect.app.login.data.model.LaunchCarouselResponse
import retrofit2.http.GET


interface LaunchCarouselApiService {

    @GET("onboardingData")
    suspend fun getOnboardingData(): LaunchCarouselResponse

}