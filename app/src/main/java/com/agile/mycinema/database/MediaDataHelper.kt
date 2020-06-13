package com.agile.mycinema.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.agile.mycinema.MediaInfo
import com.agile.mycinema.MediaType
import com.agile.mycinema.PlayInfo
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
        const val MEDIA_TYPE = "type";//电影、电视剧
        const val MEDIA_SCORE = "score"//评分
        const val MEDIA_LABEL = "label"//标签：更新至多少集
        const val MEDIA_SUB_TYPE = "subType";//喜剧、动作
        const val MEDIA_AREA = "area"//上映地区
        const val MEDIA_YEAR = "year"//上映时间
        const val MEDIA_DESCRIBE = "describe";//影视介绍
        const val MEDIA_IS_HOT = "isHot";//是否热门
        const val MEDIA_IS_Top = "isTop";//是否排行榜中数据
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
                    "$MEDIA_SUB_TYPE text default ''," +
                    "$MEDIA_SCORE text default ''," +
                    "$MEDIA_LABEL text default ''," +
                    "$MEDIA_AREA text default ''," +
                    "$MEDIA_YEAR text default ''," +
                    "$MEDIA_DESCRIBE text default ''," +
                    "$MEDIA_IS_HOT integer default 0," +
                    "$MEDIA_IS_Top integer default 0," +
                    "$MEDIA_UPDATE_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL)"
        val createMediaPlayUrlTable =
            "create table $MEDIA_PLAY_INFO_TABLE(" +
                    "_id integer primary key autoincrement," +
                    "$MEDIA_ID integer," +
                    "$MEDIA_TITLE text default ''," +
                    "$MEDIA_SUMMARY text default '', " +
                    "$MEDIA_PLAY_URL text default ''," +
                    "$MEDIA_VIDEO_URL text default ''," +
                    "$MEDIA_UPDATE_TIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL)"
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
        cv.put(MEDIA_SUB_TYPE, mediaInfo.subType.toString())
        if (mediaInfo.describe.isNotEmpty()) {
            cv.put(MEDIA_DESCRIBE, mediaInfo.describe)
        }
        if (mediaInfo.year.isNotEmpty()) {
            cv.put(MEDIA_YEAR, mediaInfo.describe)
        }
        if (mediaInfo.area.isNotEmpty()) {
            cv.put(MEDIA_AREA, mediaInfo.describe)
        }
        if (mediaInfo.score.isNotEmpty()) {
            cv.put(MEDIA_SCORE, mediaInfo.score)
        }
        if (mediaInfo.label.isNotEmpty()) {
            cv.put(MEDIA_LABEL, mediaInfo.label)
        }
        cv.put(MEDIA_IS_HOT, getBooleanValue(mediaInfo.isHot))
        cv.put(MEDIA_IS_Top, getBooleanValue(mediaInfo.isTop))
        val oldMediaInfo = getMediaInfo(mediaInfo.title)

        if (oldMediaInfo == null) {//新增
            db.insert(MEDIA_INFO_TABLE, null, cv)
        } else {//更新
            cv.put(MEDIA_IS_HOT, getBooleanValue(oldMediaInfo.isHot))
            cv.put(MEDIA_IS_Top, getBooleanValue(oldMediaInfo.isTop))
            db.update(MEDIA_INFO_TABLE, cv, "$VALUE_ID=?", arrayOf(mediaInfo._id))
        }
        db.close()
    }

    fun getBooleanValue(isTrue: Boolean): Int {
        return if (isTrue) {
            1
        } else {
            0
        }
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
        mediaInfo.area = cursor.getString(cursor.getColumnIndex(MEDIA_AREA))
        mediaInfo.year = cursor.getString(cursor.getColumnIndex(MEDIA_YEAR))
        mediaInfo.image = cursor.getString(cursor.getColumnIndex(MEDIA_IMAGE_URL))
        mediaInfo.url = cursor.getString(cursor.getColumnIndex(MEDIA_PAGE_URL))
        mediaInfo.type = MediaType.valueOf(cursor.getString(cursor.getColumnIndex(MEDIA_TYPE)))
        mediaInfo.subType = cursor.getString(cursor.getColumnIndex(MEDIA_SUB_TYPE))
        mediaInfo.score = cursor.getString(cursor.getColumnIndex(MEDIA_SCORE))
        mediaInfo.label = cursor.getString(cursor.getColumnIndex(MEDIA_LABEL))
        mediaInfo.describe = cursor.getString(cursor.getColumnIndex(MEDIA_DESCRIBE))
        mediaInfo.isHot = (1 == cursor.getInt(cursor.getColumnIndex(MEDIA_IS_HOT)))
        mediaInfo.isTop = (1 == cursor.getInt(cursor.getColumnIndex(MEDIA_IS_Top)))
        return mediaInfo
    }

    fun getAllMediaInfo(mediaInfo: MediaInfo): LinkedList<MediaInfo> {
        //创建游标对象
        val listData = LinkedList<MediaInfo>()
        var sql = StringBuilder("select * from $MEDIA_INFO_TABLE where ")
        if (mediaInfo.type != MediaType.UnKnow) {
            sql.append("$MEDIA_TYPE='${mediaInfo.type}' ")
        }

        if (mediaInfo.subType.isNotEmpty()) {
            sql.append("and $MEDIA_SUB_TYPE='${mediaInfo.subType}' ")
        }

        if (mediaInfo.area.isNotEmpty()) {
            sql.append("and $MEDIA_AREA='${mediaInfo.area}' ")
        }

        if (mediaInfo.year.isNotEmpty()) {
            sql.append("and $MEDIA_YEAR='${mediaInfo.year}' ")
        }

        if (mediaInfo.isHot) {
            sql.append("and $MEDIA_IS_HOT=1 ")
        }
        if (mediaInfo.isTop) {
            sql.append("and $MEDIA_IS_Top=1 ")
        }

        sql.append("ORDER BY $MEDIA_UPDATE_TIME DESC,_id desc")
        //创建游标对象

        val cursor =
            writableDatabase.rawQuery(sql.toString(), null)
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