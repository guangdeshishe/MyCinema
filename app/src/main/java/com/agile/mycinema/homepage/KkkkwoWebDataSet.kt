package com.agile.mycinema.homepage

import com.agile.mycinema.MediaInfo
import com.agile.mycinema.MediaType
import com.agile.mycinema.detail.MediaDetailActivity
import com.agile.mycinema.detail.PlayInfo
import com.agile.mycinema.moremedia.MoreMediaActivity
import com.agile.mycinema.moremedia.SubMediaType
import com.agile.mycinema.utils.LogUtil
import com.agile.mycinema.utils.NoticeUtil
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.util.*

open class KkkkwoWebDataSet() : AbstractHomePageDataSet() {

    init {
        host = "http://m.kkkkwo.com"

        topMovieUrl = "$host/top_mov.html"//电影排行
        topTvUrl = "$host/top_tv.html"//电视剧排行
        topTvShowUrl = "$host/top_variety.html"//综艺排行
        topMicroMovieUrl = "$host/top_weidy.html"//微电影排行
    }

    override fun parseHomePageData(content: String, action: Int, obj: Any?) {
        val doc: Document = Jsoup.parse(content)

        val titleElements = doc.select("div.modo_title")
        for (titleElement in titleElements) {
            val aElement = titleElement.select("a[href]")[0]
            val titleName = aElement.attr("title")
            val url = host + aElement.attr("href")
            if ("电影" == titleName) {
                mediaTitleSet[MediaType.MOVIE] = aElement.text()
                mediaMoreUrlSet[MediaType.MOVIE] = url
            } else if ("电视剧" == titleName) {
                mediaTitleSet[MediaType.TV] = aElement.text()
                mediaMoreUrlSet[MediaType.TV] = url
            } else if ("综艺" == titleName) {
                mediaTitleSet[MediaType.TVSHOW] = aElement.text()
                mediaMoreUrlSet[MediaType.TVSHOW] = url
            } else if ("微电影" == titleName) {
                mediaTitleSet[MediaType.MicroMovie] = aElement.text()
                mediaMoreUrlSet[MediaType.MicroMovie] = url
            }
        }

        val ulElements: Elements = doc.select("ul.list_tab_img")
        var index = 0;
        movieDatas.clear()
        tvDatas.clear()
        tvShowDatas.clear()
        microMovieDatas.clear()
        for (ulElement in ulElements) {

            val ulDoc: Document = Jsoup.parse(ulElement.html())
            val liElements: Elements = ulDoc.select("li")

            for (lisHtml in liElements) {
                val liDoc: Document = Jsoup.parse(lisHtml.html())
                val linkElements: Elements = liDoc.select("a[href]")
                val imageElements: Elements = liDoc.select("img[src]")

                val linkItem: Element = linkElements[0]
                val imageItem: Element = imageElements[0]
                var title = linkItem.attr("title")
                var imageUrl = imageItem.attr("src")

                val mediaUrl = host + linkItem.attr("href")

                var mediaInfo =
                    MediaInfo().title(title).image(imageUrl).url(mediaUrl)
                        .title(title).isHot(true)
                val type = updateMediaType(index, mediaInfo);

                when (type) {
                    MediaType.MOVIE -> movieDatas.add(mediaInfo)
                    MediaType.TV -> tvDatas.add(mediaInfo)
                    MediaType.TVSHOW -> tvShowDatas.add(mediaInfo)
                    MediaType.MicroMovie -> microMovieDatas.add(mediaInfo)
                }
            }
            index++;
        }
    }

    override fun parsePlayPageData(content: String, action: Int, obj: Any?, mediaInfo: MediaInfo) {
        if (action == MediaDetailActivity.ACTION_ITEM_CLICK) {
            val doc: Document = Jsoup.parse(content)
            val playInfo = obj as PlayInfo
            val iframeElements: Elements = doc.select("iframe")
            if (iframeElements.size == 0) {
                NoticeUtil.showToast("获取播放地址失败")
            }
            playUrl = iframeElements[0].attr("src")//m3u8格式
            LogUtil.log(playInfo.summary + " full-> " + playUrl)
            val index = playUrl.indexOf("=")
            playUrl = playUrl.substring(index + 1, playUrl.length)
            LogUtil.log(playInfo.summary + " m3u8-> " + playUrl)
            return
        }
        playInfoData.clear()
        val doc: Document = Jsoup.parse(content)

        val statueBuilder = StringBuilder()
        val infoElements = doc.getElementsByTag("p")
        var i = 0
        for (infoElement in infoElements) {
            if (i > 1) {
                statueBuilder.append(infoElement.text() + "\n")
            }
            i++
        }

        val describe = doc.getElementById("con_vod_2").text()
        mediaDescribe = statueBuilder.toString() + "\n" + describe
//                    mMediaDataHelper.updateMediaInfo(mMediaInfo)
        LogUtil.log(statueBuilder.toString())
//                    log(actors)
//                    log(mediaType)
//                    log(director)
//                    log(area)
        LogUtil.log(describe)

        val ulElements: Elements = doc.select("#con_vod_1 ul")
        var index = 0
        for (ulElement in ulElements) {
            if (index == 1) {//只取第一个播放源
                break
            }
            val ulDoc: Document = Jsoup.parse(ulElement.html())
            val liElements: Elements = ulDoc.select("li")
            if (liElements.size == 0) {
                continue
            }
            var isOk = true
            for (lisHtml in liElements) {
                val liDoc: Document = Jsoup.parse(lisHtml.html())
                val linkElements: Elements = liDoc.select("a[href]")
                if (linkElements.size == 0) {
                    isOk = false
                    break
                }
                var head = ""
                when (index) {
                    0 -> {
                        head = "播放源1"
                    }
                    1 -> {
                        head = "播放源2"
                    }
                    2 -> {
                        head = "下载源1"
                    }
                    3 -> {
                        head = "下载源2"
                    }
                }
                for (linkItem in linkElements) {
                    val summary = linkItem.text()
                    val url = host + linkItem.attr("href")
                    val playInfo =
                        PlayInfo(
                            mediaInfo._id,
                            mediaInfo.title,
                            summary,
                            url
                        )
                    LogUtil.log("$head-> $playInfo")
                    playInfoData.add(playInfo)
                }
            }
            if (isOk) {
                index++
            }

        }

        val newPlayInfoData = LinkedList<PlayInfo>()
        newPlayInfoData.addAll(playInfoData.reversed())//倒序排泄
        playInfoData = newPlayInfoData
    }

