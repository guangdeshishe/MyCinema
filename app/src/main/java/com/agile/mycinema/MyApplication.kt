package com.agile.mycinema

import android.app.Application
import android.app.UiModeManager
import android.content.res.Configuration
import com.agile.mycinema.utils.Constant
import com.lzy.okgo.OkGo
import com.lzy.okgo.cache.CacheMode
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Constant.context = applicationContext

        val uiModeManager = getSystemService(UI_MODE_SERVICE) as UiModeManager
        Constant.isTVMode = uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION

        var httpBuilder = OkHttpClient.Builder()
        httpBuilder.connectTimeout(10, TimeUnit.SECONDS)//连接超时10秒
        OkGo.getInstance().init(this).okHttpClient = httpBuilder.build()
        OkGo.getInstance().cacheMode = CacheMode.REQUEST_FAILED_READ_CACHE
//        OkGo.getInstance().retryCount = 5
        Thread.setDefaultUncaughtExceptionHandler(MyUncaughtExceptionHandler())
    }
}