package com.agile.mycinema

enum class MediaType() {
    UnKnow(),//未知分类
    MOVIE(), //电影
    TV(),//电视剧
    CARTOON(), //卡通动漫
    TVSHOW(),//综艺节目
    MicroMovie();//微电影

//    fun toMediaType(v: Int): MediaType {
//        return when (v) {
//            0 -> MOVIE
//            1 -> TV
//            2 -> CARTOON
//            3 -> TVSHOW
//            4 -> MicroMovie
//            else -> MOVIE
//        }
//    }
}