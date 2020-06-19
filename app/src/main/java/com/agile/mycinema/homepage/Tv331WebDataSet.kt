package com.agile.mycinema.homepage

import com.agile.mycinema.MediaInfo
import com.agile.mycinema.MediaType
import com.agile.mycinema.detail.MediaDetailActivity
import com.agile.mycinema.detail.PlayInfo
import com.agile.mycinema.moremedia.MoreMediaActivity
import com.agile.mycinema.moremedia.SubMediaType
import com.agile.mycinema.utils.LogUtil
import com.agile.mycinema.utils.NoticeUtil
import com.agile.mycinema.view.SelectAdapterLinearLayout
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.util.*

open class Tv331WebDataSet() : AbstractHomePageDataSet() {

    init {
        host = "http://www.tv331.com"

        topMovieUrl = "$host/index.php/vod/type/id/1.html"//电影排行
        topTvUrl = "$host/index.php/vod/type/id/2.html"//电视剧排行
        topTvShowUrl = "$host/index.php/vod/type/id/3.html"//综艺排行
    }

    override fun parseHomePageData(content: String, action: Int, obj: Any?) {
        val doc: Document = Jsoup.parse(content)


        mediaTitleSet[MediaType.MOVIE] = "热播电影"
        mediaMoreUrlSet[MediaType.MOVIE] = "$host/index.php/vod/type/id/1.html"

        mediaTitleSet[MediaType.TV] = "电视剧"
        mediaMoreUrlSet[MediaType.TV] = "$host/index.php/vod/type/id/2.html"
        mediaTitleSet[MediaType.TVSHOW] = "综艺"
        mediaMoreUrlSet[MediaType.TVSHOW] = "$host/index.php/vod/type/id/3.html"
//        mediaTitleSet[MediaType.MicroMovie] = "动漫"
//        mediaMoreUrlSet[MediaType.MicroMovie] = "$host/index.php/vod/type/id/4.html"

        val ulElements: Elements = doc.select("ul.video-list")
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
                var title = linkElements[1].text()
                var imageUrl = linkElements[0].attr("data-original")

                val mediaUrl = host + linkElements[0].attr("href")

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
//            val iframeElements: Elements = doc.select("iframe")
            val result = Regex("https.*\\.m3u8").findAll(content).toList()
            if (result.isEmpty()) {
                NoticeUtil.showToast("获取播放地址失败")
                return
            }
            playUrl = result[0].value
            playUrl = playUrl.substring(0, playUrl.indexOf("m3u8") + 4)
//            playUrl = iframeElements[0].attr("src")//m3u8格式
//            LogUtil.log(playInfo.summary + " full-> " + playUrl)
//            val index = playUrl.indexOf("=")
//            playUrl = playUrl.substring(index + 1, playUrl.length)
            LogUtil.log(playInfo.summary + " m3u8-> " + playUrl)
            return
        }
        mPlayInfoDataSet.clear()
        playInfoData.clear()
        mSourcePlayDataSet.clear()
        val doc: Document = Jsoup.parse(content)

        val statueBuilder = StringBuilder()
        val infoElements = doc.select("ul.content-rows li")

        for (infoElement in infoElements) {
            statueBuilder.append(infoElement.text() + "\n")
        }

        val describe = doc.select("p.detail-intro-txt").text()
        mediaDescribe = statueBuilder.toString() + "\n" + describe
//                    mMediaDataHelper.updateMediaInfo(mMediaInfo)
        LogUtil.log(statueBuilder.toString())
//                    log(actors)
//                    log(mediaType)
//                    log(director)
//                    log(area)
        LogUtil.log(describe)

        val resourceElements: Elements = doc.select("div.hd ul").select("li")
        val ulElements: Elements = doc.select("div.numList ul")

        for ((i, resource) in resourceElements.withIndex()) {
            val liElements: Elements = ulElements[i].select("li")
            val links = LinkedList<PlayInfo>()
            for (liDoc in liElements) {
                val linkItem = liDoc.select("a[href]")[0]
                val summary = linkItem.text()
                val url = host + linkItem.attr("href")
                val playInfo =
                    PlayInfo(
                        mediaInfo._id,
                        mediaInfo.title,
                        summary,
                        url
                    )
                links.add(playInfo)
            }
            val newPlayInfoData = LinkedList<PlayInfo>()
            newPlayInfoData.addAll(links)
            mPlayInfoDataSet[resource.text()] = newPlayInfoData

            val valueHolder = SelectAdapterLinearLayout.ValueHolder()
            valueHolder.mTitle = resource.text()
            valueHolder.mData = newPlayInfoData
            mSourcePlayDataSet.add(valueHolder)

            if (playInfoData.isEmpty()) {
                playInfoData.addAll(newPlayInfoData)
            }
        }

    }

    override fun parseMorePageData(content: String, action: Int, obj: Any?, mMediaInfo: MediaInfo) {
        val doc: Document = Jsoup.parse(content)
        if (action == MoreMediaActivity.ACTION_ITEM_CLICK || action == MoreMediaActivity.ACTION_LOAD_MORE) {//点击分类/加载更多
            val mediaType = obj as SubMediaType

            //获取下一页数据
            val pageInfo = doc.select("section.pag-warp a")[3]
            val pageUrl = host + pageInfo.attr("href")
            mediaType.nextPageUrl = pageUrl
            LogUtil.log("nextUrl -> " + mediaType.nextPageUrl)

            val ulElements: Elements = doc.select("ul.video-list")
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
                    var title = linkElements[1].text()
                    var imageUrl = linkElements[0].attr("data-original")

                    val mediaUrl = host + linkElements[0].attr("href")

                    var mediaInfo =
                        MediaInfo().type(type).title(title).image(imageUrl).url(mediaUrl)
                            .title(title).isHot(true)
                    mediaDatas.add(mediaInfo)
                    LogUtil.log(mediaInfo.toString())
                }
            }
            if (action == MoreMediaActivity.ACTION_ITEM_CLICK) {//点击分类
//                if (!mediaType.isSubType) {
//                    //最新、热门、评分
//                    subMediaType.clear()
//                    val subTitleElements = doc.select("div.list_ico a[href]")
//                    for (aElement in subTitleElements) {
//                        val title = aElement.text()
//                        val titleName = title.substring(1, title.length)
//                        val url = host + aElement.attr("href")
//                        subMediaType.add(SubMediaType(titleName, url, true))
//                    }
//                }
            }
            return
        }
        //加载主界面数据
        LogUtil.log(doc.title())
        val titleElements = doc.select("ul.con")[0].select("li")
        mediaTypes.clear()
//        when (mMediaInfo.type) {
//            MediaType.MOVIE -> mediaTypes.add(SubMediaType("全部类型", topMovieUrl, false))
//            MediaType.TV -> mediaTypes.add(SubMediaType("全部类型", topTvUrl, false))
//            MediaType.TVSHOW -> mediaTypes.add(SubMediaType("全部类型", topTvShowUrl, false))
//        }
        var i = 0
        for (titleElement in titleElements) {
            val aElement = titleElement.select("a[href]")
            val titleName = aElement.text()
            val url = host + aElement.attr("href")
            mediaTypes.add(SubMediaType(titleName, url, false))
            i++
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
                LogUtil.log("===热播动漫=== $media")
                media.type = MediaType.CARTOON
            }
        }
        return media.type
    }


}