package com.agile.mycinema

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import java.util.*

class PlayGridAdapter(context: Activity) : BaseAdapter() {
    var mPlayInfos = LinkedList<PlayInfo>()
    var mContext = context

    init {

    }

    fun initData(medias: LinkedList<PlayInfo>) {
        mPlayInfos.clear()
        mPlayInfos.addAll(medias)
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = PlayItemView(mContext)
        var playInfo = mPlayInfos[position]
        view.setData(playInfo)
        return view
    }

    override fun getItem(position: Int): Any {
        return mPlayInfos[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return mPlayInfos.size
    }

    companion object {
        class ViewHolder {
            lateinit var titleView: TextView
            lateinit var imageView: ImageView
        }
    }
}