    override fun parseMorePageData(content: String, action: Int, obj: Any?, mMediaInfo: MediaInfo) {
        val doc: Document = Jsoup.parse(content)
        if (action == MoreMediaActivity.ACTION_ITEM_CLICK || action == MoreMediaActivity.ACTION_LOAD_MORE) {//点击分类/加载更多
            val mediaType = obj as SubMediaType

            //获取下一页数据
            val pageInfoElements = doc.select("div.ui-vpages a[data]")
            for (pageInfo in pageInfoElements) {
                val pageUrl = host + pageInfo.attr("href")
                val value = pageInfo.text()
                if (mediaType.nextPage.toString() == value) {
                    mediaType.nextPage++
                    mediaType.nextPageUrl = pageUrl
                    break
                }
            }
            LogUtil.log("nextUrl -> " + mediaType.nextPageUrl)

            val ulElements: Elements = doc.select("ul")
            mediaDatas.clear()
            for (ulElement in ulElements) {
                val ulDoc: Document = Jsoup.parse(ulElement.html())
                val liElements: Elements = ulDoc.select("li")
                var type = MediaType.UnKnow;
                if (liElements.size == 0) {
                    continue
                }
                for (lisHtml in liElements) {
                    val liDoc: Document = Jsoup.parse(lisHtml.html())
                    val linkElements: Elements = liDoc.select("a[href]")
                    val imageElements: Elements = liDoc.select("img[src]")
                    if (linkElements.size == 0 || imageElements.size == 0) {
                        break
                    }
                    val linkItem: Element = linkElements[0]
                    val imageItem: Element = imageElements[0]
                    var title = linkItem.attr("title")
                    var imageUrl = imageItem.attr("src")

                    val mediaUrl = host + linkItem.attr("href")

                    var mediaInfo =
                        MediaInfo().type(type).title(title).image(imageUrl).url(mediaUrl)
                            .title(title).isHot(true)
                    mediaDatas.add(mediaInfo)
                    LogUtil.log(mediaInfo.toString())
                }
            }
            if (action == MoreMediaActivity.ACTION_ITEM_CLICK) {//点击分类
                if (!mediaType.isSubType) {
                    //最新、热门、评分
                    subMediaType.clear()
                    val subTitleElements = doc.select("div.list_ico a[href]")
                    for (aElement in subTitleElements) {
                        val title = aElement.text()
                        val titleName = title.substring(1, title.length)
                        val url = host + aElement.attr("href")
                        subMediaType.add(SubMediaType(titleName, url, true))
                    }
                }
            }
            return
        }
        //加载主界面数据
        LogUtil.log(doc.title())
        val titleElements = doc.select("div.modo_title")
        mediaTypes.clear()
        when (mMediaInfo.type) {
            MediaType.MOVIE -> mediaTypes.add(SubMediaType("排行榜", topMovieUrl, false))
            MediaType.TV -> mediaTypes.add(SubMediaType("排行榜", topTvUrl, false))
            MediaType.TVSHOW -> mediaTypes.add(SubMediaType("排行榜", topTvShowUrl, false))
            MediaType.MicroMovie -> mediaTypes.add(
                SubMediaType(
                    "排行榜",
                    topMicroMovieUrl,
                    false
                )
            )
        }

        for (titleElement in titleElements) {
            val aElement = titleElement.select("a[href]")
            val titleName = aElement.attr("title")
            val url = host + aElement.attr("href")
            mediaTypes.add(SubMediaType(titleName, url, false))
        }
    }

    fun updateMediaType(index: Int, media: MediaInfo): MediaType {
        when (index) {
            0 -> {
                LogUtil.log("===热播电影=== $media")
                media.type = MediaType.MOVIE
            }
            1 -> {
                LogUtil.log("===热播电影=== $media")
                media.type = MediaType.MOVIE
            }
            2 -> {
                LogUtil.log("===正在热映(电影)=== $media")
                media.type = MediaType.MOVIE
            }
            3 -> {
                LogUtil.log("===热播电视剧=== $media")
                media.type = MediaType.TV
            }
            4 -> {
                LogUtil.log("===正在热映(电视剧)=== $media")
                media.type = MediaType.TV
            }
            5 -> {
                LogUtil.log("===正在热映(电视剧)=== $media")
                media.type = MediaType.TV
            }
            6 -> {
                LogUtil.log("===热播动漫=== $media")
                media.type = MediaType.CARTOON
            }
            7 -> {
                LogUtil.log("===热播综艺=== $media")
                media.type = MediaType.TVSHOW
            }
            8 -> {
                LogUtil.log("===热播微电影=== $media")
                media.type = MediaType.MicroMovie
            }
        }
        return media.type
    }


}