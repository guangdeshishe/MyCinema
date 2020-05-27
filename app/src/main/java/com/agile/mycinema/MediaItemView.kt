package com.agile.mycinema

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.agile.mycinema.Constant.Companion.MEDIA_WIDTH_HEIGHT_SCALE
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.grideview_media_item_view.view.*

class MediaItemView(context: Context) : FrameLayout(context), ISelectListener {

    var paint = Paint(Paint.ANTI_ALIAS_FLAG)
    lateinit var mImageView: ImageView
    lateinit var mParentView: View
    lateinit var mContentView: View
    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
    }

    init {
        inflate(context, R.layout.grideview_media_item_view, this)
        mParentView = findViewById(R.id.mItemParentView)
        mImageView = findViewById(R.id.mImageView)
        mContentView = findViewById(R.id.mItemContentView)

        var itemParentWidth = Constant.SCREEN_WIDTH / 6
        var itemParentHeight = (itemParentWidth / MEDIA_WIDTH_HEIGHT_SCALE).toInt()

        var parentLP = mParentView.layoutParams
        parentLP.width = itemParentWidth
        parentLP.height = itemParentHeight
        mParentView.layoutParams = parentLP

        var contentLP = mContentView.layoutParams
        contentLP.width = (parentLP.width - Constant.MEDIA_SIZE_DIFFER)
        contentLP.height = (parentLP.height - Constant.MEDIA_SIZE_DIFFER)
        mContentView.layoutParams = contentLP
        post {

        }


        setPadding(
            Constant.MEDIA_PADDING,
            Constant.MEDIA_PADDING,
            Constant.MEDIA_PADDING,
            Constant.MEDIA_PADDING
        )

        setWillNotDraw(false)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = Constant.MEDIA_PADDING.toFloat()
        paint.color = Color.WHITE
    }

    fun setData(mediaInfo: MediaInfo) {
        Glide.with(context).load(mediaInfo.image).into(mImageView);
        mTitleView.text = mediaInfo.title
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (isSelected) {
//            canvas?.drawRoundRect(
//                0f,
//                0f,
//                width.toFloat(),
//                height.toFloat(),
//                Constant.MEDIA_PADDING.toFloat()*2,
//                Constant.MEDIA_PADDING.toFloat()*2,
//                paint
            canvas?.drawRect(
                0f,
                0f,
                width.toFloat(),
                height.toFloat(),
                paint
            )
        }
    }

    override fun onSelectChanged(select: Boolean) {
        isSelected = select
        invalidate()
    }

    override fun getContentView(): View {
        return mContentView
    }
}