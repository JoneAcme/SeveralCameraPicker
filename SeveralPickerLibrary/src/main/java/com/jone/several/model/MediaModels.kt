package com.jone.several.model

import java.io.Serializable

/**
 * @fileName MediaModels
 * Created by YiangJone on 2018/6/5.
 * @describe
 */

data class MediaFolder(var name: String,
                       var path: String,
                       var firstImagePath: String,
                       var imageNumber: Int,
                       var checkedNumber: Int,
                       var isChecked: Boolean,
                       var images: MutableList<MediaEntity>) : Serializable
