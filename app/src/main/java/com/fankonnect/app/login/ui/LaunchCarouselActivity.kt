package com.fankonnect.app.login.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.fankonnect.app.R
import com.fankonnect.app.base.ViewModelFactory
import com.fankonnect.app.login.adapter.ViewPagerAdapter
import com.fankonnect.app.login.data.api.LaunchCarouselApiService
import com.fankonnect.app.login.data.model.CarouselDataItem
import com.fankonnect.app.login.data.repository.LaunchCarouselRepository
import com.fankonnect.app.login.viewmodel.LaunchCarouselViewModel
import com.fankonnect.app.network.RetrofitBuilder
import com.fankonnect.app.util.Status
import kotlinx.android.synthetic.main.activity_launch_carousel.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch


class LaunchCarouselActivity : AppCompatActivity() {
    lateinit var viewModel: LaunchCarouselViewModel

    companion object {
        private const val TAG = "LaunchCarouselActivity"
        private const val DELAY_INTERVAL = 1500L
        fun startActivity(context: Context) {
            Intent(context, LaunchCarouselActivity::class.java).apply {}
                .also {
                    context.startActivity(it)
                }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch_carousel)
        initProperties()
        setUpObservers()
        setUpViews()
        viewModel.getLaunchCarouselData()
    }

    private fun setUpObservers() {
        viewModel.launchCarouselResponse.observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        handleSuccess(resource.data)
                    }
                    Status.ERROR -> {
                        Toast.makeText(
                            this,
                            it.message ?: "Some Error Occurred!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    Status.LOADING -> {
                    }
                }
            }
        }
    }

    private fun handleSuccess(data: List<CarouselDataItem?>?) {
        MainScope().launch {
            if (data != null) {
                getViewPagerPages(data.size).collect {
                    carouselViewPager.currentItem = it
                }
            }
        }
        data?.let {
            setUpPager(it)
        }
    }

    private fun setUpViews() {
        tvLoginSignUp.setOnClickListener {
            LoginActivity.startActivity(this)
        }
    }

    private fun getViewPagerPages(size: Int) = flow {
        repeat(size) {
            delay(DELAY_INTERVAL)
            emit(it)
        }
    }.flowOn(Dispatchers.Default)

    private fun highlightCTA() {
        tvLoginSignUp.background =
            ResourcesCompat.getDrawable(resources, R.drawable.bg_button_click_color_primary, theme)
        tvLoginSignUp.setTextColor(ContextCompat.getColor(this, R.color.white))
    }

    private fun setUpPager(carouselList: List<CarouselDataItem?>) {

        ViewPagerAdapter(supportFragmentManager).apply {
            for (carouselItem in carouselList) {
                addFragment(
                    LaunchCarouselFragment.newInstance(
                        carouselItem?.text,
                        carouselItem?.imageUrl
                    ), ""
                )
            }

        }.also {
            carouselViewPager.adapter = it
            carouselViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    if (position == carouselList.size - 1) highlightCTA()
                }

                override fun onPageSelected(position: Int) {}

                override fun onPageScrollStateChanged(state: Int) {}
            })
        }
        tabLayout.setupWithViewPager(carouselViewPager)
        for (i in 0 until tabLayout.tabCount) {
            val tab: View = (tabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
            (tab.layoutParams as LinearLayout.LayoutParams).setMargins(8, 0, 8, 0)
            tab.requestLayout()
        }

    }

    private fun initProperties() {
        val launchCarouselApiService =
            RetrofitBuilder.provideService(LaunchCarouselApiService::class.java) as LaunchCarouselApiService
        val factory = ViewModelFactory(LaunchCarouselRepository(launchCarouselApiService))
        viewModel = ViewModelProvider(this, factory)[LaunchCarouselViewModel::class.java]
    }
}