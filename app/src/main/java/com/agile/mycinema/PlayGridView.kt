package com.agile.mycinema

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView

class PlayGridView(context: Context?, attrs: AttributeSet?) : WrapHeightGridView(context, attrs),
    AdapterView.OnItemSelectedListener {
    var sourceWidth = 0
    var sourceHeight = 0
    var lastSelectedView: ISelectListener? = null

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
            lastSelectedView?.onSelectChanged(false)
            lastSelectedView = null
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
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

        lastSelectedView = view as ISelectListener
    }

}