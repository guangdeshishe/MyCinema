package com.agile.mycinema

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View

class PaintUtil(_view: View) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var view = _view

    init {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = Constant.MEDIA_PADDING.toFloat()
        paint.color = Color.WHITE

        view.setPadding(
            Constant.MEDIA_PADDING,
            Constant.MEDIA_PADDING,
            Constant.MEDIA_PADDING,
            Constant.MEDIA_PADDING
        )
        view.setWillNotDraw(false)
    }

    fun onDraw(canvas: Canvas?) {
        if (canvas == null) {
            return
        }
        //            canvas?.drawRoundRect(
//                0f,
//                0f,
//                width.toFloat(),
//                height.toFloat(),
//                Constant.MEDIA_PADDING.toFloat()*2,
//                Constant.MEDIA_PADDING.toFloat()*2,
//                paint
        canvas.drawRect(
            0f,
            0f,
            view.width.toFloat(),
            view.height.toFloat(),
            paint
        )
    }
}