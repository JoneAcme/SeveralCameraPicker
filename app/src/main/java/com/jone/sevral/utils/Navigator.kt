package com.jone.sevral.utils

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.jone.sevral.R
import com.jone.sevral.config.PickerConstant
import com.jone.sevral.config.PickerOption
import com.jone.sevral.model.MediaEntity
import com.jone.sevral.ui.CameraActivity
import com.jone.sevral.ui.PickerActivity
import com.jone.sevral.ui.PreviewActivity
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
            activity.startActivity(Intent(activity, PickerActivity::class.java).putExtras(bundle))
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

            activity.startActivity(Intent(activity, PreviewActivity::class.java).putExtras(bundle))
            activity.OverrideTransition()
        }

        fun showCameraView(activity: Activity, option: PickerOption) {
            val bundle = Bundle()
            bundle.putParcelable(PickerConstant.PICKER_OPTION, option)
            activity.startActivity(Intent(activity, CameraActivity::class.java).putExtras(bundle))
            activity.OverrideTransition()
        }
    }
}