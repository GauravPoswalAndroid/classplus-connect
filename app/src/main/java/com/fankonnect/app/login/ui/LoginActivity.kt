package com.fankonnect.app.login.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.fankonnect.app.R
import com.fankonnect.app.base.ViewModelFactory
import com.fankonnect.app.login.adapter.ViewPagerAdapter
import com.fankonnect.app.login.data.api.LoginApiService
import com.fankonnect.app.login.data.repository.LoginDataRepository
import com.fankonnect.app.login.viewmodel.LoginViewModel
import com.fankonnect.app.network.RetrofitBuilder
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
            login_viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {}

                override fun onPageSelected(position: Int) {
                    if (position == 1) {
                        viewModel.isOtpPageShowing = true
                        ((login_viewPager.adapter as ViewPagerAdapter).getItem(1) as OtpFragment).setMobileNumber()
                    }
                    else viewModel.isOtpPageShowing = false
                }

                override fun onPageScrollStateChanged(state: Int) {}
            })
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