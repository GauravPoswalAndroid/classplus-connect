package com.classplus.connect.login.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.classplus.connect.R

class LoadingButton @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.loading_button, this, true)

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it,
                    R.styleable.LoadingButton, 0, 0)

            with(resources) {
                val title = getText(typedArray
                        .getResourceId(R.styleable
                                .LoadingButton_title, R.string.done))
                setButtonData(title.toString(), typedArray.getResourceId(R.styleable.LoadingButton_drawableStart, 0))
            }

            typedArray.recycle()
        }
    }

    @JvmOverloads
    fun updateButtonState(isLoading: Boolean, btnText: String? = null, @DrawableRes drawable: Int = 0) {
        findViewById<ProgressBar>(R.id.progress_bar)?.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnText?.let {
            setButtonData(it, drawable)
        }
    }

    /**
     * Don't call this method directly. Instead, use the extension method `LoadingButton.enable()` in
     * {@see Extensions.kt} file.
     */
    fun enableDisableButton(shouldEnable: Boolean) {
        findViewById<ConstraintLayout>(R.id.btnRoot).isEnabled = shouldEnable
        findViewById<AppCompatTextView>(R.id.text_view)?.apply {
            isEnabled = shouldEnable
            setTextColor(
                    if (shouldEnable) ContextCompat.getColor(context, R.color.white)
                    else ContextCompat.getColor(context, R.color.gray100)
            )
        }
    }

    private fun setButtonData(text: String, @DrawableRes drawable: Int = 0) {
        findViewById<AppCompatTextView>(R.id.text_view)?.let {
            it.text = text
            it.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0)
        }
    }
}