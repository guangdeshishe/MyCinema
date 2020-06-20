package com.agile.mycinema

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.AdapterView
import com.agile.mycinema.utils.Constant

class MediaGridView(context: Context?, attrs: AttributeSet?) : WrapHeightGridView(context, attrs),
    AdapterView.OnItemSelectedListener {
    var sourceWidth = 0
    var sourceHeight = 0
    var lastSelectedView: ISelectListener? = null
    var customerListener: AdapterView.OnItemSelectedListener? = null

    init {
        onItemSelectedListener = this
    }

    override fun onFocusChanged(gainFocus: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        if (!gainFocus) {
            clearLastItem()
        } else {
            handleItemSelect(selectedView)
        }
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        clearLastItem()
    }

    fun clearLastItem() {
        if (lastSelectedView != null) {
            val oldView = (lastSelectedView as ISelectListener).getContentView()
            lastSelectedView?.onSelectChanged(false)
//            val diffValue = 1000f * Constant.MEDIA_SIZE_DIFFER
//            val oldAnimator = getAnimator(oldView, diffValue, 0f)
//            oldAnimator.start()
            lastSelectedView = null
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        customerListener?.onItemSelected(parent, view, position, id)
        handleItemSelect(view)
    }

    private fun handleItemSelect(view: View?) {
        if (view == null || !isFocused) {
            return
        }


        if (view is ISelectListener) {
            view.onSelectChanged(true)
        }
        lastSelectedView?.onSelectChanged(false)

        val diffValue = 1000f * Constant.MEDIA_SIZE_DIFFER
        val newView = (view as ISelectListener).getContentView()
        if (sourceWidth == 0) {
            sourceWidth = newView.measuredWidth
            sourceHeight = newView.measuredHeight
        }

        var newAnimator = getAnimator(newView, 0f, diffValue)

        var animatorSet = AnimatorSet()
        var builder = animatorSet.play(newAnimator)
        if (lastSelectedView != null && lastSelectedView != newView) {
            val oldView = (lastSelectedView as ISelectListener).getContentView()
            val oldAnimator = getAnimator(oldView, diffValue, 0f)
            builder.with(oldAnimator)
        }
        animatorSet.duration = 300
        animatorSet.interpolator = LinearInterpolator()
        animatorSet.start()

        lastSelectedView = view as ISelectListener
    }

    private fun getAnimator(view: View, from: Float, to: Float): ValueAnimator {
        var animator = ValueAnimator.ofFloat(from, to)
        animator.addUpdateListener {
            var diff = (it.animatedValue) as Float / 1000f
            var layoutParams = view.layoutParams
            layoutParams.width = (sourceWidth + diff).toInt()
            layoutParams.height = (sourceHeight + diff).toInt()
            view.layoutParams = layoutParams
        }
        return animator
    }
}