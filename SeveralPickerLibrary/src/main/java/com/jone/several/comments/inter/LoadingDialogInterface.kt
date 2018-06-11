package com.jone.several.comments.inter

import android.app.Dialog
import android.content.Context

/**
 * @fileName LoadingDialogInterface
 * Created by YiangJone on 2018/6/7.
 * @describe
 */

interface LoadingDialogInterface{
    fun createDialog(mContext: Context):Dialog
}