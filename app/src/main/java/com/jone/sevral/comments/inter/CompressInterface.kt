package com.jone.sevral.comments.inter

import android.content.Context
import com.jone.sevral.config.PickerOption

/**
 * @fileName CompressInterface
 * Created by YiangJone on 2018/6/8.
 * @describe
 */

interface CompressInterface {
    fun compress(mContext: Context, imgPath: String,pickerOption: PickerOption):String
}