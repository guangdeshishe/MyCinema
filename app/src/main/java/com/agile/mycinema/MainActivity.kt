package com.agile.mycinema

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.AdapterView
import android.widget.ScrollView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.agile.mycinema.detail.MediaDetailActivity
import com.agile.mycinema.homepage.AbstractHomePageDataSet
import com.agile.mycinema.homepage.KankanwuWebDataSet
import com.agile.mycinema.homepage.KkkkwoWebDataSet
import com.agile.mycinema.moremedia.MoreMediaActivity
import com.agile.mycinema.utils.Constant
import com.agile.mycinema.utils.UnitUtil
import kotlinx.android.synthetic.main.activity_main.*


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

    companion object {
        var WebPageDataSet: AbstractHomePageDataSet =
//            KkkkwoWebDataSet()
            KankanwuWebDataSet()
//            L090ys1WebDataSet()
    }


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

        mKankanwuWebSite.changeSelected(true)
        mKankanwuWebSite.setOnClickListener {
            mKankanwuWebSite.changeSelected(true)
            mKkkkwoWebSite.changeSelected(false)
            mContentScrollView.fullScroll(ScrollView.FOCUS_UP)
            WebPageDataSet = KankanwuWebDataSet()
            initView()
        }
        mKkkkwoWebSite.setOnClickListener {
            mKankanwuWebSite.changeSelected(false)
            mKkkkwoWebSite.changeSelected(true)
            mContentScrollView.fullScroll(ScrollView.FOCUS_UP)
            WebPageDataSet = KkkkwoWebDataSet()
            initView()
        }

        requestPermission()
    }

    fun initView() {
        loadPageData(WebPageDataSet.host)

    }

    override fun onLoadPageDataSuccess(content: String, action: Int, obj: Any?) {
        WebPageDataSet.parseHomePageData(content, action, obj)

        mHotMovieTitle.text = WebPageDataSet.mediaTitleSet[MediaType.MOVIE]
        val movieUrl = WebPageDataSet.mediaMoreUrlSet[MediaType.MOVIE]
        if (!movieUrl.isNullOrEmpty()) {
            mHotMovieTitle.setOnClickListener {
                MoreMediaActivity.open(
                    this,
                    MediaInfo().type(MediaType.MOVIE).url(movieUrl)
                )
            }
        }

        mHotTVTitle.text = WebPageDataSet.mediaTitleSet[MediaType.TV]
        val tvUrl = WebPageDataSet.mediaMoreUrlSet[MediaType.TV]
        if (!tvUrl.isNullOrEmpty()) {
            mHotTVTitle.setOnClickListener {
                MoreMediaActivity.open(
                    this,
                    MediaInfo().type(MediaType.TV).url(tvUrl)
                )
            }
        }

        mHotTVShowTitle.text = WebPageDataSet.mediaTitleSet[MediaType.TVSHOW]
        val tvShowUrl = WebPageDataSet.mediaMoreUrlSet[MediaType.TVSHOW]
        if (!tvShowUrl.isNullOrEmpty()) {
            mHotTVShowTitle.setOnClickListener {
                MoreMediaActivity.open(
                    this,
                    MediaInfo().type(MediaType.TVSHOW).url(tvShowUrl)
                )
            }
        }
        mHotMicroMovieTitle.text = WebPageDataSet.mediaTitleSet[MediaType.MicroMovie]
        val microMovieUrl = WebPageDataSet.mediaMoreUrlSet[MediaType.MicroMovie]
        if (!microMovieUrl.isNullOrEmpty()) {
            mHotMicroMovieTitle.setOnClickListener {
                MoreMediaActivity.open(
                    this,
                    MediaInfo().type(MediaType.MicroMovie).url(microMovieUrl)
                )
            }
        }

        mHotMovieAdapter.initData(filterRepeatMedia(WebPageDataSet.movieDatas))
        mHotTVAdapter.initData(filterRepeatMedia(WebPageDataSet.tvDatas))
        mHotTvShowAdapter.initData(WebPageDataSet.tvShowDatas)
        mHotMicroMovieAdapter.initData(WebPageDataSet.microMovieDatas)
        mHotMovieGridView.postDelayed({ mHotMovieTitle.requestFocus() }, 500)

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
