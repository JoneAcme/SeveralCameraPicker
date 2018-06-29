package com.jone.demo

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import kotlin.properties.Delegates

/**
 * @fileName UpLoadImgDialog
 * Created by YiangJone on 5018/6/12.
 * @describe
 */

class UpLoadImgDialog(private var mContext: Context) : Dialog(mContext, R.style.loading_dialog) {

    private var rootContainer: LinearLayout by Delegates.notNull()
    private var imgContainer: LinearLayout by Delegates.notNull()
    private var bottomContainer: LinearLayout by Delegates.notNull()
    private var ivLoading: ImageView by Delegates.notNull()
    private var tvLoading: TextView by Delegates.notNull()
    private var tvCancel: TextView by Delegates.notNull()

    init {
//        width = LinearLayout.LayoutParams.WRAP_CONTENT
//        height = LinearLayout.LayoutParams.WRAP_CONTENT

//        setBackgroundDrawable(ColorDrawable())
        initContainer(mContext)
//        isOutsideTouchable = false

    }

    private fun initContainer(mContext: Context) {
        val paramsWW = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val paramsMM = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        rootContainer = LinearLayout(mContext)
        imgContainer = LinearLayout(mContext)
        bottomContainer = LinearLayout(mContext)
        ivLoading = ImageView(mContext)
        tvCancel = TextView(mContext)
        tvLoading = TextView(mContext)

        rootContainer.orientation = LinearLayout.VERTICAL
        rootContainer.setBackgroundResource(R.drawable.shape_bg_loading_corner_black)

        paramsWW.setMargins(50,50,50,50)
        imgContainer.orientation = LinearLayout.HORIZONTAL
        imgContainer.layoutParams = paramsWW
        imgContainer.gravity = Gravity.CENTER_VERTICAL
        ivLoading.setImageResource(R.drawable.icon_loading)
        val loadAnimation = AnimationUtils.loadAnimation(mContext, R.anim.rotate_anim)
        loadAnimation.interpolator = LinearInterpolator()
        ivLoading.startAnimation(loadAnimation)
        imgContainer.addView(ivLoading)

        tvLoading.text = "正在上传..."
        tvLoading.textSize = 20F
        tvLoading.setTextColor(Color.WHITE)

        imgContainer.addView(tvLoading)

        rootContainer.addView(imgContainer)

        paramsMM.setMargins(0, 50, 50, 50)
        bottomContainer.layoutParams = paramsMM
        bottomContainer.gravity = Gravity.RIGHT

        tvCancel.gravity = Gravity.RIGHT
        tvCancel.text = "取消"
        tvCancel.setTextColor(Color.WHITE)
        bottomContainer.addView(tvCancel)
        rootContainer.addView(bottomContainer)

        tvCancel.setOnClickListener { this.dismiss() }

        setContentView(rootContainer)
//        contentView = rootContainer
    }
}