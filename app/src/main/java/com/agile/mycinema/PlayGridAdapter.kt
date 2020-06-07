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
    var selectedPosition = 0

    fun initData(medias: LinkedList<PlayInfo>) {
        mPlayInfos.clear()
        mPlayInfos.addAll(medias)
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = PlayItemView(mContext)
        var playInfo = mPlayInfos[position]
        view.setData(playInfo, selectedPosition == position)
        return view
    }

    override fun getItem(position: Int): Any {
        if (position >= 0 && position < mPlayInfos.size) {
            return mPlayInfos[position]
        }
        return PlayInfo();
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