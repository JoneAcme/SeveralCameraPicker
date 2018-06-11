package com.jone.several.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import com.jone.several.R
import com.jone.several.utils.ScreenUtil

/**
 * @fileName CameraButton
 * Created by YiangJone on 2018/6/7.
 * @describe
 */

class CameraButton(context: Context, attr: AttributeSet) : View(context, attr) {

    private var boundingSize = 100F
    private var centerX = 50F
    private var centerY = 50F

    private var centerOffset = 10F
    private var overTransWidth = 10F
    private var overCircleWidth = 1F

    private var transCircleRadius = 0F
    private var overTransRadius = 0F

    private var centerTransColor = ContextCompat.getColor(getContext(), R.color.circle_shallow_translucent_background)
    private var overTransColor = ContextCompat.getColor(getContext(), R.color.white_sixty_percent)
    private var overCircleColor = ContextCompat.getColor(getContext(), R.color.black)

    private var centerPaint: Paint
    private var overTransPaint: Paint
    private var overCirclePaint: Paint

    init {
        boundingSize = ScreenUtil.dip2px(getContext(), 80F).toFloat()
        centerOffset = ScreenUtil.dip2px(getContext(), 10F).toFloat()
        overTransWidth = ScreenUtil.dip2px(getContext(), 5F).toFloat()
        centerX = boundingSize / 2
        centerY = boundingSize / 2


        centerPaint = Paint()
        overTransPaint = Paint()
        overCirclePaint = Paint()

        centerPaint.isAntiAlias = true
        overTransPaint.isAntiAlias = true
        overCirclePaint.isAntiAlias = true

        overTransPaint.strokeWidth = overTransWidth
        overCirclePaint.strokeWidth = overCircleWidth

        centerPaint.style = (Paint.Style.FILL_AND_STROKE)
        overTransPaint.style = (Paint.Style.STROKE)
        overCirclePaint.style = (Paint.Style.STROKE)

        centerPaint.color = centerTransColor
        overTransPaint.color = overTransColor
        overCirclePaint.color = overCircleColor

        transCircleRadius = boundingSize / 2-overCircleWidth - overTransWidth - centerOffset
        overTransRadius = transCircleRadius+centerOffset

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(boundingSize.toInt(), boundingSize.toInt())
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(centerX,centerX,transCircleRadius,centerPaint)
        canvas.drawCircle(centerX,centerX,overTransRadius,overTransPaint)
//        canvas.drawCircle(centerX,centerX,overTransRadius+overTransWidth,overCirclePaint)
    }
}