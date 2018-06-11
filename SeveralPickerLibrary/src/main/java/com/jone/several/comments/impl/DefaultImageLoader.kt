package com.jone.several.comments.impl

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.jone.several.comments.inter.ImageLoaderInterface

/**
 * @fileName DefaultImageLoader
 * Created by YiangJone on 2018/6/7.
 * @describe
 */

class DefaultImageLoader: ImageLoaderInterface {
    override fun loadImage(mContext: Context, path: String, ivTaget: ImageView) {
        Glide.with(mContext).load(path).centerCrop().into(ivTaget)
    }

    override fun loadCameraPreviewImage(mContext: Context, path: String, ivTaget: ImageView) {
        Glide.with(mContext).load(path).centerCrop().into(ivTaget)
    }

}