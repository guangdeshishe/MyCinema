package com.agile.mycinema

class PlayInfo() {
    var _id = ""
    var mediaId: String = ""
    var summary: String = ""
    var title: String = ""
    var url: String = ""
    var videoUrl: String = ""

    constructor(_mediaId: String, _title: String, _summary: String, _url: String) : this() {
        mediaId = _mediaId
        title = _title
        summary = _summary
        url = _url
    }

    override fun toString(): String {
        return title + " " + url
    }
}