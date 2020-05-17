package com.agile.mycinema

import android.os.Parcel
import android.os.Parcelable

class MediaInfo() : Parcelable {
    lateinit var type: Type
    lateinit var title: String
    lateinit var image: String
    lateinit var url: String

    constructor(_type: Type, _title: String, _image: String, _url: String) : this() {
        type = _type
        title = _title
        image = _image
        url = _url
    }

    constructor(parcel: Parcel) : this() {
        title = parcel.readString().toString()
        image = parcel.readString().toString()
        url = parcel.readString().toString()
    }

    override fun toString(): String {
        return type.name + " " + title + " " + image + " " + url
    }

    public enum class Type(value: Int) {
        MOVIE(0), TV(1), CARTOON(2), TVSHOW(3)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(image)
        parcel.writeString(url)
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