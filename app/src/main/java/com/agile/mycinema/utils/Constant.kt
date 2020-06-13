package com.agile.mycinema.utils

class Constant {
    companion object {
        var SCREEN_WIDTH: Int = 0
        var SCREEN_HEIGHT: Int = 0
        var MEDIA_WIDTH_HEIGHT_SCALE = 270f / 381f
        var MEDIA_PADDING = 0
        var MEDIA_SIZE_DIFFER = 0
        var CONTENT_MARGIN = 0
        var HOST = "http://m.kkkkwo.com"
        var MEDIA_TOP_URL = "$HOST/top_mov.html"//电影排行
        var MEDIA_TOP_TV_URL = "$HOST/top_tv.html"//电视剧排行
        var MEDIA_TOP_TV_SHOW_URL = "$HOST/top_variety.html"//综艺排行
        var MEDIA_TOP_Micro_Movie_URL = "$HOST/top_weidy.html"//微电影排行
    }
}