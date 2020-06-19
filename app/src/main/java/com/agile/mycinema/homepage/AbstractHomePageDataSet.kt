package com.agile.mycinema.homepage

import com.agile.mycinema.MediaInfo
import com.agile.mycinema.MediaType
import com.agile.mycinema.detail.PlayInfo
import com.agile.mycinema.view.SelectAdapterLinearLayout
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap

abstract class AbstractHomePageDataSet() {

    var movieDatas = LinkedList<MediaInfo>()
    var tvDatas = LinkedList<MediaInfo>()
    var tvShowDatas = LinkedList<MediaInfo>()
    var microMovieDatas = LinkedList<MediaInfo>()

    var mediaTitleSet = HashMap<MediaType, String>()
    var mediaMoreUrlSet = HashMap<MediaType, String>()

    var host = ""

    var playUrl = ""//视频播放链接

    //    var playInfoData = LinkedList<PlayInfo>()
    var mediaDescribe = ""//影片介绍
    var mPlayInfoDataSet = LinkedHashMap<String, LinkedList<PlayInfo>>()//剧集信息
    var playInfoData = LinkedList<PlayInfo>()
    val mSourcePlayDataSet = LinkedList<SelectAdapterLinearLayout.IValueHolder>()//数据来源

    var topMovieUrl = ""//电影排行
    var topTvUrl = ""//电视剧排行
    var topTvShowUrl = ""//综艺排行
    var topMicroMovieUrl = ""//微电影排行
    val mediaTypes = LinkedList<SelectAdapterLinearLayout.IValueHolder>()//分类数据
    val mediaDatas = LinkedList<MediaInfo>()//加载更多的数据
    val subMediaType = LinkedList<SelectAdapterLinearLayout.IValueHolder>()//子分类数据

    abstract fun parseHomePageData(content: String, action: Int, obj: Any?)

    abstract fun parsePlayPageData(content: String, action: Int, obj: Any?, mediaInfo: MediaInfo)

    abstract fun parseMorePageData(content: String, action: Int, obj: Any?, mediaInfo: MediaInfo)

}