package com.agile.mycinema

import android.app.Application
import android.app.UiModeManager
import android.content.res.Configuration
import com.agile.mycinema.utils.Constant
import com.lzy.okgo.OkGo
import com.lzy.okgo.cache.CacheMode
import com.umeng.commonsdk.UMConfigure
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Constant.context = applicationContext

        val uiModeManager = getSystemService(UI_MODE_SERVICE) as UiModeManager
        Constant.isTVMode = uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION

        val deviceType = if (Constant.isTVMode) {
            UMConfigure.DEVICE_TYPE_BOX
        } else {
            UMConfigure.DEVICE_TYPE_PHONE
        }
        /**
         * 注意: 即使您已经在AndroidManifest.xml中配置过appkey和channel值，也需要在App代码中调
         * 用初始化接口（如需要使用AndroidManifest.xml中配置好的appkey和channel值，
         * UMConfigure.init调用中appkey和channel参数请置为null）。
         */
        UMConfigure.init(this, "5eed8b63167eddff8a00002d", "common", deviceType, "");

        var httpBuilder = OkHttpClient.Builder()
        httpBuilder.connectTimeout(10, TimeUnit.SECONDS)//连接超时10秒
        OkGo.getInstance().init(this).okHttpClient = httpBuilder.build()
        OkGo.getInstance().cacheMode = CacheMode.REQUEST_FAILED_READ_CACHE
//        OkGo.getInstance().retryCount = 5
        Thread.setDefaultUncaughtExceptionHandler(MyUncaughtExceptionHandler())
    }
}