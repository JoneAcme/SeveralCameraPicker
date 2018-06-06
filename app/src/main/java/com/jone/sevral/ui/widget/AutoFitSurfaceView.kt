package com.jone.sevral.ui.widget

import android.annotation.SuppressLint
import android.content.Context
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View

/**
 * @fileName AutoFitSurfaceView
 * Created by YiangJone on 2018/6/4.
 * @describe
 */


@SuppressLint("ViewConstructor")
class AutoFitSurfaceView(context: Context, callback: SurfaceHolder.Callback) : SurfaceView(context) {

    private val surfaceHolder: SurfaceHolder

    private var ratioWidth: Int = 0
    private var ratioHeight: Int = 0

    init {

        this.surfaceHolder = holder

        this.surfaceHolder.addCallback(callback)
        this.surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
    }

    /*
     * Sets the aspect ratio for this view. The size of the view will be measured based on the ratio
     * calculated fromList the parameters. Note that the actual sizes of parameters don't matter, that
     * is, calling setAspectRatio(2, 3) and setAspectRatio(4, 6) make the same result.
     *
     * @param width  Relative horizontal size
     * @param height Relative vertical size
     */
    fun setAspectRatio(width: Int, height: Int) {
        if (width < 0 || height < 0) {
            throw IllegalArgumentException("Size cannot be negative.")
        }
        ratioWidth = width
        ratioHeight = height
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.resolveSize(suggestedMinimumWidth, widthMeasureSpec)
        val height = View.resolveSize(suggestedMinimumHeight, heightMeasureSpec)

        if (0 == ratioWidth || 0 == ratioHeight) {
            setMeasuredDimension(width, height)
        } else {
            if (width < height * (ratioWidth / ratioHeight.toFloat())) {
                setMeasuredDimension(width, (width * (ratioWidth / ratioHeight.toFloat())).toInt())
            } else {
                setMeasuredDimension((height * (ratioWidth / ratioHeight.toFloat())).toInt(), height)
            }
        }
    }

    companion object {

        private val TAG = "AutoFitSurfaceView"
    }
}
