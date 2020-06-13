package com.agile.mycinema.moremedia

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.AdapterView
import com.agile.mycinema.*
import com.agile.mycinema.utils.Constant
import com.agile.mycinema.utils.Constant.Companion.HOST
import com.agile.mycinema.utils.Constant.Companion.MEDIA_TOP_Micro_Movie_URL
import com.agile.mycinema.utils.Constant.Companion.MEDIA_TOP_TV_SHOW_URL
import com.agile.mycinema.utils.Constant.Companion.MEDIA_TOP_TV_URL
import com.agile.mycinema.utils.Constant.Companion.MEDIA_TOP_URL
import kotlinx.android.synthetic.main.activity_more_media.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
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

        mMediaTypeContentView.mMediaDataAdapter = mMediaDataAdapter
        mMediaTypeContentView.mMediaTypeClickListener = this

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

            if (mMediaTypeContentView.getCurrentMediaType() != mediaType) {//返回的数据不是当前分类下的
                return
            }

            //获取下一页数据
            val pageInfoElements = doc.select("div.ui-vpages a[data]")
            for (pageInfo in pageInfoElements) {
                val pageUrl = HOST + pageInfo.attr("href")
                val value = pageInfo.text()
                if (mediaType.nextPage.toString() == value) {
                    mediaType.nextPage++
                    mediaType.nextPageUrl = pageUrl
                    break
                }
            }
            log("nextUrl -> " + mediaType.nextPageUrl)

            val ulElements: Elements = doc.select("ul")
            val mediaDatas = LinkedList<MediaInfo>()
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

                    val mediaUrl = Constant.HOST + linkItem.attr("href")

                    var mediaInfo =
                        MediaInfo().type(type).title(title).image(imageUrl).url(mediaUrl)
                            .title(title).isHot(true)
                    mediaDatas.add(mediaInfo)
                    log(mediaInfo.toString())
                }
            }
            if (action == ACTION_LOAD_MORE) {//加载更多
                mMediaDataAdapter.addData(mediaDatas)
            } else {//初始化数据
                mMediaDataAdapter.initData(mediaDatas)
            }
            return
        }
        //加载主界面数据
        log(doc.title())
        val titleElements = doc.select("div.modo_title")
        val mediaTypes = LinkedList<SubMediaType>()
        when (mMediaInfo.type) {
            MediaType.MOVIE -> mediaTypes.add(SubMediaType("排行榜", MEDIA_TOP_URL))
            MediaType.TV -> mediaTypes.add(SubMediaType("排行榜", MEDIA_TOP_TV_URL))
            MediaType.TVSHOW -> mediaTypes.add(SubMediaType("排行榜", MEDIA_TOP_TV_SHOW_URL))
            MediaType.MicroMovie -> mediaTypes.add(SubMediaType("排行榜", MEDIA_TOP_Micro_Movie_URL))
        }

        for (titleElement in titleElements) {
            val aElement = titleElement.select("a[href]")
            val titleName = aElement.attr("title")
            val url = Constant.HOST + aElement.attr("href")
            mediaTypes.add(SubMediaType(titleName, url))
        }
        mMediaTypeContentView.initData(mediaTypes)
    }

    fun initView() {
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
