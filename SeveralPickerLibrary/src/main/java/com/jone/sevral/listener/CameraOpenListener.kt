package com.jone.sevral.listener

import com.jone.sevral.utils.Size

/**
 * @fileName CameraOpenListener
 * Created by YiangJone on 2018/6/4.
 * @describe
 */

 interface CameraOpenListener<CameraId, SurfaceListener> {

    fun onCameraOpened(openedCameraId: CameraId, previewSize :Size,surfaceListener: SurfaceListener)

    fun onCameraOpenError()
}