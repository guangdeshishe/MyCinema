package com.agile.mycinema.moremedia

import android.content.Context
import com.agile.mycinema.CommonTextView
import com.agile.mycinema.utils.Constant.Companion.MEDIA_PADDING
import com.agile.mycinema.utils.PaintUtil

class SelectableTextView(context: Context, mediaType: String) : CommonTextView(context, null) {
    init {
        text = mediaType
        val padding = 2 * MEDIA_PADDING
        setPadding(padding, padding, padding, padding)
    }

    fun changeSelected(isSelected: Boolean) {
        PaintUtil.handleTextSelected(this, isSelected)
    }
}