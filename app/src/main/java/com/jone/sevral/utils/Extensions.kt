package com.jone.sevral.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.support.annotation.IdRes
import android.widget.ImageView
import com.jone.sevral.R
import com.jone.sevral.SeveralImagePicker
import com.jone.sevral.config.PickerOption
import com.jone.sevral.model.MediaEntity
import com.jone.sevral.processor.PictureCompressProcessor
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
 * 压缩后 isCompressed = true
 * 路径存放 setCompressPath
 * 如果不压缩，即返回原路径
 */
fun Context.compressMedias(pickerOption: PickerOption, mediaList: MutableList<MediaEntity>, loading: Dialog, onResult: (MutableList<MediaEntity>) -> Unit) {
    val result: MutableList<MediaEntity> = ArrayList()

    if (!pickerOption.enableCompress) {
        onResult(mediaList)
        return
    }
    Observable.create(ObservableOnSubscribe<MediaEntity> { e ->
        mediaList.forEach {
            e.onNext(PictureCompressProcessor().syncProcess(this, it, pickerOption))
        }
        e.onComplete()
    }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<MediaEntity> {
                override fun onSubscribe(d: Disposable) {
                    if (!loading.isShowing) loading.show()
                }

                override fun onNext(mediaEntity: MediaEntity) {
                    result.add(mediaEntity)
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