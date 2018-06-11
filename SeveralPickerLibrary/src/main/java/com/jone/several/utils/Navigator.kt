package com.jone.several.utils

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.jone.several.config.PickerConstant
import com.jone.several.config.PickerOption
import com.jone.several.model.MediaEntity
import com.jone.several.ui.CameraActivityPicker
import com.jone.several.ui.PickerActivityPicker
import com.jone.several.ui.PreviewActivityPicker
import java.io.Serializable

/**
 * @fileName Navigator
 * Created by YiangJone on 2018/6/6.
 * @describe
 */


class Navigator {
    companion object {

        fun startPicker(activity: Activity, option: PickerOption) {
            val bundle = Bundle()
            bundle.putParcelable(PickerConstant.PICKER_OPTION, option)
            activity.startActivity(Intent(activity, PickerActivityPicker::class.java).putExtras(bundle))
            activity.OverrideTransition()
        }

        /**
         * preview media files, for now, only support images or videos.
         */
        fun showPreviewView(activity: Activity, option: PickerOption,
                            previewMediaList: MutableList<MediaEntity>,
                            pickedMediaList: MutableList<MediaEntity>,
                            currentPosition: Int,
                            previewFromType:Int = PickerConstant.TYPE_PREIVEW_FROM_PICK
                            ) {
            if (DoubleUtils.isFastDoubleClick) return


            val bundle = Bundle()
            bundle.putParcelable(PickerConstant.PICKER_OPTION, option)
            bundle.putSerializable(PickerConstant.KEY_PICK_LIST, pickedMediaList as Serializable)
            bundle.putSerializable(PickerConstant.KEY_ALL_LIST, previewMediaList as Serializable)
            bundle.putInt(PickerConstant.KEY_POSITION, currentPosition)
            bundle.putInt(PickerConstant.KEY_PREVIEW_TYPE, previewFromType)

            activity.startActivity(Intent(activity, PreviewActivityPicker::class.java).putExtras(bundle))
            activity.OverrideTransition()
        }

        fun showCameraView(activity: Activity, option: PickerOption) {
            val bundle = Bundle()
            bundle.putParcelable(PickerConstant.PICKER_OPTION, option)
            activity.startActivity(Intent(activity, CameraActivityPicker::class.java).putExtras(bundle))
            activity.OverrideTransition()
        }
    }
}