package com.jone.several.utils

/**
 * @fileName DoubleUtils
 * Created by YiangJone on 2018/6/6.
 * @describe
 */

object DoubleUtils {
    /**
     * Prevent continuous click, jump media_two pages
     */
    private var lastClickTime: Long = 0
    private val TIME: Long = 800

    val isFastDoubleClick: Boolean
        get() {
            val time = System.currentTimeMillis()
            if (time - lastClickTime < TIME) {
                return true
            }
            lastClickTime = time
            return false
        }
}