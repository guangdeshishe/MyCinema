package com.agile.mycinema.moremedia

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import com.agile.mycinema.MediaGridAdapter
import com.agile.mycinema.utils.LogUtil
import java.util.*

class MediaTypeListView(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    val mDatas = LinkedList<SubMediaType>()
    var mCurrentSelectPosition = 0//默认选中第一个
    lateinit var mMediaDataAdapter: MediaGridAdapter
    var mMediaTypeClickListener: MediaTypeClickListener? = null

    init {
        orientation = VERTICAL
    }

    interface MediaTypeClickListener {
        fun onMediaTypeClick(position: Int, mediaType: SubMediaType)
    }

    fun initData(mediaTypes: LinkedList<SubMediaType>) {
        mDatas.clear()
        mDatas.addAll(mediaTypes)
        updateView()
    }

    private fun updateView() {
        var position = -1
        for (mediaType: SubMediaType in mDatas) {
            position++
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.gravity = Gravity.CENTER
            var textView = SelectableTextView(context, mediaType.mediaType)
            if (position == 0) {
                textView.changeSelected(true)
            }
            textView.setOnClickListener {
                val pos = indexOfChild(it)
                if (mCurrentSelectPosition != pos) {
                    (getChildAt(mCurrentSelectPosition) as SelectableTextView).changeSelected(false)
                }
                mCurrentSelectPosition = pos
                textView.changeSelected(true)
                mMediaTypeClickListener?.onMediaTypeClick(pos, mediaType)
            }
            addView(textView, layoutParams)

            if (position == 0) {
                mMediaTypeClickListener?.onMediaTypeClick(position, mediaType)
            }
        }

    }

    fun getCurrentMediaType(): SubMediaType {
        return mDatas[mCurrentSelectPosition]
    }


    fun log(message: String) {
        LogUtil.log(message)
    }
}