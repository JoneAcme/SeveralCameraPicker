package com.jone.sevral.comments.impl

import android.content.Context
import android.widget.Toast
import com.jone.sevral.comments.inter.ToastInterFace

/**
 * @fileName DefaltToast
 * Created by YiangJone on 2018/6/7.
 * @describe
 */

class DefaltToast: ToastInterFace {
    override fun show(mContext: Context, msg: String) {
        Toast.makeText(mContext,msg,Toast.LENGTH_SHORT).show()
    }
}