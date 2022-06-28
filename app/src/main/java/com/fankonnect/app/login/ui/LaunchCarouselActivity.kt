package com.fankonnect.app.login.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.viewpager.widget.ViewPager
import com.fankonnect.app.R
import com.fankonnect.app.login.adapter.ViewPagerAdapter
import kotlinx.android.synthetic.main.activity_launch_carousel.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch


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
        setUpViews()
        setUpPager()
        MainScope().launch {
            getViewPagerPages().collect {
                carouselViewPager.currentItem = it
            }
        }
    }

    private fun setUpViews() {
        tvLoginSignUp.setOnClickListener{
            LoginActivity.startActivity(this)
        }
    }

    private fun getViewPagerPages() = flow {
        repeat(3) {
            delay(1500)
            emit(it)
        }
    }.flowOn(Dispatchers.Default)

    private fun highlightCTA() {
        tvLoginSignUp.background =
            ResourcesCompat.getDrawable(resources, R.drawable.bg_button_click_color_primary, theme)
        tvLoginSignUp.setTextColor(ContextCompat.getColor(this, R.color.white))
    }

    private fun setUpPager() {
        ViewPagerAdapter(supportFragmentManager).apply {
            addFragment(LaunchCarouselFragment.newInstance(R.drawable.ic_carousel_image1, getString(R.string.carousel_text1)), "")
            addFragment(LaunchCarouselFragment.newInstance(R.drawable.ic_carousel_image2, getString(R.string.carousel_text2)), "")
            addFragment(LaunchCarouselFragment.newInstance(R.drawable.ic_carousel_image3, getString(R.string.carousel_text3)), "")

        }.also {
            carouselViewPager.adapter = it
            carouselViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    if(position == 2) highlightCTA()
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
}