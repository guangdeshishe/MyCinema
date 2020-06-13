package com.agile.mycinema

import android.os.Parcel
import android.os.Parcelable

class MediaInfo() : Parcelable {
    var _id: String = ""
    var type = MediaType.UnKnow//影片类型
    var subType = ""//喜剧、动作等
    var score = ""//评分
    var label = ""//标签：更新至多少集
    var area = ""//上映地区
    var year = ""//上映年份
    var title = ""//影片名称
    var image = ""//图片
    var url = ""//网页详情链接
    var describe = ""//影片介绍
    var isHot = false//是否热门，热门的会展示在首页
    var isTop = false//排行榜数据

    fun image(_image: String): MediaInfo {
        this.image = _image
        return this
    }

    fun url(_url: String): MediaInfo {
        this.url = _url
        return this
    }

    fun describe(_describe: String): MediaInfo {
        this.describe = _describe
        return this
    }

    fun type(_type: MediaType): MediaInfo {
        this.type = _type
        return this
    }

    fun subType(_subType: String): MediaInfo {
        this.subType = _subType
        return this
    }

    fun area(_area: String): MediaInfo {
        this.area = _area
        return this
    }

    fun year(_year: String): MediaInfo {
        this.year = _year
        return this
    }

    fun isHot(_isHot: Boolean): MediaInfo {
        this.isHot = _isHot
        return this
    }

    fun isTop(_isTop: Boolean): MediaInfo {
        this.isTop = _isTop
        return this
    }

    fun title(_title: String): MediaInfo {
        this.title = _title
        return this
    }

    override fun toString(): String {
        return type.name + " " + title + " " + image + " " + url + " " + describe
    }

    constructor(parcel: Parcel) : this() {
        _id = parcel.readString().toString()
        type = MediaType.valueOf(parcel.readString().toString())
        subType = parcel.readString().toString()
        score = parcel.readString().toString()
        label = parcel.readString().toString()
        area = parcel.readString().toString()
        year = parcel.readString().toString()
        title = parcel.readString().toString()
        image = parcel.readString().toString()
        url = parcel.readString().toString()
        describe = parcel.readString().toString()
        isHot = parcel.readByte() != 0.toByte()
        isTop = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(_id)
        parcel.writeString(type.toString())
        parcel.writeString(subType.toString())
        parcel.writeString(score)
        parcel.writeString(label)
        parcel.writeString(area)
        parcel.writeString(year)
        parcel.writeString(title)
        parcel.writeString(image)
        parcel.writeString(url)
        parcel.writeString(describe)
        parcel.writeByte(if (isHot) 1 else 0)
        parcel.writeByte(if (isTop) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MediaInfo> {
        override fun createFromParcel(parcel: Parcel): MediaInfo {
            return MediaInfo(parcel)
        }

        override fun newArray(size: Int): Array<MediaInfo?> {
            return arrayOfNulls(size)
        }
    }


}