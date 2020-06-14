package com.agile.mycinema

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.agile.mycinema.moremedia.MoreMediaActivity
import com.agile.mycinema.utils.Constant
import com.agile.mycinema.utils.Constant.Companion.HOST
import com.agile.mycinema.utils.UnitUtil
import kotlinx.android.synthetic.main.activity_main.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.util.*


class MainActivity : BaseActivity(),
    AdapterView.OnItemClickListener {


    private val permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.READ_PHONE_STATE
    ) //需要的权限
    private lateinit var mSuggestMediaAdapter: MediaGridAdapter
    private lateinit var mHotMovieAdapter: MediaGridAdapter
    private lateinit var mHotTVAdapter: MediaGridAdapter
    private lateinit var mHotCartoonAdapter: MediaGridAdapter
    private lateinit var mHotTvShowAdapter: MediaGridAdapter
    private lateinit var mHotMicroMovieAdapter: MediaGridAdapter
    private var isLoadedCache = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val outMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(outMetrics)
        Constant.SCREEN_WIDTH = outMetrics.widthPixels
        Constant.SCREEN_HEIGHT = outMetrics.heightPixels
        Constant.MEDIA_PADDING = UnitUtil.dp2px(this, 5f)
        Constant.CONTENT_MARGIN = UnitUtil.dp2px(this, 10f)
        Constant.MEDIA_SIZE_DIFFER = 4 * Constant.MEDIA_PADDING

        log("screenWidth:" + Constant.SCREEN_WIDTH + ";screenHeight:" + Constant.SCREEN_HEIGHT)


        setContentView(R.layout.activity_main)
        mSuggestMediaAdapter = MediaGridAdapter(this)
        mSuggestMediaGridView.adapter = mSuggestMediaAdapter

        mHotMovieAdapter = MediaGridAdapter(this)
        mHotMovieGridView.adapter = mHotMovieAdapter

        mHotTVAdapter = MediaGridAdapter(this)
        mHotTVGridView.adapter = mHotTVAdapter

        mHotTvShowAdapter = MediaGridAdapter(this)
        mHotTVShowGridView.adapter = mHotTvShowAdapter

        mHotMicroMovieAdapter = MediaGridAdapter(this)
        mHotMicroMovieGridView.adapter = mHotMicroMovieAdapter

        mSuggestMediaGridView.onItemClickListener = this
        mHotMovieGridView.onItemClickListener = this
        mHotTVGridView.onItemClickListener = this
        mHotTVShowGridView.onItemClickListener = this
        mHotMicroMovieGridView.onItemClickListener = this

        requestPermission()
    }

