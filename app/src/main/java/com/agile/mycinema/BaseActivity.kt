package com.agile.mycinema

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.agile.mycinema.utils.LogUtil
import com.agile.mycinema.utils.NoticeUtil
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import java.io.File
import java.io.UnsupportedEncodingException
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap

abstract class BaseActivity : AppCompatActivity() {
    //    lateinit var mMediaDataHelper: MediaDataHelper
    val cacheDatas = HashMap<String, String>()
    val cacheKeys = HashMap<String, String>()

    companion object {
        const val ACTION_MAIN = 0//加载主界面数据
        const val ACTION_LOAD_BACKGROUND = -100//异步在线更新数据，供下次加载最新数据
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        mMediaDataHelper = MediaDataHelper.getInstance(this)
    }

    fun loadPageData(url: String) {
        loadPageData(url, ACTION_MAIN)
    }

    fun loadPageData(url: String, action: Int) {
        loadPageData(url, action, null)
    }

    fun loadPageData(url: String, action: Int, obj: Any?) {
        log("loadPageData:$url")
        val content = loadCacheData(url)
        if (content.isNullOrEmpty()) {
            loadDataOnline(url, action, obj)
            return
        }
        onLoadPageDataSuccess(content, action, obj)
        onLoadPageDataFinished(true)
        //异步获取网页最新数缓存到本地
        loadDataOnline(url, ACTION_LOAD_BACKGROUND, obj)
    }

    private fun loadDataOnline(url: String, action: Int, obj: Any?) {
        OkGo.get<String>(url)
            .tag(this)
            .cacheKey(getKey(url))            // 设置当前请求的缓存key,建议每个不同功能的请求设置一个
            .execute(object : StringCallback() {
                override fun onSuccess(response: Response<String>) {
                    val result = response.body()
                    saveCacheFile(url, result)//将数据缓存到本地文件中
                    if (action != ACTION_LOAD_BACKGROUND) {
                        onLoadPageDataSuccess(result, action, obj)
                        onLoadPageDataFinished(true)
                    }
                }

                override fun onError(response: Response<String>) {
                    super.onError(response)
                    if (action != ACTION_LOAD_BACKGROUND) {
                        onLoadPageDataFinished(false)
                        showToast("" + response.message())
                    }
                }
            })
    }

    abstract fun onLoadPageDataSuccess(content: String, action: Int, obj: Any?)

    open fun onLoadPageDataFinished(isSuccess: Boolean) {

    }

    private fun loadCacheData(url: String): String? {
        val key = getKey(url)
        if (cacheDatas.containsKey(key)) {
            return cacheDatas[key]
        }
        return loadCacheFile(key)
    }

    /**
     * 从文件中读取网页缓存数据
     */
    private fun loadCacheFile(key: String): String {
        val file = File(getFullFilePath(key))
        if (!file.exists()) {
            return ""
        }
        val content = file.readText()
        cacheDatas[key] = content
        return content
    }

    /**
     * 将网页内容缓存到文件
     */
    private fun saveCacheFile(url: String, content: String) {
        if (content.isEmpty()) {
            return
        }
        val key = getKey(url)
        val file = File(getFullFilePath(key))
        if (!file.exists()) {
            file.createNewFile()
        }
        file.writeText(content)
    }

    private fun getFullFilePath(key: String): String {
        return filesDir.absolutePath + File.separator + key
    }

    /**
     * 网页url转md5
     */
    private fun getKey(url: String): String {
        if (cacheKeys.containsKey(url)) {
            return cacheKeys[url]!!
        }
        var digest: ByteArray? = null
        try {
            val md5: MessageDigest = MessageDigest.getInstance("md5")
            digest = md5.digest(url.toByteArray(Charsets.UTF_8))
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        //16是表示转换为16进制数
        //16是表示转换为16进制数
        val result = BigInteger(1, digest).toString(16)
        cacheKeys[url] = result
        return result
    }

    fun log(message: String) {
        LogUtil.log(message)
    }

    fun showToast(message: String) {
        NoticeUtil.showToast(message)
    }

    /**
     * 过滤重复的电影
     */
    fun filterRepeatMedia(medias: LinkedList<MediaInfo>): LinkedList<MediaInfo> {
        val mediasMap = LinkedHashMap<String, MediaInfo>()
        for (media in medias) {
            if (mediasMap.containsKey(media.title)) {
                continue
            }
            mediasMap[media.title] = media
        }

        return LinkedList(mediasMap.values)
    }
}