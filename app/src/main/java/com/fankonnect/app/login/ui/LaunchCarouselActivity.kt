package com.fankonnect.app.login.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.fankonnect.app.R
import com.fankonnect.app.login.adapter.ViewPagerAdapter
import kotlinx.android.synthetic.main.activity_launch_carousel.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import android.view.ViewGroup
import android.widget.LinearLayout


class LaunchCarouselActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "LaunchCarouselActivity"
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
        setUpPager()
        MainScope().launch {
            getViewPagerPages().collect {
                carouselViewPager.currentItem = it
            }
        }
    }

    private fun getViewPagerPages() = flow {
        repeat(3) {
            delay(2000)
            emit(it)
        }
    }.flowOn(Dispatchers.Default)

    private fun setUpPager() {
        ViewPagerAdapter(supportFragmentManager).apply {
            addFragment(LaunchCarouselFragment.newInstance(), "")
            addFragment(LaunchCarouselFragment.newInstance(), "")
            addFragment(LaunchCarouselFragment.newInstance(), "")

        }.also {
            carouselViewPager.adapter = it
        }
        tabLayout.setupWithViewPager(carouselViewPager)
        for (i in 0 until tabLayout.tabCount) {
            val tab: View = (tabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
            (tab.layoutParams as LinearLayout.LayoutParams).setMargins(8, 0, 8, 0)
            tab.requestLayout()
        }
    }
}