//    fun loadCacheData() {
//        var HotMovies =
//            mMediaDataHelper.getAllMediaInfo(MediaInfo().type(MediaType.MOVIE).isHot(true))
//        var HotTV = mMediaDataHelper.getAllMediaInfo(MediaInfo().type(MediaType.TV).isHot(true))
//        if (HotMovies.size > 0) {
//            isLoadedCache = true
//            mHotMovieAdapter.initData(HotMovies)
//            mHotTVAdapter.initData(HotTV)
//            mHotMovieGridView.postDelayed({ mHotMovieGridView.requestFocus() }, 500)
//        }
//    }

    fun initView() {
        loadPageData(HOST)

    }

    override fun onLoadPageDataSuccess(content: String, action: Int, obj: Any?) {
        val doc: Document = Jsoup.parse(content)
        log(doc.title())
        val ulElements: Elements = doc.select("ul")
        var index = 0;

        val titleElements = doc.select("div.modo_title")
        for (titleElement in titleElements) {
            val aElement = titleElement.select("a[href]")[0]
            val titleName = aElement.attr("title")
            val url = HOST + aElement.attr("href")
            if ("电影" == titleName) {
                mHotMovieTitle.text = aElement.text()
                mHotMovieTitle.setOnClickListener {
                    MoreMediaActivity.open(
                        this,
                        MediaInfo().type(MediaType.MOVIE).url(url)
                    )
                }
            } else if ("电视剧" == titleName) {
                mHotTVTitle.text = aElement.text()
                mHotTVTitle.setOnClickListener {
                    MoreMediaActivity.open(
                        this,
                        MediaInfo().type(MediaType.TV).url(url)
                    )
                }
            } else if ("综艺" == titleName) {
                mHotTVShowTitle.text = aElement.text()
                mHotTVShowTitle.setOnClickListener {
                    MoreMediaActivity.open(
                        this,
                        MediaInfo().type(MediaType.TVSHOW).url(url)
                    )
                }
            } else if ("微电影" == titleName) {
                mHotMicroMovieTitle.text = aElement.text()
                mHotMicroMovieTitle.setOnClickListener {
                    MoreMediaActivity.open(
                        this,
                        MediaInfo().type(MediaType.MicroMovie).url(url)
                    )
                }
            }
        }

        var movieDatas = LinkedList<MediaInfo>()
        var tvDatas = LinkedList<MediaInfo>()
        var tvShowDatas = LinkedList<MediaInfo>()
        var microMovieDatas = LinkedList<MediaInfo>()
        for (ulElement in ulElements) {

            val ulDoc: Document = Jsoup.parse(ulElement.html())
            val liElements: Elements = ulDoc.select("li")
            if (liElements.size == 0) {
                continue
            }
            var isOk = true;
            for (lisHtml in liElements) {
                val liDoc: Document = Jsoup.parse(lisHtml.html())
                val linkElements: Elements = liDoc.select("a[href]")
                val imageElements: Elements = liDoc.select("img[src]")
                if (linkElements.size == 0 || imageElements.size == 0) {
                    isOk = false
                    break
                }
                val linkItem: Element = linkElements[0]
                val imageItem: Element = imageElements[0]
                var title = linkItem.attr("title")
                var imageUrl = imageItem.attr("src")
                if (index == 0) {//顶部轮播
                    imageUrl = imageItem.attr("data-src")
                    title = liDoc.select("em")[0].text()
                }
                val mediaUrl = Constant.HOST + linkItem.attr("href")

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
            if (isOk) {
                index++;
            }
        }
        mHotMovieAdapter.initData(filterRepeatMedia(movieDatas))
        mHotTVAdapter.initData(filterRepeatMedia(tvDatas))
        mHotTvShowAdapter.initData(tvShowDatas)
        mHotMicroMovieAdapter.initData(microMovieDatas)
        mHotMovieGridView.postDelayed({ mHotMovieTitle.requestFocus() }, 500)

    }

    fun updateMediaType(index: Int, media: MediaInfo): MediaType {
        when (index) {
            0 -> {
                log("===轮播数据=== $media")
                return MediaType.UnKnow

            }
            1 -> {
                log("===热播电影=== $media")
                media.type = MediaType.MOVIE
            }
            2 -> {
                log("===正在热映(电影)=== $media")
                media.type = MediaType.MOVIE
            }
            3 -> {
                log("===热播电视剧=== $media")
                media.type = MediaType.TV
            }
            4 -> {
                log("===正在热映(电视剧)=== $media")
                media.type = MediaType.TV
            }
            5 -> {
                log("===热播动漫=== $media")
                media.type = MediaType.CARTOON
            }
            6 -> {
                log("===热播综艺=== $media")
                media.type = MediaType.TVSHOW
            }
            7 -> {
                log("===热播微电影=== $media")
                media.type = MediaType.MicroMovie
            }
        }
        return media.type
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var isPermissionOkCount = 0
        for (i in grantResults.indices) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                isPermissionOkCount++
            }
        }
        if (isPermissionOkCount == grantResults.size) { //全部权限都同意了
            initView()
        } else {
            Toast.makeText(this, "权限不够，无法正常使用", Toast.LENGTH_LONG).show()
        }
    }

    private fun requestPermission() { // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) { // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                ActivityCompat.requestPermissions(this, permissions, 0)
            } else {
                ActivityCompat.requestPermissions(this, permissions, 0)
            }
        } else {
            initView()
        }
    }


    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var mediaInfo: MediaInfo? = null
        when (parent) {
            mSuggestMediaGridView -> {
                mediaInfo = mSuggestMediaAdapter.getItem(position) as MediaInfo
            }
            mHotMovieGridView -> {
                mediaInfo = mHotMovieAdapter.getItem(position) as MediaInfo
            }
            mHotTVGridView -> {
                mediaInfo = mHotTVAdapter.getItem(position) as MediaInfo
            }
            mHotTVShowGridView -> {
                mediaInfo = mHotTvShowAdapter.getItem(position) as MediaInfo
            }
            mHotMicroMovieGridView -> {
                mediaInfo = mHotMicroMovieAdapter.getItem(position) as MediaInfo
            }
        }
        MediaDetailActivity.open(this@MainActivity, mediaInfo)
    }

}
