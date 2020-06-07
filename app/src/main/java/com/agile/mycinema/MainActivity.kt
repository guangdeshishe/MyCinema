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
import com.agile.mycinema.Constant.Companion.HOST
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.activity_main.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements


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
//        mHotCartoonAdapter = MediaGridAdapter(this)
//        mHotMovieGridView.adapter = mHotCartoonAdapter
//        mHotTvShowAdapter = MediaGridAdapter(this)
//        mHotMovieGridView.adapter = mHotTvShowAdapter
//        mHotMicroMovieAdapter = MediaGridAdapter(this)
//        mHotMovieGridView.adapter = mHotMicroMovieAdapter
        mSuggestMediaGridView.onItemClickListener = this
        mHotMovieGridView.onItemClickListener = this

        mHotTVGridView.onItemClickListener = this

        requestPermission()
    }

    fun loadCacheData() {
        var SuggestMedias = mMediaDataHelper.getAllMediaInfo(MediaType.Suggest, true)
        var HotMovies = mMediaDataHelper.getAllMediaInfo(MediaType.MOVIE, true)
        var HotTV = mMediaDataHelper.getAllMediaInfo(MediaType.TV, true)
        if (HotMovies.size > 0) {
            isLoadedCache = true
            mSuggestMediaAdapter.initData(SuggestMedias)
            mHotMovieAdapter.initData(HotMovies)
            mHotTVAdapter.initData(HotTV)
            mHotMovieGridView.postDelayed({ mSuggestMediaGridView.requestFocus() }, 500)
        }
    }

    fun initView() {
        loadCacheData()
//        showToast("后台正在更新网络数据")
        var host = HOST
        OkGo.get<String>(host)
            .tag(this)
            .cacheKey("cacheKey1")            // 设置当前请求的缓存key,建议每个不同功能的请求设置一个
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    val result = response.body()
                    val doc: Document = Jsoup.parse(result)
                    log(doc.title())
//                    Toast.makeText(this@MainActivity, "" + doc.title(), Toast.LENGTH_LONG).show()
                    val ulElements: Elements = doc.select("ul")
                    var index = 0;
                    for (ulElement in ulElements) {
                        val ulDoc: Document = Jsoup.parse(ulElement.html())
                        val liElements: Elements = ulDoc.select("li")
                        var type = MediaType.MOVIE;
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
                            val mediaUrl = host + linkItem.attr("href")

                            var mediaInfo = MediaInfo(type, title, imageUrl, mediaUrl, title, true)
                            addMedia(index, mediaInfo);
                        }
                        if (isOk) {
                            index++;
                        }
                    }
//                    if(!isLoadedCache){
//                        showToast("在线读取数据成功")
                    loadCacheData()
//                    }

                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    Toast.makeText(this@MainActivity, "" + response.message(), Toast.LENGTH_LONG)
                        .show()
                }
            })
    }

    fun addMedia(index: Int, media: MediaInfo) {
        when (index) {
            0 -> {
                log("===轮播数据=== $media")
                media.type = MediaType.Suggest
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
        mMediaDataHelper.updateMediaInfo(media)
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
        if (parent == mSuggestMediaGridView) {
            mediaInfo = mSuggestMediaAdapter.getItem(position) as MediaInfo

        } else if (parent == mHotMovieGridView) {
            mediaInfo = mHotMovieAdapter.getItem(position) as MediaInfo

        } else if (parent == mHotTVGridView) {
            mediaInfo = mHotTVAdapter.getItem(position) as MediaInfo
        }
        MediaDetailActivity.open(this@MainActivity, mediaInfo)
    }

}
