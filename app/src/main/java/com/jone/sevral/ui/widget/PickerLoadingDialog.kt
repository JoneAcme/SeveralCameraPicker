package com.jone.sevral.ui.widget

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.jone.sevral.R

/**
 * @fileName PickerLoadingDialog
 * Created by YiangJone on 2018/6/6.
 * @describe
 */


class PickerLoadingDialog(context: Context) : Dialog(context, R.style.style_dialog) {

    init {
        setCancelable(true)
        setCanceledOnTouchOutside(false)
//        window.setWindowAnimations(R.style.style_window)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_loading)
    }
}