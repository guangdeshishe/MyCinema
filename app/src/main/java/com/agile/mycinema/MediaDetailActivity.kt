package com.agile.mycinema

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.Toast
import com.agile.mycinema.Constant.Companion.HOST
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.activity_media_detail.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.util.*


class MediaDetailActivity : BaseActivity() {

    var mPlayInfoList = LinkedList<PlayInfo>()
    lateinit var mAdapter: PlayGridAdapter
    var mediaInfo: MediaInfo? = null

    companion object {
        private const val MEDIA_INFO_KEY = "media_info_key"
        fun open(context: Activity, mediaInfo: MediaInfo?) {
            if (mediaInfo == null) {
                Toast.makeText(context, "数据异常", Toast.LENGTH_SHORT).show()
                return
            }
            val intent = Intent(context, MediaDetailActivity::class.java);
            intent.putExtra(MEDIA_INFO_KEY, mediaInfo)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_detail)
        mediaInfo = intent.getParcelableExtra<MediaInfo>(MEDIA_INFO_KEY)
        if (mediaInfo == null) {
            showToast("数据异常")
            finish()
        }
        mAdapter = PlayGridAdapter(this)
        mPlayGridView.adapter = mAdapter

        mMediaPlayerContentView.mediaName = mediaInfo?.title.toString()

        OkGo.get<String>(mediaInfo?.url)
            .tag(this)
            .cacheKey("cacheKey2")            // 设置当前请求的缓存key,建议每个不同功能的请求设置一个
//            .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)    // 缓存模式，详细请看缓存介绍
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    val result = response.body()
                    val doc: Document = Jsoup.parse(result)

                    val ulElements: Elements = doc.select("#con_vod_1 ul")
                    var index = 0;
                    for (ulElement in ulElements) {
                        if (index == 1) {
                            break
                        }
                        val ulDoc: Document = Jsoup.parse(ulElement.html())
                        val liElements: Elements = ulDoc.select("li")
                        if (liElements.size == 0) {
                            continue
                        }
                        var isOk = true;
                        for (lisHtml in liElements) {
                            val liDoc: Document = Jsoup.parse(lisHtml.html())
                            val linkElements: Elements = liDoc.select("a[href]")
                            if (linkElements.size == 0) {
                                isOk = false
                                break
                            }
                            var head = "";
                            if (index == 0) {
                                head = "播放源1"
                            } else if (index == 1) {
                                head = "播放源2"
                            } else if (index == 2) {
                                head = "下载源1"
                            } else if (index == 3) {
                                head = "下载源2"
                            }
                            for (linkItem in linkElements) {
                                val title = linkItem.text()
                                val url = HOST + linkItem.attr("href")
                                val playInfo = PlayInfo(title, url)
                                log("$head-> $playInfo")
                                mPlayInfoList.add(playInfo)
                            }
                        }
                        if (isOk) {
                            index++;
                        }

                    }

                    mAdapter.initData(mPlayInfoList)

                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    Toast.makeText(
                        this@MediaDetailActivity,
                        "" + response.message(),
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            })

        mPlayGridView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                var playInfo = mAdapter.getItem(position) as PlayInfo
                OkGo.get<String>(playInfo.url)
                    .tag(this)
                    .cacheKey("cacheKey3")            // 设置当前请求的缓存key,建议每个不同功能的请求设置一个
//                    .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)    // 缓存模式，详细请看缓存介绍
                    .execute(object : StringCallback() {
                        override fun onSuccess(response: Response<String>) {
                            val result = response.body()
                            val doc: Document = Jsoup.parse(result)

                            val iframeElements: Elements = doc.select("iframe")
                            if (iframeElements.size == 0) {
                                showToast("获取播放地址失败")
                            }
                            var playUrl = iframeElements[0].attr("src");//m3u8格式
                            log(playInfo.title + " full-> " + playUrl)
                            var index = playUrl.indexOf("=")
                            playUrl = playUrl.substring(index + 1, playUrl.length)
                            log(playInfo.title + " m3u8-> " + playUrl)

                            mMediaPlayerContentView.setVideoURI(playUrl, playInfo.title);

                        }

                        override fun onError(response: Response<String>) {
                            super.onError(response)
                            Toast.makeText(
                                this@MediaDetailActivity,
                                "" + response.message(),
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                    })
            }
    }

    override fun onBackPressed() {
        if (mMediaPlayerContentView.onBackPressed()) {
            return
        }
        super.onBackPressed()
    }

    override fun onPause() {
        mMediaPlayerContentView.pause()
        super.onPause()
    }

    override fun onStop() {
        mMediaPlayerContentView.stopPlayback()
        super.onStop()
    }

    override fun onResume() {
        mMediaPlayerContentView.resume()
        super.onResume()
    }


}