package com.classplus.connect.login.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.classplus.connect.R
import com.classplus.connect.base.ViewModelFactory
import com.classplus.connect.login.data.api.LoginApiService
import com.classplus.connect.login.data.model.UserViewItem
import com.classplus.connect.login.data.repository.LoginDataRepository
import com.classplus.connect.login.viewmodel.LoginViewModel
import com.classplus.connect.network.RetrofitBuilder
import com.gauravposwal.testapplication.util.Status
import com.gauravposwal.testapplication.util.hide
import com.gauravposwal.testapplication.util.show
import kotlinx.android.synthetic.main.app_progress_bar.*

class LoginActivity : AppCompatActivity() {
    lateinit var viewModel: LoginViewModel

    companion object {
        fun start(context: Context) {
            Intent(context, LoginActivity::class.java).apply {}
                .also {
                    context.startActivity(it)
                }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initProperties()
        setUpObservers()
        getUsersListing()
    }

    private fun setUpObservers() {
        viewModel.userListingData.observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        progressBar.hide()
                        handleSuccess(resource.data)
                    }
                    Status.ERROR -> {
                        progressBar.hide()
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    }
                    Status.LOADING -> {
                        progressBar.show()

                    }
                }
            }
        }
    }

    private fun handleSuccess(data: List<UserViewItem>?) {
        data?.let {
            Toast.makeText(this, "Api hit Successfully", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initProperties() {
        val listingApiService = RetrofitBuilder.provideService(LoginApiService::class.java) as LoginApiService
        val factory = ViewModelFactory(LoginDataRepository(listingApiService))
        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]
    }

    private fun getUsersListing() {
        viewModel.fetchUsersList()
    }
}