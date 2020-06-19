package com.agile.mycinema.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import com.agile.mycinema.moremedia.SelectableTextView
import com.agile.mycinema.utils.LogUtil
import com.agile.mycinema.utils.UnitUtil
import java.util.*

class SelectAdapterLinearLayout(context: Context?, attrs: AttributeSet?) :
    LinearLayout(context, attrs) {

    val mDatas = LinkedList<IValueHolder>()
    var mCurrentSelectPosition = 0//默认选中第一个
    var mSelectItemClickListener: SelectItemClickListener? = null


    interface IValueHolder {

        fun getTitle(): String
        fun getData(): Any
    }

    class ValueHolder : IValueHolder {
        lateinit var mTitle: String
        lateinit var mData: Any
        override fun getTitle(): String {
            return mTitle
        }

        override fun getData(): Any {
            return mData
        }


    }


    interface SelectItemClickListener {
        fun onSelectItemClick(position: Int, data: Any)
    }

    fun initData(IValues: LinkedList<IValueHolder>) {
        mCurrentSelectPosition = 0
        mDatas.clear()
        removeAllViews()
        mDatas.addAll(IValues)
        updateView()
    }

    private fun updateView() {
        var position = -1
        for (IValueHolder: IValueHolder in mDatas) {
            position++

            var textView = SelectableTextView(
                context,
                IValueHolder.getTitle()
            )
            if (position == 0) {
                textView.changeSelected(true)
            }

            textView.setOnClickListener {
                val pos = indexOfChild(it)
                val data = mDatas[pos]
                if (mCurrentSelectPosition != pos) {
                    (getChildAt(mCurrentSelectPosition) as SelectableTextView).changeSelected(false)
                }
                mCurrentSelectPosition = pos
                textView.changeSelected(true)
                mSelectItemClickListener?.onSelectItemClick(pos, data.getData())
            }
            if (orientation == VERTICAL) {//纵向排列
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams.gravity = Gravity.CENTER
                val margin = UnitUtil.dp2px(context, 1f)
                layoutParams.setMargins(
                    0, margin, 0, margin
                )
                addView(textView, layoutParams)
            } else {//横向排列
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                val margin = UnitUtil.dp2px(context, 5f)
                layoutParams.setMargins(
                    margin, margin, margin, margin
                )
                addView(textView, layoutParams)
            }


            if (position == 0) {
                mSelectItemClickListener?.onSelectItemClick(position, IValueHolder.getData())
            }
        }
    }

    fun getCurrentData(): IValueHolder {
        return mDatas[mCurrentSelectPosition]
    }

    fun isDataEmpty(): Boolean {
        return mDatas.size == 0
    }

    fun log(message: String) {
        LogUtil.log(message)
    }
}