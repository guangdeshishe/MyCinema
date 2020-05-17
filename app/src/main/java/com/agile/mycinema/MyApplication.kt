package com.agile.mycinema

import android.app.Application
import com.lzy.okgo.OkGo
import com.lzy.okgo.cache.CacheMode

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        OkGo.getInstance().init(this)
        OkGo.getInstance().cacheMode = CacheMode.REQUEST_FAILED_READ_CACHE
    }
}