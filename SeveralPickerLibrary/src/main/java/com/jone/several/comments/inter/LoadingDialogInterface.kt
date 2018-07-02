package com.jone.several.comments.inter

import android.app.Dialog
import android.content.Context

/**
 * @fileName LoadingDialogInterface
 * Created by YiangJone on 2018/7/2.
 * @describe
 */

interface LoadingDialogInterface{
    fun createDialog(mContext: Context): Dialog
}