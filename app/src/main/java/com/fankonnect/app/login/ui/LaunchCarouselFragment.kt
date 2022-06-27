package com.fankonnect.app.login.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fankonnect.app.R

class LaunchCarouselFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_launch_carousel, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            LaunchCarouselFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}