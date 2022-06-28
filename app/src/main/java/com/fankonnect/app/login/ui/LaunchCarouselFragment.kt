package com.fankonnect.app.login.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fankonnect.app.R
import kotlinx.android.synthetic.main.fragment_launch_carousel.*

class LaunchCarouselFragment : Fragment() {
    private var carouselImage: Int? = null
    private var carouselText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            carouselImage = it.getInt(CAROUSEL_IMAGE_PATH)
            carouselText = it.getString(CAROUSEL_TEXT)
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
            ivCarouselImage.setImageResource(it)
        }
        carouselText?.let {
            tvCarouselText.text = it
        }
    }

    companion object {
        private const val CAROUSEL_IMAGE_PATH = "CAROUSEL_IMAGE_PATH"
        private const val CAROUSEL_TEXT = "CAROUSEL_TEXT"

        @JvmStatic
        fun newInstance(imageSrc: Int, text: String) =
            LaunchCarouselFragment().apply {
                arguments = Bundle().apply {
                    putInt(CAROUSEL_IMAGE_PATH, imageSrc)
                    putString(CAROUSEL_TEXT, text)
                }
            }
    }
}