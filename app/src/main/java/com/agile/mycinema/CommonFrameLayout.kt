package com.agile.mycinema

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.widget.FrameLayout
import com.agile.mycinema.utils.PaintUtil

class CommonFrameLayout(context: Context, attrs: AttributeSet?) :
    FrameLayout(context, attrs) {
    var paintUtil = PaintUtil(this)

    init {
        isFocusable = true
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        isSelected = focused
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (isSelected) {
            paintUtil.onDraw(canvas)
        }
    }
}