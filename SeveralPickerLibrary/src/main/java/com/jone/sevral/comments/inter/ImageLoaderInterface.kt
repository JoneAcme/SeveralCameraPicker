package com.jone.sevral.comments.inter

import android.content.Context
import android.widget.ImageView

/**
 * @fileName ImageLoaderInterface
 * Created by YiangJone on 2018/6/7.
 * @describe
 */

interface ImageLoaderInterface {

    fun loadImage(mContext: Context, path: String, ivTaget: ImageView)

    fun loadCameraPreviewImage(mContext: Context,path: String, ivTaget: ImageView)
}