package com.fankonnect.app.login.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fankonnect.app.login.data.model.CarouselDataItem
import com.fankonnect.app.login.data.model.LaunchCarouselResponse
import com.fankonnect.app.login.data.repository.LaunchCarouselRepository
import com.fankonnect.app.util.Resource
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LaunchCarouselViewModel(
    private val launchCarouselRepository: LaunchCarouselRepository,
) : ViewModel() {



    private val _launchCarouselResponse = MutableLiveData<Resource<List<CarouselDataItem>>>()
    val launchCarouselResponse: LiveData<Resource<List<CarouselDataItem>>>
        get() = _launchCarouselResponse


    fun getLaunchCarouselData() = viewModelScope.launch {

        _launchCarouselResponse.value = Resource.loading(null)
        try {
            _launchCarouselResponse.value = Resource.success(launchCarouselRepository.getLaunchCarouselData().data)
        } catch (throwable: Throwable) {
            when (throwable) {
                is HttpException -> {
                    val errorBody = Gson().fromJson(throwable.response()?.errorBody()?.charStream(), LaunchCarouselResponse::class.java)
                    _launchCarouselResponse.value =
                        errorBody?.let {  errorBody.message}?.let { Resource.error(null, it) }
                }
                else -> {
                    _launchCarouselResponse.value = Resource.error(null, "Something went wrong!")

                }
            }
        }
    }

}