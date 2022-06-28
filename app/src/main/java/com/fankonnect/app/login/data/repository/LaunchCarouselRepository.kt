package com.fankonnect.app.login.data.repository

import com.fankonnect.app.base.BaseRepository
import com.fankonnect.app.login.data.api.LaunchCarouselApiService

class LaunchCarouselRepository(private val launchCarouselApiService: LaunchCarouselApiService) :
    BaseRepository() {


    suspend fun getLaunchCarouselData(
    ) = hitApiCall {
        launchCarouselApiService.getOnboardingData()
    }

}