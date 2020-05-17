package com.agile.mycinema

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.grideview_media_item_view.view.*

class MediaItemView(context: Context) : FrameLayout(context) {

    var paint = Paint(Paint.ANTI_ALIAS_FLAG)
    lateinit var mImageView: ImageView
    lateinit var mParentView: View
    lateinit var mContentView: View

    init {
        inflate(context, R.layout.grideview_media_item_view, this)
        mParentView = findViewById(R.id.mItemParentView)
        mImageView = findViewById(R.id.mImageView)
        mContentView = findViewById(R.id.mItemParentView)

        var itemParentWidth = Constant.SCREEN_WIDTH / 6
        var itemParentHeight = itemParentWidth * 381 / 270

        var layoutParams = mParentView.layoutParams
        layoutParams.width = itemParentWidth
        layoutParams.height = itemParentHeight
        layoutParams = mContentView.layoutParams

        layoutParams.width = itemParentWidth * 2 / 3
        layoutParams.height = itemParentHeight * 2 / 3



        setWillNotDraw(false)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        paint.color = Color.RED
    }

    fun setData(mediaInfo: MediaInfo) {
        Glide.with(context).load(mediaInfo.image).into(mImageView);
        mTitleView.text = mediaInfo.title
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (isSelected) {
            canvas?.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        }
    }
}