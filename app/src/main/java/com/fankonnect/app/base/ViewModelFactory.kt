package com.fankonnect.app.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fankonnect.app.login.data.repository.LaunchCarouselRepository
import com.fankonnect.app.login.data.repository.LoginDataRepository
import com.fankonnect.app.login.viewmodel.LaunchCarouselViewModel
import com.fankonnect.app.login.viewmodel.LoginViewModel
import com.fankonnect.app.login.viewmodel.SignUpViewModel

class ViewModelFactory(private val repository: BaseRepository) : ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) ->
                LoginViewModel(
                    repository as LoginDataRepository
                ) as T

            modelClass.isAssignableFrom(SignUpViewModel::class.java) ->
                SignUpViewModel(
                    repository as LoginDataRepository
                ) as T

            modelClass.isAssignableFrom(LaunchCarouselViewModel::class.java) ->
                LaunchCarouselViewModel(
                    repository as LaunchCarouselRepository
                ) as T

            else -> throw IllegalArgumentException("ViewModel Class Not Found")
        }
    }
}