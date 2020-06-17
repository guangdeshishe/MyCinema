package com.agile.mycinema.homepage

import com.agile.mycinema.MediaInfo
import com.agile.mycinema.MediaType
import com.agile.mycinema.detail.MediaDetailActivity
import com.agile.mycinema.detail.PlayInfo
import com.agile.mycinema.utils.LogUtil
import com.agile.mycinema.utils.NoticeUtil
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

class L090ys1WebDataSet : AbstractHomePageDataSet() {

    init {
        host = "http://1090ys1.com"
    }

    override fun parseHomePageData(content: String, action: Int, obj: Any?) {
        val doc: Document = Jsoup.parse(content)

        val contentElements = doc.getElementsByClass("col-lg-wide-75 col-xs-1 padding-0")
        for (contentElement in contentElements) {
            val titleInfo = contentElement.getElementsByClass("title")[0]
            val titleLink = titleInfo.select("a[href]")[0]
            val titleName = titleLink.text()
            val url = host + titleLink.attr("href")

            val ulElement = contentElement.select("ul")[1]
            val liElements: Elements = ulElement.select("li")
            for (lisHtml in liElements) {
                val linkItem = lisHtml.select("a[href]")[0]
                var title = linkItem.attr("title")
                var imageUrl = linkItem.attr("data-original")
                val mediaUrl = host + linkItem.attr("href")

                var mediaInfo =
                    MediaInfo().title(title).image(imageUrl).url(mediaUrl)
                        .title(title)
                val type = if ("电影" == titleName) {
                    MediaType.MOVIE
                } else {
                    MediaType.TV
                }
                mediaInfo.type = type

                when (type) {
                    MediaType.MOVIE -> movieDatas.add(mediaInfo)
                    MediaType.TV -> tvDatas.add(mediaInfo)
                }
            }

            if ("电影" == titleName) {
                mediaTitleSet[MediaType.MOVIE] = titleName
                mediaMoreUrlSet[MediaType.MOVIE] = url
            }
        }

        mediaTitleSet[MediaType.TV] = "电视剧"
        mediaMoreUrlSet[MediaType.TV] = ""


    }

    override fun parsePlayPageData(content: String, action: Int, obj: Any?, mediaInfo: MediaInfo) {
        val doc: Document = Jsoup.parse(content)
        if (action == MediaDetailActivity.ACTION_ITEM_CLICK) {
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
        val statueBuilder = StringBuilder()
        val infoElements = doc.getElementsByClass("data")
        for (infoElement in infoElements) {
            statueBuilder.append(infoElement.text())
        }

        val describe = doc.getElementsByClass("detail-content").text()
        mediaDescribe = statueBuilder.toString() + "\n" + describe
        LogUtil.log(statueBuilder.toString())
        LogUtil.log(describe)

        playInfoData.clear()
        val ulElements: Elements = doc.select("#play_1 ul")
        for (ulElement in ulElements) {

            val ulDoc: Document = Jsoup.parse(ulElement.html())
            val liElements: Elements = ulDoc.select("li")
            if (liElements.size == 0) {
                continue
            }
            for (lisHtml in liElements) {
                val liDoc: Document = Jsoup.parse(lisHtml.html())
                val linkElements: Elements = liDoc.select("a[href]")
                if (linkElements.size == 0) {
                    break
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
                    LogUtil.log("$playInfo")
                    playInfoData.add(playInfo)
                }
            }
        }

    }

    override fun parseMorePageData(content: String, action: Int, obj: Any?, mediaInfo: MediaInfo) {

    }
}