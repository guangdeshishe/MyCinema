package com.agile.mycinema.detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.Toast
import com.agile.mycinema.BaseActivity
import com.agile.mycinema.MainActivity.Companion.WebPageDataSet
import com.agile.mycinema.MediaInfo
import com.agile.mycinema.R
import com.agile.mycinema.utils.Constant
import com.agile.mycinema.utils.NoticeUtil
import com.agile.mycinema.view.SelectAdapterLinearLayout
import kotlinx.android.synthetic.main.activity_media_detail.*
import java.util.*


class MediaDetailActivity : BaseActivity(), SelectAdapterLinearLayout.SelectItemClickListener {

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
        window.setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        );//保持屏幕不变黑
        setContentView(R.layout.activity_media_detail)
        val tmpMediaInfo = intent.getParcelableExtra<MediaInfo>(
            MEDIA_INFO_KEY
        )
        mMediaInfo = tmpMediaInfo ?: MediaInfo()
        mSourcesPlayList.mSelectItemClickListener = this
        mMediaPlayerContentView.mediaName = mMediaInfo.title
        mTitleView.text = mMediaInfo.title

        mTitleDescribeView.setOnClickListener {
            mDescribeFullView.visibility = View.VISIBLE
            mDescribeFullView.requestFocus()
        }
        mDescribeView.setOnClickListener {
            mDescribeFullView.visibility = View.VISIBLE
            mDescribeFullView.requestFocus()
        }
        mDescribeFullView.movementMethod = ScrollingMovementMethod.getInstance();
//
        mAdapter = PlayGridAdapter(this)
        mPlayGridView.adapter = mAdapter

        log("detailPage: " + mMediaInfo.url)
        loadPageData(
            mMediaInfo.url,
            ACTION_MAIN
        )//加载网页数据

        mPlayGridView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                handleItemClick(position)
            }

        mPlayGridView.numColumns = if (Constant.isTVMode) {
            9
        } else {
            7
        }
    }

    override fun onLoadPageDataSuccess(content: String, action: Int, obj: Any?) {
        WebPageDataSet.parsePlayPageData(content, action, obj, mMediaInfo)
        val lastVideoUrl = mMediaDataHelper.getMediaVideoUrl(mMediaInfo.url)
        if (action == ACTION_ITEM_CLICK) {
            val playInfo = obj as PlayInfo
            var playUrl = WebPageDataSet.playUrl
            playInfo.videoUrl = playUrl
            if (playUrl.contains(".html")) {
                NoticeUtil.showToast("暂不支持播放")
            } else {
                var progress: Long = 0
                if (lastVideoUrl == playInfo.url) {
                    progress = mMediaDataHelper.getMediaPlayProgress(mMediaInfo.url)
                }
                mMediaPlayerContentView.setVideoURI(
                    playUrl,
                    playInfo.summary,
                    progress
                )
            }
            return
        }

        mMediaInfo.describe = WebPageDataSet.mediaDescribe
        mDescribeView.text = mMediaInfo.describe
        mDescribeFullView.text = mMediaInfo.describe

        mSourcesPlayList.initData(WebPageDataSet.mSourcePlayDataSet)
//        mAdapter.initData(WebPageDataSet.mPlayInfoDataSet.get(WebPageDataSet.mSourcePlayDataSet[0].getTitle()))
        mAdapter.initData(WebPageDataSet.playInfoData)
        mPlayGridView.postDelayed({
            mPlayGridView.requestFocus()
            var selectedItem = 0
            if (lastVideoUrl.isNotEmpty()) {
                for ((index, playInfo) in WebPageDataSet.playInfoData.withIndex()) {
                    if (lastVideoUrl == playInfo.url) {
                        selectedItem = index
                    }
                }
            }
            mPlayGridView.setSelection(selectedItem)
            handleItemClick(selectedItem)
        }, 500)
    }

    fun handleItemClick(position: Int) {
        val playInfo = mAdapter.getItem(position) as PlayInfo
        mAdapter.selectedPosition = position
        mAdapter.notifyDataSetChanged()
        if (playInfo.videoUrl.isNotEmpty()) {
            mMediaPlayerContentView.setVideoURI(
                playInfo.videoUrl,
                playInfo.summary,
                mMediaDataHelper.getMediaPlayProgress(playInfo.videoUrl)
            )
            return
        }
        loadPageData(
            playInfo.url,
            ACTION_ITEM_CLICK, playInfo
        )

    }

    override fun onBackPressed() {
        if (mDescribeFullView.visibility == View.VISIBLE) {
            mDescribeFullView.visibility = View.GONE
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
        val playInfo = mAdapter.getItem(mAdapter.selectedPosition) as PlayInfo
        mMediaDataHelper.updatePlayProgressInfo(
            mMediaInfo.url,
            playInfo.url,
            mMediaPlayerContentView.getPlayProgress()
        )
    }

    override fun onDestroy() {
        mMediaPlayerContentView.stopPlayback()
        super.onDestroy()
    }

    override fun onResume() {
        mMediaPlayerContentView.resume()
        super.onResume()
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        mMediaPlayerContentView.dispatchKeyEvent(event)
        return super.dispatchKeyEvent(event)
    }

    override fun onSelectItemClick(position: Int, data: Any) {
        val playInfo = data as LinkedList<PlayInfo>
        mAdapter.initData(playInfo)
    }

}