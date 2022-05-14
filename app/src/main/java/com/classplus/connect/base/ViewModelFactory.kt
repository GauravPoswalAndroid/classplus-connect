package com.classplus.connect.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.classplus.connect.login.data.repository.LoginDataRepository
import com.classplus.connect.login.viewmodel.LoginViewModel
import com.classplus.connect.login.viewmodel.SignUpViewModel

class ViewModelFactory(private val repository: BaseRepository) : ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) ->
                LoginViewModel(
                    repository as LoginDataRepository
                ) as T

            modelClass.isAssignableFrom(SignUpViewModel::class.java) ->
                LoginViewModel(
                    repository as LoginDataRepository
                ) as T

            else -> throw IllegalArgumentException("ViewModel Class Not Found")
        }
    }
}