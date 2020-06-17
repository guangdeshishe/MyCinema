package com.agile.mycinema.moremedia

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.AdapterView
import com.agile.mycinema.BaseActivity
import com.agile.mycinema.MainActivity.Companion.WebPageDataSet
import com.agile.mycinema.MediaGridAdapter
import com.agile.mycinema.MediaInfo
import com.agile.mycinema.R
import com.agile.mycinema.detail.MediaDetailActivity
import kotlinx.android.synthetic.main.activity_more_media.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.*


class MoreMediaActivity : BaseActivity(),
    AdapterView.OnItemClickListener, MediaTypeListView.MediaTypeClickListener,
    AbsListView.OnScrollListener, AdapterView.OnItemSelectedListener {

    private lateinit var mMediaDataAdapter: MediaGridAdapter
    private lateinit var mMediaInfo: MediaInfo

    companion object {
        val MEDIA_INFO_KEY = "media_type_key"
        const val ACTION_ITEM_CLICK = 1//点击分类
        const val ACTION_LOAD_MORE = 2//加载更多数据
        var isLoadingData = false

        fun open(content: Context, mediaInfo: MediaInfo) {
            val intent = Intent(content, MoreMediaActivity::class.java)
            intent.putExtra(MEDIA_INFO_KEY, mediaInfo)
            content.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mMediaInfo = intent.getParcelableExtra(MEDIA_INFO_KEY)

        setContentView(R.layout.activity_more_media)
        mMediaDataAdapter = MediaGridAdapter(this)
        mMediaDataGridView.adapter = mMediaDataAdapter

        mMediaDataGridView.onItemClickListener = this
        mMediaDataGridView.setOnScrollListener(this)

        mMediaTypeContentView.mMediaTypeClickListener = this
        mMediaSubTypeContentView.mMediaTypeClickListener = this

        mMediaDataGridView.mListener = this
        initView()

    }

    override fun onLoadPageDataFinished(isSuccess: Boolean) {
        isLoadingData = false
    }

    override fun onLoadPageDataSuccess(content: String, action: Int, obj: Any?) {
        val doc: Document = Jsoup.parse(content)
        if (action == ACTION_ITEM_CLICK || action == ACTION_LOAD_MORE) {//点击分类/加载更多
            val mediaType = obj as SubMediaType

            if (mMediaTypeContentView.getCurrentMediaType() != mediaType
                && (mMediaSubTypeContentView.isDataEmpty() || mMediaSubTypeContentView.getCurrentMediaType() != mediaType)
            ) {//返回的数据不是当前分类下的
                return
            }
            WebPageDataSet.parseMorePageData(content, action, obj, mMediaInfo)


            if (action == ACTION_LOAD_MORE) {//加载更多
                mMediaDataAdapter.addData(WebPageDataSet.mediaDatas)
            } else {//初始化数据
                if (!mediaType.isSubType) {
                    //最新、热门、评分

                    mMediaSubTypeContentView.initData(WebPageDataSet.subMediaType)
                    if (WebPageDataSet.subMediaType.size > 0) {
                        mMediaSubTypeMainContentView.visibility = View.VISIBLE
                    } else {
                        mMediaSubTypeMainContentView.visibility = View.GONE
                    }
                }

                mMediaDataAdapter.initData(WebPageDataSet.mediaDatas)
                mMediaDataGridView.postDelayed({
                    mMediaDataGridView.requestFocus()
                    mMediaDataGridView.scrollTo(0, 0)
                }, 500)
            }
            return
        }
        WebPageDataSet.parseMorePageData(content, action, obj, mMediaInfo)
        //加载主界面数据
        mMediaTypeContentView.initData(WebPageDataSet.mediaTypes)
    }

    fun initView() {
        log("initView")
        loadPageData(mMediaInfo.url)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var mediaInfo: MediaInfo? = null
        mediaInfo = mMediaDataAdapter.getItem(position) as MediaInfo

        MediaDetailActivity.open(
            this@MoreMediaActivity,
            mediaInfo
        )
    }

    override fun onMediaTypeClick(position: Int, mediaType: SubMediaType) {
        if (mediaType.url.isEmpty()) {
            mMediaDataAdapter.initData(LinkedList())
            return
        }
        mediaType.resetNextPage()
        mMediaDataAdapter.clear()
        log("onMediaTypeClick:$position")
        loadPageData(mediaType.url, ACTION_ITEM_CLICK, mediaType)
    }

    override fun onScroll(
        view: AbsListView?,
        firstVisibleItem: Int,
        visibleItemCount: Int,
        totalItemCount: Int
    ) {

    }

    override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
        when (scrollState) {
            AbsListView.OnScrollListener.SCROLL_STATE_IDLE ->                 // 判断滚动到底部
                checkLoadNext()
        }
    }

    private fun checkLoadNext() {
        log("checkLoadNext")
        val view = mMediaDataGridView
        if (view.lastVisiblePosition == view.count - 1) {
            if (isLoadingData) {
                showToast("数据加载中,请稍候")
                return
            }
            val mediaType = mMediaTypeContentView.getCurrentMediaType()
            if (mediaType.nextPageUrl.isEmpty()) {
                showToast("没有更多数据了")
                return
            }
            log("loadNextUrl = ${mediaType.nextPageUrl}")
            isLoadingData = true
            loadPageData(mediaType.nextPageUrl, ACTION_LOAD_MORE, mediaType)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        checkLoadNext()
    }

}
