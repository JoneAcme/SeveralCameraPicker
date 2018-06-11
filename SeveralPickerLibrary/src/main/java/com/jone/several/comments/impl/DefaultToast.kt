package com.jone.several.comments.impl

import android.content.Context
import android.widget.Toast
import com.jone.several.comments.inter.ToastInterFace

/**
 * @fileName DefaultToast
 * Created by YiangJone on 2018/6/7.
 * @describe
 */

class DefaultToast : ToastInterFace {
    override fun show(mContext: Context, msg: String) {
        Toast.makeText(mContext,msg,Toast.LENGTH_SHORT).show()
    }
}