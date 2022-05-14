package com.classplus.connect.login.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.classplus.connect.R
import com.classplus.connect.base.ViewModelFactory
import com.classplus.connect.login.adapter.ViewPagerAdapter
import com.classplus.connect.login.data.api.LoginApiService
import com.classplus.connect.login.data.repository.LoginDataRepository
import com.classplus.connect.login.viewmodel.LoginViewModel
import com.classplus.connect.network.RetrofitBuilder
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    lateinit var viewModel: LoginViewModel

    companion object {
        private const val TAG = "LoginActivity"
        fun startActivity(context: Context) {
            Intent(context, LoginActivity::class.java).apply {}
                .also {
                    context.startActivity(it)
                }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setUpPager()
        initProperties()
        setUpObservers()
    }

    private fun setUpPager() {
        ViewPagerAdapter(supportFragmentManager).apply {
            addFragment(LoginSignupFragment.newInstance())
            addFragment(OtpFragment.newInstance())

        }.also {
            login_viewPager.adapter = it
        }
    }

    private fun setUpObservers() {

        viewModel.updatePagerNavToPage.observe(this) {
            Log.i(TAG, "setUpObservers: PagerNavigatedToPage: $it")
            login_viewPager.currentItem = it
        }
    }


    private fun initProperties() {
        val listingApiService = RetrofitBuilder.provideService(LoginApiService::class.java) as LoginApiService
        val factory = ViewModelFactory(LoginDataRepository(listingApiService))
        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]
    }
}