package com.jone.sevral

import android.app.Activity
import com.jone.sevral.comments.impl.DefaultCompress
import com.jone.sevral.comments.impl.DefaultToast
import com.jone.sevral.comments.impl.DefaultImageLoader
import com.jone.sevral.comments.impl.DefaultLoading
import com.jone.sevral.comments.inter.CompressInterface
import com.jone.sevral.comments.inter.ImageLoaderInterface
import com.jone.sevral.comments.inter.LoadingDialogInterface
import com.jone.sevral.comments.inter.ToastInterFace
import com.jone.sevral.config.PickerOption
import com.jone.sevral.utils.Navigator

/**
 * @fileName SeveralImagePicker
 * Created by YiangJone on 2018/6/7.
 * @describe
 */

object SeveralImagePicker {
    var mToast: ToastInterFace = DefaultToast()
    var mLoadingDialog: LoadingDialogInterface = DefaultLoading()
    var mImageLoader: ImageLoaderInterface = DefaultImageLoader()
    var mCompress:CompressInterface = DefaultCompress()


    fun start(activity: Activity, option: PickerOption = PickerOption()) {
        Navigator.startPicker(activity, option)
    }

}