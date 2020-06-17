package com.agile.mycinema.utils

import android.content.Context

class Constant {
    companion object {
        var SCREEN_WIDTH: Int = 0
        var SCREEN_HEIGHT: Int = 0
        var MEDIA_WIDTH_HEIGHT_SCALE = 270f / 381f
        var MEDIA_PADDING = 0
        var MEDIA_SIZE_DIFFER = 0
        var CONTENT_MARGIN = 0

        lateinit var context: Context

        var isTVMode = true
    }
}