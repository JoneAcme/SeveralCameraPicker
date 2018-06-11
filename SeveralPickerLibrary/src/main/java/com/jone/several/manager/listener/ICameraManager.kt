package com.jone.several.manager.listener

import android.content.Context
import com.jone.several.config.CameraConfig
import com.jone.several.config.CameraConfigProvider
import com.jone.several.listener.CameraCloseListener
import com.jone.several.listener.CameraOpenListener
import com.jone.several.listener.CameraPictureListener
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