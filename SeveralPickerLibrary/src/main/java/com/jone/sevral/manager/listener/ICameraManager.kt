package com.jone.sevral.manager.listener

import android.content.Context
import android.view.MotionEvent
import com.jone.sevral.config.CameraConfig
import com.jone.sevral.config.CameraConfigProvider
import com.jone.sevral.listener.CameraCloseListener
import com.jone.sevral.listener.CameraOpenListener
import com.jone.sevral.listener.CameraPictureListener
import java.io.File

/**
 * @fileName ICameraManager
 * Created by YiangJone on 2018/6/4.
 * @describe
 */

interface ICameraManager<CameraId, SurfaceListener>{

    fun initManager(mContext: Context,cameraConfigProvider: CameraConfigProvider)

    fun releaseManager()

     fun openCamera(mCameraId: CameraId, openListener: CameraOpenListener<CameraId, SurfaceListener>)

    fun setFlashMode(@CameraConfig.FlashMode flashMode: Int)

     fun closeCamera(closeListener: CameraCloseListener<CameraId>?)


    fun cameraPictureTaken(outFile: File, listener: CameraPictureListener)

    fun getPhotoOrientation(@CameraConfig.SensorPosition sensorPosition:Int):Int

    fun prepareCameraOutputs()

}