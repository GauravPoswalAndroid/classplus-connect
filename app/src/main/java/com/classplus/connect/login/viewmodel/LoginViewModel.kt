package com.classplus.connect.login.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.classplus.connect.login.data.model.UserViewItem
import com.classplus.connect.login.data.repository.LoginDataRepository
import com.classplus.connect.login.mapper.UserDataMapper
import com.gauravposwal.testapplication.util.Resource
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel(
    private val listingRepository: LoginDataRepository,
    private val dataMapper: UserDataMapper
) : ViewModel() {

    companion object {
        private const val PAGED_LIST_SIZE = 20
    }

    var offset: Int = 0

    private val _userListingData = MutableLiveData<Resource<List<UserViewItem>>>()

    val userListingData: LiveData<Resource<List<UserViewItem>>>
        get() = _userListingData

    fun fetchUsersList() = viewModelScope.launch {
        _userListingData.value = Resource.loading(null)
        try {
            _userListingData.value = Resource.success(
                dataMapper.map(
                    listingRepository.getUsersList(
                        offset,
                        PAGED_LIST_SIZE
                    )
                )
            )
        } catch (throwable: Throwable) {
            when (throwable) {
                is HttpException -> {
                    _userListingData.value =
                        Resource.error(null, throwable.message())
                }
                else -> {
                    _userListingData.value = Resource.error(null, "Something went wrong!")

                }
            }
        }
    }

}