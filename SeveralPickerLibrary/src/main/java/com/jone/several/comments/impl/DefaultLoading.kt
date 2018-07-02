package com.jone.several.comments.impl

import android.app.Dialog
import android.content.Context
import com.jone.several.comments.inter.LoadingDialogInterface
import com.jone.several.ui.widget.PickerLoadingDialog

/**
 * @fileName DefaultDialog
 * Created by YiangJone on 2018/7/2.
 * @describe
 */


class DefaultLoading : LoadingDialogInterface {

    override fun createDialog(mContext: Context): Dialog = PickerLoadingDialog(mContext)

}