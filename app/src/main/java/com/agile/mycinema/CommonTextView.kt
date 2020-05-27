package com.agile.mycinema

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet

class CommonTextView(context: Context, attrs: AttributeSet?) :
    androidx.appcompat.widget.AppCompatTextView(context, attrs) {
    var paintUtil = PaintUtil(this)
    var fontSize = 15f;
    var fontBigSize = 20f;

    init {
        isFocusable = true
        textSize = fontSize
        setTextColor(resources.getColor(R.color.colorTitle))
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        isSelected = focused
        startAnimator()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (isSelected) {
            paintUtil.onDraw(canvas)
        }
    }

    private fun startAnimator() {
        if (!isFocused) {
            AnimatorProUtil.startTextSizeAnimator(this, fontBigSize, fontSize)
        } else {
            AnimatorProUtil.startTextSizeAnimator(this, fontSize, fontBigSize)
        }
    }
}