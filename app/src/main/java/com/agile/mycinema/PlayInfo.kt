package com.agile.mycinema

class PlayInfo(_title: String, _url: String) {
    var title: String = _title
    var url: String = _url

    override fun toString(): String {
        return title + " " + url
    }
}