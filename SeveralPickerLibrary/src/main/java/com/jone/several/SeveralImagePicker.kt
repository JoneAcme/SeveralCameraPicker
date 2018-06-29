package com.jone.several

import android.app.Activity
import com.jone.several.comments.impl.DefaultToast
import com.jone.several.comments.impl.DefaultImageLoader
import com.jone.several.comments.inter.*
import com.jone.several.config.SeveralPickerOption
import com.jone.several.utils.Jumper

/**
 * @fileName SeveralImagePicker
 * Created by YiangJone on 2018/6/7.
 * @describe
 */

object SeveralImagePicker {


    internal var pickerOption: SeveralPickerOption = SeveralPickerOption()

    /**
     * Toast
     */
    internal var mToast: ToastInterFace = DefaultToast()
    /**
     * 图片加载
     */
    internal var mImageLoader: ImageLoaderInterface = DefaultImageLoader()
    /**
     * 图片返回回调
     */
    internal var mCompleteListener: PickerCompleteInterface? = null

    /**
     * Toast
     */
    fun setDefaultToast(mToast: ToastInterFace): SeveralImagePicker {
        this.mToast = mToast
        return this
    }

    /**
     * 图片加载
     */
    fun setDefaultImageLoader(mImageLoader: ImageLoaderInterface): SeveralImagePicker {
        this.mImageLoader = mImageLoader
        return this
    }


    /**
     * 图片返回回调
     */
    fun setCompleteListener(mCompleteListener: PickerCompleteInterface): SeveralImagePicker {
        this.mCompleteListener = mCompleteListener
        return this
    }

    fun setOption(pickerOption: SeveralPickerOption): SeveralImagePicker {
        this.pickerOption = pickerOption
        return this
    }

    fun start(activity: Activity) {
        Jumper.startPicker(activity)
    }
    fun start(activity: Activity, maxCount:Int) {
        pickerOption.maxPickNumber = maxCount
        Jumper.startPicker(activity)
    }

}