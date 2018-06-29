package com.jone.several.utils

import android.app.Activity
import android.content.Context
import android.support.annotation.IdRes
import android.widget.ImageView
import com.jone.several.R
import com.jone.several.SeveralImagePicker

/**
 * @fileName Extensions
 * Created by YiangJone on 2018/6/7.
 * @describe
 */

fun Activity.OverrideTransition(@IdRes startAnim: Int = R.anim.picker_anim_activity_in, @IdRes endAnim: Int = R.anim.picker_anim_activity_out) {
    overridePendingTransition(startAnim, endAnim)
}

fun Context.showToast(msg: String) {
    SeveralImagePicker.mToast.show(this, msg)
}

fun Context.loadImage(path: String, ivTaget: ImageView) {
    SeveralImagePicker.mImageLoader.loadImage(this, path, ivTaget)
}

fun Context.loadCameraPreviewImage(path: String, ivTaget: ImageView) {
    SeveralImagePicker.mImageLoader.loadCameraPreviewImage(this, path, ivTaget)
}



fun onPickerComlete(mList: ArrayList<String>) {
    SeveralImagePicker.mCompleteListener?.let { it.onComlete(mList) }
}
