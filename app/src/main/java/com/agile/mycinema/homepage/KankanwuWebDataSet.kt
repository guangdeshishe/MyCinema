package com.agile.mycinema.homepage

class KankanwuWebDataSet() : KkkkwoWebDataSet() {

    init {
        host = "https://s.kankanwu.com"

        topMovieUrl = "$host/top_mov.html"//电影排行
        topTvUrl = "$host/top_tv.html"//电视剧排行
        topTvShowUrl = "$host/top_variety.html"//综艺排行
        topMicroMovieUrl = "$host/top_weidy.html"//微电影排行
    }
}