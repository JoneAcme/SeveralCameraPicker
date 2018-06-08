package com.jone.sevral.ui.adapter

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jone.several.selector.logic.widget.photoview.PhotoView
import com.jone.sevral.R
import com.jone.sevral.model.MediaEntity
import com.jone.sevral.utils.loadImage

/**
 * @fileName SimpleFragmentAdapter
 * Created by YiangJone on 2018/6/6.
 * @describe
 */

class SimpleFragmentAdapter(var mContext: Context, var allMediaList: MutableList<MediaEntity> = ArrayList()) : PagerAdapter() {

    fun setList(allMediaList: MutableList<MediaEntity>) {
        this.allMediaList = allMediaList
        notifyDataSetChanged()
    }


    override fun getCount(): Int {
        return allMediaList.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val contentView = LayoutInflater.from(container.context).inflate(R.layout.picker_item_vp_preview, container, false)

        val preview_image = contentView.findViewById<PhotoView>(R.id.preview_image)

        val mediaEntity = allMediaList[position]

        val path = if (TextUtils.isEmpty(mediaEntity.finalPath))
            mediaEntity.localPath
        else
            mediaEntity.finalPath

        preview_image.visibility = View.VISIBLE
        mContext.loadImage(path,preview_image)
        container.addView(contentView, 0)
        return contentView
    }
}