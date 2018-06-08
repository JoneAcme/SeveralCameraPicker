package com.jone.sevral

import android.app.Activity
import com.jone.sevral.comments.impl.DefaultCompress
import com.jone.sevral.comments.impl.DefaultToast
import com.jone.sevral.comments.impl.DefaultImageLoader
import com.jone.sevral.comments.impl.DefaultLoading
import com.jone.sevral.comments.inter.*
import com.jone.sevral.config.PickerOption
import com.jone.sevral.utils.Navigator

/**
 * @fileName SeveralImagePicker
 * Created by YiangJone on 2018/6/7.
 * @describe
 */

object SeveralImagePicker {
    /**
     * Toast
     */
    internal var mToast: ToastInterFace = DefaultToast()
    /**
     * loading、 dialog
     */
    internal var mLoadingDialog: LoadingDialogInterface = DefaultLoading()
    /**
     * 图片加载
     */
    internal var mImageLoader: ImageLoaderInterface = DefaultImageLoader()
    /**
     * 图片压缩
     */
    internal var mCompress: CompressInterface = DefaultCompress()
    /**
     * 图片返回回调
     */
    internal var mCompleteListener: PickerCompleteInterface? = null


    internal var pickerOption: PickerOption = PickerOption()

    /**
     * Toast
     */
    fun setDefaultToast(mToast: ToastInterFace): SeveralImagePicker {
        this.mToast = mToast
        return this
    }

    /**
     * loading、 dialog
     */
    fun setDefaultLoadingDialog(mLoadingDialog: LoadingDialogInterface): SeveralImagePicker {
        this.mLoadingDialog = mLoadingDialog
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
     * 图片压缩
     */
    fun setDefaultCompress(mCompress: CompressInterface): SeveralImagePicker {
        this.mCompress = mCompress
        return this
    }

    /**
     * 图片返回回调
     */
    fun setCompleteListener(mCompleteListener: PickerCompleteInterface): SeveralImagePicker {
        this.mCompleteListener = mCompleteListener
        return this
    }

    fun setOptions( pickerOption: PickerOption) : SeveralImagePicker{
        this.pickerOption = pickerOption
        return  this
    }


    fun start(activity: Activity) {
        this.mCompleteListener = mCompleteListener
        Navigator.startPicker(activity, pickerOption)
    }

}