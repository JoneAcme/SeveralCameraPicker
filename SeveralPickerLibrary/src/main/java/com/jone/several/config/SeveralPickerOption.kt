package com.jone.several.config

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Environment
import android.os.Parcel
import android.os.Parcelable

@SuppressLint("ParcelCreator")
/**
 * @fileName SeveralPickerOption
 * Created by YiangJone on 2018/6/5.
 * @describe
 */

class SeveralPickerOption {

    //是否显示拍照按钮
    var enableCamera = true

    //最大选择张数，默认为9
    var maxPickNumber = 3
    //最小选择张数，默认为0，表示不限制
    var minPickNumber = 0
    //图片选择界面每行图片个数
    var spanCount = 4


    //选择列表图片宽度
    var thumbnailWidth = 100
    //选择列表图片高度
    var thumbnailHeight = 100

    //选择列表点击动画效果
    var enableAnimation = true
    //是否显示gif图片
    var enableGif: Boolean = false
    //是否开启点击预览
    var enablePreview = true

    //是否开启点击声音
    var enableClickSound = true
    //预览图片时，是否增强左右滑动图片体验
    var previewEggs = true

    //是否开启数字显示模式
    var enableNumPick: Boolean = false

}