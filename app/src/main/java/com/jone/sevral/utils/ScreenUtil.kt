package com.jone.sevral.utils

import android.app.Activity
import android.content.Context
import android.graphics.Matrix
import android.graphics.Rect
import android.util.DisplayMetrics
import java.lang.reflect.Field
import android.graphics.RectF
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth



/**
 * @fileName ScreenUtil
 * Created by YiangJone on 2018/6/5.
 * @describe
 */


object ScreenUtil {
    /**
     * dp2px
     */
    fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    fun getScreenWidth(context: Context): Int {
        val localDisplayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(localDisplayMetrics)
        return localDisplayMetrics.widthPixels
    }

    fun getScreenHeight(context: Context): Int {
        val localDisplayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(localDisplayMetrics)
        return localDisplayMetrics.heightPixels - getStatusBarHeight(context)
    }

    fun getStatusBarHeight(context: Context): Int {
        var c: Class<*>? = null
        var obj: Any? = null
        var field: Field? = null
        var x = 0
        var statusBarHeight = 0
        try {
            c = Class.forName("com.android.internal.R\$dimen")
            obj = c!!.newInstance()
            field = c.getField("status_bar_height")
            x = Integer.parseInt(field!!.get(obj).toString())
            statusBarHeight = context.resources.getDimensionPixelSize(x)
        } catch (e1: Exception) {
            e1.printStackTrace()
        }

        return statusBarHeight
    }

     fun calculateTapArea(x: Float, y: Float, surfaceWidth:Int,sufaceHeight:Int,coefficient: Float): Rect {
        val areaSize = java.lang.Float.valueOf(20 * coefficient).toInt()

        val left = clamp(x.toInt() - areaSize / 2, 0, surfaceWidth - areaSize)
        val top = clamp(y.toInt() - areaSize / 2, 0, sufaceHeight - areaSize)

        val rectF = RectF(left.toFloat(), top.toFloat(), (left + areaSize).toFloat(), (top + areaSize).toFloat())
        Matrix().mapRect(rectF)

        return Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF.bottom))
    }

    private fun clamp(x: Int, min: Int, max: Int): Int {
        if (x > max) {
            return max
        }
        return if (x < min) {
            min
        } else x
    }
}
