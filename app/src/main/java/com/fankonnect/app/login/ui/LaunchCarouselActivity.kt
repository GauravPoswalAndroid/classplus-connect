package com.fankonnect.app.login.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.fankonnect.app.R
import com.fankonnect.app.login.adapter.ViewPagerAdapter
import kotlinx.android.synthetic.main.activity_launch_carousel.*
import java.util.*

class LaunchCarouselActivity : AppCompatActivity() {

    private var currentPage = 0

    companion object {
        private const val NUM_PAGES = 4
        private const val DELAY_MS = 500L
        private const val PERIOD_MS = 3000L
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
        runInfiniteCarousel()
    }

    private fun runInfiniteCarousel() {

        val handler = Handler()
        val update = Runnable {
            if (currentPage == NUM_PAGES - 1) {
                currentPage = 0
            }
            carouselViewPager.setCurrentItem(currentPage++, true)
        }

       val timer = Timer() // This will create a new Thread
        timer.schedule(object : TimerTask() {
            // task to be scheduled
           override fun run() {
                handler.post(update)
            }
        }, DELAY_MS, PERIOD_MS)
    }

    private fun setUpPager() {
        ViewPagerAdapter(supportFragmentManager).apply {
            addFragment(LaunchCarouselFragment.newInstance(), "")
            addFragment(LaunchCarouselFragment.newInstance(), "")
            addFragment(LaunchCarouselFragment.newInstance(), "")

        }.also {
            carouselViewPager.adapter = it
            carouselViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {}

                override fun onPageSelected(position: Int) {
                }

                override fun onPageScrollStateChanged(state: Int) {}
            })
        }
        tabLayout.setupWithViewPager(carouselViewPager)
    }
}