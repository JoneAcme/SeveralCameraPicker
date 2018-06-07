package com.jone.sevral

import android.app.Activity
import com.jone.sevral.comments.impl.DefaltToast
import com.jone.sevral.comments.impl.DefaultImageLoader
import com.jone.sevral.comments.impl.DefaultLoading
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
    var mToast: ToastInterFace = DefaltToast()
    var mLoadingDialog: LoadingDialogInterface = DefaultLoading()
    var mImageLoader: ImageLoaderInterface = DefaultImageLoader()

    fun start(activity: Activity, option: PickerOption = PickerOption()) {
        Navigator.startPicker(activity, option)
    }
}