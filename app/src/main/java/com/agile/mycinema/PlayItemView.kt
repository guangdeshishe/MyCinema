package com.agile.mycinema

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.grideview_media_item_view.view.*

class PlayItemView(context: Context) : FrameLayout(context), ISelectListener {

    var paintUtil = PaintUtil(this)
    var fontSize = 15f;
    var fontBigSize = 17f;

    init {
        inflate(context, R.layout.grideview_play_item_view, this)


    }

    fun setData(mediaInfo: PlayInfo, isSelected: Boolean) {
        mTitleView.text = mediaInfo.summary
        if (isSelected) {
            mTitleView.setBackgroundColor(Color.parseColor("#ffffff"))
            mTitleView.setTextColor(ContextCompat.getColor(context, R.color.colorTheme))
        } else {
            mTitleView.setBackgroundColor(Color.parseColor("#77000000"))
            mTitleView.setTextColor(Color.parseColor("#ffffff"))
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (isSelected) {
            paintUtil.onDraw(canvas)
        }
    }


    override fun onSelectChanged(select: Boolean) {
        isSelected = select
        if (!isSelected) {
            AnimatorProUtil.startTextSizeAnimator(mTitleView, fontBigSize, fontSize)
        } else {
            AnimatorProUtil.startTextSizeAnimator(mTitleView, fontSize, fontBigSize)
        }
        invalidate()
    }

    override fun getContentView(): View {
        return this
    }
}