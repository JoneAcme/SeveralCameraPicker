package com.jone.several.comments.inter

import android.content.Context
import com.jone.several.config.SeveralPickerOption

/**
 * @fileName CompressInterface
 * Created by YiangJone on 2018/6/8.
 * @describe
 */

interface CompressInterface {
    fun compress(mContext: Context, imgPath: String,pickerOption: SeveralPickerOption):String
}