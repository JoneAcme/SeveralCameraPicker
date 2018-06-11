package com.jone.several.ui

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.Window
import android.view.WindowManager
import com.jone.several.SeveralImagePicker
import com.jone.several.model.MediaEntity
import com.jone.several.utils.compressMedias
import com.jone.several.utils.createLoadingDialog
import com.jone.several.utils.onPickerComlete

/**
 * @fileName PickerBaseActivity
 * Created by YiangJone on 2018/6/8.
 * @describe
 */

open class PickerBaseActivity : FragmentActivity() {
    protected var pickerOption = SeveralImagePicker.pickerOption
    protected val loadingDialog: Dialog by lazy { createLoadingDialog() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }


    protected fun processMedia(mediaList: MutableList<MediaEntity>) {
        compressMedias(pickerOption, mediaList, loadingDialog, { mList ->
            onPickerComlete(mList)
            finish()
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        if(loadingDialog.isShowing) loadingDialog.hide()
    }

    protected fun showLoading() {
        if(!loadingDialog.isShowing) loadingDialog.show()
    }
    protected fun dissmissLoading() {
        if(loadingDialog.isShowing) loadingDialog.hide()
    }
}