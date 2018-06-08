package com.jone.sevral.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.support.annotation.IdRes
import android.util.Log
import android.widget.ImageView
import com.jone.sevral.R
import com.jone.sevral.SeveralImagePicker
import com.jone.sevral.config.PickerOption
import com.jone.sevral.model.MediaEntity
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

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

fun Context.createLoadingDialog() = SeveralImagePicker.mLoadingDialog.createDialog(this)


/**
 * 压缩图片，
 * 如果不压缩，即返回原路径
 */
fun Context.compressMedias(pickerOption: PickerOption, mediaList: MutableList<MediaEntity>, loading: Dialog, onResult: (MutableList<String>) -> Unit) {
    val result: MutableList<String> = ArrayList()

    if (!pickerOption.enableCompress) {
        mediaList.forEach {
            result.add(it.localPath)
        }
        onResult(result)
        return
    }
    Observable.create(ObservableOnSubscribe<String> { e ->
        mediaList.forEach {
            if (it.isCompressed || it.size > pickerOption.compressPictureFilterSize * 1000)
                e.onNext(SeveralImagePicker.mCompress.compress(this, it.localPath, pickerOption))
            else
                e.onNext(it.localPath)
        }
        e.onComplete()
    }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<String> {
                override fun onSubscribe(d: Disposable) {
                    if (!loading.isShowing) loading.show()
                }

                override fun onNext(path: String) {
                    Log.e("onNext", path)
                    result.add(path)
                }

                override fun onError(e: Throwable) {
                    if (loading.isShowing) loading.dismiss()
                }

                override fun onComplete() {
                    if (loading.isShowing) loading.dismiss()
                    onResult(result)
                }
            })
}