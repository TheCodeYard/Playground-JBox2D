package com.thecodeyard.playground.jbox2d

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatTextView

/**
 * A simple [AppCompatTextView] with a circle as background and the text centered.
 */
class BubbleView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AppCompatTextView(context, attrs, defStyleAttr) {
    init {
        gravity = Gravity.CENTER
        setBackgroundResource(R.drawable.bg_bubble)
    }
}