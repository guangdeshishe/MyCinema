package com.agile.mycinema.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.agile.mycinema.MediaInfo
import com.agile.mycinema.MediaType
import com.agile.mycinema.PlayInfo
import com.agile.mycinema.utils.DateTimeUtil
import java.util.*


class MediaDataHelper(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {

    companion object {
        private const val DB_NAME = "MediaData.db"
        const val MEDIA_INFO_TABLE = "MediaInfo"//影视信息
        const val MEDIA_PLAY_INFO_TABLE = "MediaPlayInfo"//视频播放链接
        const val VALUE_ID = "_id"
        const val MEDIA_ID = "mediaId";
        const val MEDIA_TITLE = "title";
        const val MEDIA_SUMMARY = "summary";//例如：第一集
        const val MEDIA_IMAGE_URL = "imageUrl";
        const val MEDIA_PAGE_URL = "pageUrl";//影视详情界面
        const val MEDIA_TYPE = "type";
        const val MEDIA_DESCRIBE = "describe";//影视介绍
        const val MEDIA_IS_HOT = "isHot";//是否热门
        const val MEDIA_PLAY_URL = "playUrl";//播放详情界网址
        const val MEDIA_VIDEO_URL = "videoUrl";//真正的视频播放链接
        const val MEDIA_UPDATE_TIME = "updateTime";//更新时间
        private lateinit var instance: MediaDataHelper
        fun getInstance(context: Context): MediaDataHelper {
            if (!this::instance.isInitialized) {
                instance = MediaDataHelper(context.applicationContext, DB_NAME, null, 1)
            }
            return instance
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createMediaInfoTable =
            "create table $MEDIA_INFO_TABLE(" +
                    "_id integer primary key autoincrement ," +
                    "$MEDIA_TITLE text default ''," +
                    "$MEDIA_IMAGE_URL text default ''," +
                    "$MEDIA_PAGE_URL text default '', " +
                    "$MEDIA_TYPE text default ''," +
                    "$MEDIA_DESCRIBE text default ''," +
                    "$MEDIA_IS_HOT integer default 0," +
                    "$MEDIA_UPDATE_TIME date)"
        val createMediaPlayUrlTable =
            "create table $MEDIA_PLAY_INFO_TABLE(" +
                    "_id integer primary key autoincrement," +
                    "$MEDIA_ID integer," +
                    "$MEDIA_TITLE text default ''," +
                    "$MEDIA_SUMMARY text default '', " +
                    "$MEDIA_PLAY_URL text default ''," +
                    "$MEDIA_VIDEO_URL text default ''," +
                    "$MEDIA_UPDATE_TIME date)"
        db.execSQL(createMediaInfoTable)
        db.execSQL(createMediaPlayUrlTable)
    }

    fun updateMediaPlayInfo(
        playInfo: PlayInfo
    ) {
        val db: SQLiteDatabase = writableDatabase
        val cv = ContentValues()
        if (playInfo.mediaId.isNotEmpty()) {
            cv.put(MEDIA_ID, playInfo.mediaId)
        }
        if (playInfo.title.isNotEmpty()) {
            cv.put(MEDIA_TITLE, playInfo.title)
        }
        if (playInfo.summary.isNotEmpty()) {
            cv.put(MEDIA_SUMMARY, playInfo.summary)
        }
        if (playInfo.url.isNotEmpty()) {
            cv.put(MEDIA_PLAY_URL, playInfo.url)
        }
        if (playInfo.videoUrl.isNotEmpty()) {
            cv.put(MEDIA_VIDEO_URL, playInfo.videoUrl)
        }
        cv.put(MEDIA_UPDATE_TIME, DateTimeUtil.getNowDateTime())

        var oldPlayInfo = getPlayInfo(playInfo.mediaId, playInfo.summary)
        if (oldPlayInfo == null) {//新增
            db.insert(MEDIA_PLAY_INFO_TABLE, null, cv)
        } else {//更新
            db.update(MEDIA_PLAY_INFO_TABLE, cv, "$VALUE_ID=?", arrayOf(playInfo._id))
        }
        db.close()
    }

    fun updateMediaInfo(
        mediaInfo: MediaInfo
    ) {
        val db: SQLiteDatabase = writableDatabase
        val cv = ContentValues()
        if (mediaInfo.title.isNotEmpty()) {
            cv.put(MEDIA_TITLE, mediaInfo.title)
        }
        if (mediaInfo.image.isNotEmpty()) {
            cv.put(MEDIA_IMAGE_URL, mediaInfo.image)
        }
        if (mediaInfo.url.isNotEmpty()) {
            cv.put(MEDIA_PAGE_URL, mediaInfo.url)
        }
        cv.put(MEDIA_TYPE, mediaInfo.type.toString())
        if (mediaInfo.describe.isNotEmpty()) {
            cv.put(MEDIA_DESCRIBE, mediaInfo.describe)
        }
        cv.put(MEDIA_UPDATE_TIME, DateTimeUtil.getNowDateTime())
        val oldMediaInfo = getMediaInfo(mediaInfo.title)
        if (oldMediaInfo == null) {//新增
            val isHot = if (mediaInfo.isHot) {
                1
            } else {
                0
            }
            cv.put(MEDIA_IS_HOT, isHot)
            db.insert(MEDIA_INFO_TABLE, null, cv)
        } else {//更新
            db.update(MEDIA_INFO_TABLE, cv, "$VALUE_ID=?", arrayOf(mediaInfo._id))
        }
        db.close()
    }

    private fun getMediaInfo(title: String): MediaInfo? {
        val cursor =
            writableDatabase.query(
                MEDIA_INFO_TABLE, null, "title = ?",
                arrayOf(title), null, null, null
            )
        var mediaInfo: MediaInfo? = null
        if (cursor.moveToFirst()) {
            mediaInfo = toMediaInfo(cursor)
        }
        return mediaInfo
    }

    private fun getPlayInfo(mediaId: String, summary: String): PlayInfo? {
        val cursor =
            writableDatabase.query(
                MEDIA_PLAY_INFO_TABLE, null, "$MEDIA_ID = ? and $MEDIA_SUMMARY = ?",
                arrayOf(mediaId, summary), null, null, null
            )
        var playInfo: PlayInfo? = null
        if (cursor.moveToFirst()) {
            playInfo = toMediaPlayInfo(cursor)
        }
        return playInfo
    }

    private fun toMediaPlayInfo(cursor: Cursor): PlayInfo {
        var playInfo = PlayInfo()
        playInfo._id = cursor.getString(cursor.getColumnIndex(VALUE_ID))
        playInfo.mediaId = cursor.getString(cursor.getColumnIndex(MEDIA_ID))
        playInfo.title = cursor.getString(cursor.getColumnIndex(MEDIA_TITLE))
        playInfo.summary = cursor.getString(cursor.getColumnIndex(MEDIA_SUMMARY))
        playInfo.url = cursor.getString(cursor.getColumnIndex(MEDIA_PLAY_URL))
        playInfo.videoUrl = cursor.getString(cursor.getColumnIndex(MEDIA_VIDEO_URL))
        return playInfo
    }

    private fun toMediaInfo(cursor: Cursor): MediaInfo {
        var mediaInfo = MediaInfo()
        mediaInfo._id = cursor.getString(cursor.getColumnIndex(VALUE_ID))
        mediaInfo.title = cursor.getString(cursor.getColumnIndex(MEDIA_TITLE))
        mediaInfo.image = cursor.getString(cursor.getColumnIndex(MEDIA_IMAGE_URL))
        mediaInfo.url = cursor.getString(cursor.getColumnIndex(MEDIA_PAGE_URL))
        mediaInfo.type = MediaType.valueOf(cursor.getString(cursor.getColumnIndex(MEDIA_TYPE)))
        mediaInfo.describe = cursor.getString(cursor.getColumnIndex(MEDIA_DESCRIBE))
        mediaInfo.isHot = (1 == cursor.getInt(cursor.getColumnIndex(MEDIA_IS_HOT)))
        return mediaInfo
    }

    fun getAllMediaInfo(type: MediaType, isHot: Boolean): LinkedList<MediaInfo> {
        //创建游标对象
        val listData = LinkedList<MediaInfo>()
        //创建游标对象
        var selection = "$MEDIA_TYPE=?"
        var values = arrayOf(type.toString())
        if (isHot) {
            selection = "$MEDIA_TYPE=? and $MEDIA_IS_HOT=1"
        }
        val cursor =
            writableDatabase.query(
                MEDIA_INFO_TABLE,
                null,
                selection,
                values,
                null,
                null,
                null
            )
        while (cursor.moveToNext()) {
            val mediaInfo = toMediaInfo(cursor)
            listData.add(mediaInfo)
        }
        return listData
    }

    fun getAllMediaPlayInfo(mediaInfo: MediaInfo): LinkedList<PlayInfo> {
        //创建游标对象
        val listData = LinkedList<PlayInfo>()
        //创建游标对象
        val cursor =
            writableDatabase.query(
                MEDIA_PLAY_INFO_TABLE, null, "$MEDIA_ID = ?",
                arrayOf(mediaInfo._id), null, null, null
            )
        while (cursor.moveToNext()) {
            val playInfo = toMediaPlayInfo(cursor)
            listData.add(playInfo)
        }
        return listData
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}