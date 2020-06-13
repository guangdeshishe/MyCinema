package com.agile.mycinema.moremedia

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.agile.mycinema.CommonTextView
import com.agile.mycinema.MediaType
import com.agile.mycinema.R
import java.util.*

class MediaTypeAdapter(context: Activity) : BaseAdapter() {
    var mMedias = LinkedList<SubMediaType>()
    var mContext = context


    init {

    }

    fun initData(mediaType: MediaType) {
        mMedias.clear()
        when (mediaType) {
//            MediaType.MOVIE -> mMedias.addAll(MovieTypes)
        }

        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var contentView =
            LayoutInflater.from(mContext).inflate(R.layout.media_type_item_view, null, false)
        var titleView = contentView.findViewById<CommonTextView>(R.id.mMediaTypeView)
        titleView.text = mMedias[position].mediaType
        return titleView
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