package com.jone.several.ui.widget

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * @fileName PreviewViewPager
 * Created by YiangJone on 2018/6/6.
 * @describe
 */


class PreviewViewPager : ViewPager {
    interface TouchListener {
        fun onTouch()
    }

    private var listener: TouchListener? = null
    private var downX = 0F
    private var downY = 0F

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    override fun onTouchEvent(ev: MotionEvent): Boolean {

//        return  listener?.let { it.onTouch() }?:super.onTouchEvent(ev)
        return super.onTouchEvent(ev)
    }

    fun addTouchListener(listener: TouchListener) {
        this.listener = listener
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
//       return  when (ev.action) {
//            MotionEvent.ACTION_DOWN -> {
//                downX = ev.x
//                downY  = ev.y
//                return super.onInterceptTouchEvent(ev)
//            }
//            MotionEvent.ACTION_UP->{
//                var offset = Math.abs(downX-ev.x)
//                if(offset>10){
//                    return super.onInterceptTouchEvent(ev)
//                }
//                else {
//                    listener?.let { it.onTouch();true }?:super.onInterceptTouchEvent(ev)
//                }
//            }
//            else->{
//                return super.onInterceptTouchEvent(ev)
//            }
//        }



        try {
            return super.onInterceptTouchEvent(ev)
        } catch (ex: IllegalArgumentException) {
            ex.printStackTrace()
        }

        return false
    }
}
