package com.jone.several.listener

import com.jone.several.utils.Size

/**
 * @fileName CameraOpenListener
 * Created by YiangJone on 2018/6/4.
 * @describe
 */

 interface CameraOpenListener<CameraId, SurfaceListener> {

    fun onCameraOpened(openedCameraId: CameraId, previewSize :Size,surfaceListener: SurfaceListener)

    fun onCameraOpenError()
}