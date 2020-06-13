package com.agile.mycinema

import android.app.Application
import com.lzy.okgo.OkGo
import com.lzy.okgo.cache.CacheMode
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        var httpBuilder = OkHttpClient.Builder()
        httpBuilder.connectTimeout(10, TimeUnit.SECONDS)//连接超时10秒
        OkGo.getInstance().init(this).okHttpClient = httpBuilder.build()
        OkGo.getInstance().cacheMode = CacheMode.REQUEST_FAILED_READ_CACHE
//        OkGo.getInstance().retryCount = 5
        Thread.setDefaultUncaughtExceptionHandler(MyUncaughtExceptionHandler())
    }
}