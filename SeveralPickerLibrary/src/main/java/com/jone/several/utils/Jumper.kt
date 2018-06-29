package com.jone.several.utils

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.jone.several.config.PickerConstant
import com.jone.several.model.MediaEntity
import com.jone.several.ui.SeveralPickerActivity
import com.jone.several.ui.SeveralPreviewActivity
import com.jone.several.ui.SeveralCameraActivity
import java.io.Serializable

/**
 * @fileName Jumper
 * Created by YiangJone on 2018/6/6.
 * @describe
 */


class Jumper {
    companion object {

        fun startPicker(activity: Activity) {
            activity.startActivity(Intent(activity, SeveralPickerActivity::class.java))
            activity.OverrideTransition()
        }

        /**
         * preview media files, for now, only support images or videos.
         */
        fun showPreviewView(activity: Activity,
                            previewMediaList: MutableList<MediaEntity>,
                            pickedMediaList: MutableList<MediaEntity>,
                            currentPosition: Int,
                            previewFromType: Int = PickerConstant.TYPE_PREIVEW_FROM_PICK
        ) {
            if (DoubleClickUtils.isFastDoubleClick) return


            val bundle = Bundle()
            bundle.putSerializable(PickerConstant.KEY_PICK_LIST, pickedMediaList as Serializable)
            bundle.putSerializable(PickerConstant.KEY_ALL_LIST, previewMediaList as Serializable)
            bundle.putInt(PickerConstant.KEY_POSITION, currentPosition)
            bundle.putInt(PickerConstant.KEY_PREVIEW_TYPE, previewFromType)

            activity.startActivity(Intent(activity, SeveralPreviewActivity::class.java).putExtras(bundle))
            activity.OverrideTransition()
        }

        fun showCameraView(activity: Activity) {
            val bundle = Bundle()
            activity.startActivity(Intent(activity, SeveralCameraActivity::class.java).putExtras(bundle))
            activity.OverrideTransition()
        }
    }
}