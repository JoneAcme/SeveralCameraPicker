package com.jone.several.config

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Environment
import android.os.Parcel
import android.os.Parcelable

@SuppressLint("ParcelCreator")
/**
 * @fileName PickerOption
 * Created by YiangJone on 2018/6/5.
 * @describe
 */

class PickerOption() : Parcelable {

    object THEME {
        //主题颜色 - 默认
        val THEME_DEFAULT = Color.parseColor("#333333")
        //主题 - 中国红主题
        val THEME_RED = Color.parseColor("#FF4040")
        //主题 - 青春橙主题
        val THEME_ORANGE = Color.parseColor("#FF571A")
        //主题 - 天空蓝主题
        val THEME_BLUE = Color.parseColor("#538EEB")
    }
    object QUALITY{
        val MEDIA_QUALITY_LOWEST = 15
        val MEDIA_QUALITY_LOW = 20
        val MEDIA_QUALITY_MEDIUM = 50
        val MEDIA_QUALITY_HIGH = 80
        val MEDIA_QUALITY_HIGHEST = 100
    }

    //主题样式，
    private var theme = THEME.THEME_DEFAULT

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

    //是否开启压缩
    var enableCompress: Boolean = true
    //图片压缩阈值（多少kb以下的图片不进行压缩，默认1024kb）
    var compressPictureFilterSize = 1024


    //拍照保存地址
    var savePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
    //压缩保存地址
    var compresssPath = ""
    //压缩质量
    var compressQuality = QUALITY.MEDIA_QUALITY_MEDIUM

    //是否开启数字显示模式
    var enableNumPick:Boolean = false


    constructor(parcel: Parcel) : this() {
        theme = parcel.readInt()
        enableCamera = parcel.readByte() != 0.toByte()
        maxPickNumber = parcel.readInt()
        minPickNumber = parcel.readInt()
        spanCount = parcel.readInt()
        thumbnailWidth = parcel.readInt()
        thumbnailHeight = parcel.readInt()
        enableAnimation = parcel.readByte() != 0.toByte()
        enableGif = parcel.readByte() != 0.toByte()
        enablePreview = parcel.readByte() != 0.toByte()
        enableClickSound = parcel.readByte() != 0.toByte()
        previewEggs = parcel.readByte() != 0.toByte()
        enableCompress = parcel.readByte() != 0.toByte()
        compressPictureFilterSize = parcel.readInt()
        savePath = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(theme)
        parcel.writeByte(if (enableCamera) 1 else 0)
        parcel.writeInt(maxPickNumber)
        parcel.writeInt(minPickNumber)
        parcel.writeInt(spanCount)
        parcel.writeInt(thumbnailWidth)
        parcel.writeInt(thumbnailHeight)
        parcel.writeByte(if (enableAnimation) 1 else 0)
        parcel.writeByte(if (enableGif) 1 else 0)
        parcel.writeByte(if (enablePreview) 1 else 0)
        parcel.writeByte(if (enableClickSound) 1 else 0)
        parcel.writeByte(if (previewEggs) 1 else 0)
        parcel.writeByte(if (enableCompress) 1 else 0)
        parcel.writeInt(compressPictureFilterSize)
        parcel.writeString(savePath)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PickerOption> {
        override fun createFromParcel(parcel: Parcel): PickerOption {
            return PickerOption(parcel)
        }

        override fun newArray(size: Int): Array<PickerOption?> {
            return arrayOfNulls(size)
        }
    }


}