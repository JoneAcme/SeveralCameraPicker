package com.jone.sevral.listener

/**
 * @fileName CameraCloseListener
 * Created by YiangJone on 2018/6/4.
 * @describe
 */

interface CameraCloseListener<CameraId> {
    fun onCameraClosed(closedCameraId: CameraId)
}
