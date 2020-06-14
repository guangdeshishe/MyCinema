package com.agile.mycinema

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import com.agile.mycinema.utils.Constant.Companion.HOST
import kotlinx.android.synthetic.main.activity_media_detail.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.util.*


class MediaDetailActivity : BaseActivity() {

    lateinit var mAdapter: PlayGridAdapter
    lateinit var mMediaInfo: MediaInfo

    companion object {
        private const val MEDIA_INFO_KEY = "media_info_key"
        const val ACTION_ITEM_CLICK = 1//点击某一集
        fun open(context: Activity, mediaInfo: MediaInfo?) {
            if (mediaInfo == null) {
                Toast.makeText(context, "数据异常", Toast.LENGTH_SHORT).show()
                return
            }
            val intent = Intent(context, MediaDetailActivity::class.java)
            intent.putExtra(MEDIA_INFO_KEY, mediaInfo)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_detail)
        val tmpMediaInfo = intent.getParcelableExtra<MediaInfo>(MEDIA_INFO_KEY)
        mMediaInfo = tmpMediaInfo ?: MediaInfo()
//        if (mMediaInfo._id.isEmpty()) {
//            showToast("数据异常")
//            finish()
//        }
        mMediaPlayerContentView.mediaName = mMediaInfo.title
        mTitleView.text = mMediaInfo.title

        mTitleDescribeView.setOnClickListener {
            mDescribeFullContextView.visibility = View.VISIBLE
            mDescribeFullContextView.requestFocus()
        }
        mDescribeFullContextView.setOnClickListener {
            mDescribeFullContextView.visibility = View.GONE
            mTitleDescribeView.requestFocus()
        }
        mAdapter = PlayGridAdapter(this)
        mPlayGridView.adapter = mAdapter

        log("detailPage: " + mMediaInfo.url)
        loadPageData(mMediaInfo.url, ACTION_MAIN)//加载网页数据

        mPlayGridView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                handleItemClick(position)
            }
    }

    override fun onLoadPageDataSuccess(content: String, action: Int, obj: Any?) {
        if (action == ACTION_ITEM_CLICK) {
            val doc: Document = Jsoup.parse(content)
            val playInfo = obj as PlayInfo
            val iframeElements: Elements = doc.select("iframe")
            if (iframeElements.size == 0) {
                showToast("获取播放地址失败")
            }
            var playUrl = iframeElements[0].attr("src")//m3u8格式
            log(playInfo.summary + " full-> " + playUrl)
            val index = playUrl.indexOf("=")
            playUrl = playUrl.substring(index + 1, playUrl.length)
            log(playInfo.summary + " m3u8-> " + playUrl)
            playInfo.videoUrl = playUrl
            showToast(playUrl)
            mMediaPlayerContentView.setVideoURI(playUrl, playInfo.summary)
            return
        }
        val doc: Document = Jsoup.parse(content)
        val statue = doc.getElementsByClass("clear  fn-left")[0].text()
//                    val actors = doc.getElementsByClass("vw100 clear")[0].text()
//                    val mediaType = doc.getElementsByClass("vw100 fn-left")[0].text()
//                    val director = doc.getElementsByClass("vw50 fn-left")[0].text()
//                    val area = doc.getElementsByClass("vw50 yc fn-right")[0].text()
        val describe = doc.getElementById("con_vod_2").text()
        mMediaInfo.describe = statue + "\n" + describe
//                    mMediaDataHelper.updateMediaInfo(mMediaInfo)
        log(statue)
//                    log(actors)
//                    log(mediaType)
//                    log(director)
//                    log(area)
        log(describe)

        val ulElements: Elements = doc.select("#con_vod_1 ul")
        var index = 0
        var playInfoData = LinkedList<PlayInfo>()
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
                    val url = HOST + linkItem.attr("href")
                    val playInfo =
                        PlayInfo(mMediaInfo._id, mMediaInfo.title, summary, url)
                    log("$head-> $playInfo")
                    playInfoData.add(playInfo)
//                                mMediaDataHelper.updateMediaPlayInfo(playInfo)
//                                mPlayInfoList.add(playInfo)
                }
            }
            if (isOk) {
                index++
            }

        }
        mDescribeView.text = mMediaInfo.describe
        mDescribeFullView.text = mMediaInfo.describe
        val newPlayInfoData = LinkedList<PlayInfo>()
        newPlayInfoData.addAll(playInfoData.reversed())//倒序排泄

        mAdapter.initData(newPlayInfoData)
        mPlayGridView.postDelayed({
            mPlayGridView.requestFocus()
            val selectedItem = 0
            mPlayGridView.setSelection(selectedItem)
            handleItemClick(selectedItem)
        }, 500)
    }

    fun handleItemClick(position: Int) {
        val playInfo = mAdapter.getItem(position) as PlayInfo
        mAdapter.selectedPosition = position
        mAdapter.notifyDataSetChanged()
        if (playInfo.videoUrl.isNotEmpty()) {
            mMediaPlayerContentView.setVideoURI(playInfo.videoUrl, playInfo.summary)
            return
        }
        loadPageData(playInfo.url, ACTION_ITEM_CLICK, playInfo)

    }

    override fun onBackPressed() {
        if (mDescribeFullContextView.visibility == View.VISIBLE) {
            mDescribeFullContextView.visibility = View.GONE
            mTitleDescribeView.requestFocus()
            return
        }
        if (mMediaPlayerContentView.onBackPressed()) {
            return
        }
        super.onBackPressed()
    }

    override fun onPause() {
        mMediaPlayerContentView.pause()
        super.onPause()
    }

    override fun onDestroy() {
        mMediaPlayerContentView.stopPlayback()
        super.onStop()
    }

    override fun onResume() {
        mMediaPlayerContentView.resume()
        super.onResume()
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        mMediaPlayerContentView.dispatchKeyEvent(event)
        return super.dispatchKeyEvent(event)
    }

}