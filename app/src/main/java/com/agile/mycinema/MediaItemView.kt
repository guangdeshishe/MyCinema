package com.agile.mycinema

import android.content.Context
import android.graphics.Canvas
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.agile.mycinema.utils.Constant
import com.agile.mycinema.utils.Constant.Companion.MEDIA_WIDTH_HEIGHT_SCALE
import com.agile.mycinema.utils.PaintUtil
import com.bumptech.glide.Glide

class MediaItemView(context: Context) : FrameLayout(context), ISelectListener {

    lateinit var mImageView: ImageView
    lateinit var mParentView: View
    lateinit var mContentView: View
    lateinit var mTitleView: TextView
    var paintUtil = PaintUtil(this)
    var fontSize = 15f;
    var fontBigSize = 20f;

    init {
        inflate(context, R.layout.grideview_media_item_view, this)
        mParentView = findViewById(R.id.mItemParentView)
        mImageView = findViewById(R.id.mImageView)
        mContentView = findViewById(R.id.mItemContentView)
        mTitleView = findViewById(R.id.mTitleView)
        mTitleView.textSize = fontSize

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

    }

    fun setData(mediaInfo: MediaInfo) {
        Glide.with(context).load(mediaInfo.image).error(R.mipmap.default_img).into(mImageView);
        mTitleView.text = mediaInfo.title
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
        return mContentView
    }


}