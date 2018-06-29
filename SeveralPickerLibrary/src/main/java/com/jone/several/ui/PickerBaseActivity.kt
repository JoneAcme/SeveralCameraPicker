package com.jone.several.ui

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.Window
import android.view.WindowManager
import com.jone.several.SeveralImagePicker
import com.jone.several.config.PickerConstant
import com.jone.several.model.EventEntity
import com.jone.several.model.MediaEntity
import com.jone.several.model.rxbus.RxBus
import com.jone.several.utils.onPickerComlete

/**
 * @fileName PickerBaseActivity
 * Created by YiangJone on 2018/6/8.
 * @describe
 */

open class PickerBaseActivity : FragmentActivity() {

    protected var option = SeveralImagePicker.pickerOption
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }


    protected fun completePicker(mediaList: MutableList<MediaEntity>) {
        val list = mediaList.map { it.localPath } as ArrayList<String>
        onPickerComlete(list)

        val obj = EventEntity(PickerConstant.FLAG_PREVIEW_COMPLETE, mediaList, 0)
        RxBus.default.post(obj)
        finish()

    }

    override fun onDestroy() {
        super.onDestroy()
    }

}