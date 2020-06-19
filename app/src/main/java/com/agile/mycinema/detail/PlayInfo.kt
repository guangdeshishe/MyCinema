package com.agile.mycinema.detail

import com.agile.mycinema.view.SelectAdapterLinearLayout

class PlayInfo() : SelectAdapterLinearLayout.IValueHolder {
    var _id = ""
    var mediaId: String = ""
    var summary: String = ""
    var text: String = ""
    var url: String = ""
    var videoUrl: String = ""

    constructor(_mediaId: String, _title: String, _summary: String, _url: String) : this() {
        mediaId = _mediaId
        text = _title
        summary = _summary
        url = _url
    }

    override fun getTitle(): String {
        return text
    }

    override fun getData(): Any {
        return this
    }

    override fun toString(): String {
        return text + " " + url
    }
}