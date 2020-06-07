package com.agile.mycinema

import android.os.Parcel
import android.os.Parcelable

class MediaInfo() : Parcelable {
    var _id: String = ""
    var type = MediaType.UnKnow//影片类型
    var title = ""//影片名称
    var image = ""//图片
    var url = ""//网页详情链接
    var describe = ""//影片介绍
    var isHot = false//是否热门，热门的会展示在首页

    constructor(parcel: Parcel) : this() {
        _id = parcel.readString().toString()
        type = MediaType.valueOf(parcel.readString().toString())
        title = parcel.readString().toString()
        image = parcel.readString().toString()
        url = parcel.readString().toString()
        describe = parcel.readString().toString()
        isHot = parcel.readByte() != 0.toByte()
    }

    constructor(
        _type: MediaType,
        _title: String,
        _image: String,
        _url: String,
        _describe: String,
        _isHot: Boolean
    ) : this() {
        type = _type
        title = _title
        image = _image
        url = _url
        describe = _describe
        isHot = _isHot
    }


    override fun toString(): String {
        return type.name + " " + title + " " + image + " " + url + " " + describe
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(_id)
        parcel.writeString(type.toString())
        parcel.writeString(title)
        parcel.writeString(image)
        parcel.writeString(url)
        parcel.writeString(describe)
        parcel.writeByte(if (isHot) 1 else 0)
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