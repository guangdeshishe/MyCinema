package com.agile.mycinema

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.grideview_media_item_view.view.*

class PlayItemView(context: Context) : FrameLayout(context) {

    var paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        inflate(context, R.layout.grideview_play_item_view, this)

        setWillNotDraw(false)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        paint.color = Color.RED
    }

    fun setData(mediaInfo: PlayInfo) {
        mTitleView.text = mediaInfo.title
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (isSelected) {
            canvas?.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        }
    }
}