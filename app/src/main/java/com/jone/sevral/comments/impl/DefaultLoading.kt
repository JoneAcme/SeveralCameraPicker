package com.jone.sevral.comments.impl

import android.app.Dialog
import android.content.Context
import com.jone.sevral.comments.inter.LoadingDialogInterface
import com.jone.sevral.ui.widget.PickerLoadingDialog

/**
 * @fileName DefaultLoading
 * Created by YiangJone on 2018/6/7.
 * @describe
 */

class DefaultLoading : LoadingDialogInterface {

    override fun createDialog(mContext: Context): Dialog = PickerLoadingDialog(mContext)

}