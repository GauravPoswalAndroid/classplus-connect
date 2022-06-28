package com.fankonnect.app.login.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.fankonnect.app.R
import kotlinx.android.synthetic.main.fragment_launch_carousel.*

class LaunchCarouselFragment : Fragment() {
    private var carouselText: String? = null
    private var carouselImage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            carouselText = it.getString(CAROUSEL_TEXT)
            carouselImage = it.getString(CAROUSEL_IMAGE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_launch_carousel, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCarouselData()
    }

    private fun setCarouselData() {
        carouselImage?.let {
            Glide.with(requireContext()).load(it).into(ivCarouselImage)
        }
        carouselText?.let {
            tvCarouselText.text = it
        }
    }

    companion object {
        private const val CAROUSEL_TEXT = "CAROUSEL_TEXT"
        private const val CAROUSEL_IMAGE = "CAROUSEL_IMAGE"

        @JvmStatic
        fun newInstance(text: String?, carouselImage: String?) =
            LaunchCarouselFragment().apply {
                arguments = Bundle().apply {
                    putString(CAROUSEL_TEXT, text)
                    putString(CAROUSEL_IMAGE, carouselImage)
                }
            }
    }
}