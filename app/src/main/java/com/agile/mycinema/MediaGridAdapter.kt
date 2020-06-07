package com.agile.mycinema

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import java.util.*

class MediaGridAdapter(context: Activity) : BaseAdapter() {
    var mMedias = LinkedList<MediaInfo>()
    var mContext = context

    init {

    }

    fun initData(medias: LinkedList<MediaInfo>) {
        mMedias.clear()
        mMedias.addAll(medias)
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = MediaItemView(mContext)
        var mediaInfo = mMedias[position]
        view.setData(mediaInfo)
        return view
    }

    override fun getItem(position: Int): Any {
        return mMedias[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return mMedias.size
    }

    companion object {
        class ViewHolder {
            lateinit var titleView: TextView
            lateinit var imageView: ImageView
        }
    }
}