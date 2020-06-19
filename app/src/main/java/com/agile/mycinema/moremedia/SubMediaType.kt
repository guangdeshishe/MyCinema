package com.agile.mycinema.moremedia

import com.agile.mycinema.view.SelectAdapterLinearLayout

class SubMediaType(var mediaType: String, var url: String, var isSubType: Boolean) :
    SelectAdapterLinearLayout.IValueHolder {
    var nextPageUrl = "" //下一页链接
    var nextPage = DEFAULT_NEXT_PAGE

    companion object {
        val DEFAULT_NEXT_PAGE = 2

    }

    fun resetNextPage() {
        nextPage = DEFAULT_NEXT_PAGE
    }

    override fun getTitle(): String {
        return mediaType
    }

    override fun getData(): Any {
        return this
    }
//    UnKnow("未知",""),//未知分类
//    MovieTop("排行榜","http://m.kkkkwo.com/top_mov.html"),
//    MovieComedy("喜剧片",""),
//    MovieAction("动作片",""),
//    MovieScience("科幻片",""),
//    MovieDracula("恐怖片","");
}