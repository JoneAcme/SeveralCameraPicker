package com.jone.sevral.manager

import android.content.Context
import android.graphics.Rect
import android.hardware.Camera
import android.hardware.camera2.CameraDevice
import android.os.*
import android.util.Log
import android.view.MotionEvent
import com.jone.sevral.config.CameraConfigProvider
import com.jone.sevral.manager.listener.ICameraManager
import com.jone.sevral.utils.ScreenUtil
import com.jone.sevral.utils.Size

/**
 * @fileName BaseCameraManager
 * Created by YiangJone on 2018/6/4.
 * @describe
 */
//interface CameraManager<CameraId, SurfaceListener>
abstract class BaseCameraManager<CameraId, SurfaceListener> : ICameraManager<CameraId, SurfaceListener>{
    private val TAG = "BaseCameraManager"
    var mContext: Context? = null
    var mCameraId: CameraId? = null

    var mNumberOfCameras: Int = 0
    var mFaceBackCameraId: CameraId? = null
    var mFaceBackCameraOrientation: Int = 0
    var mFaceFrontCameraId: CameraId? = null
    var mFaceFrontCameraOrientation: Int = 0


    var mBackgroundThread: HandlerThread? = null
    var mBackgroundHandler: Handler? = null
    var mUiiHandler = Handler(Looper.getMainLooper())

    lateinit var cameraConfigProvider: CameraConfigProvider

    lateinit var mPhotoSize: Size
    lateinit var mPreviewSize: Size

    override fun initManager(mContext: Context, cameraConfigProvider: CameraConfigProvider) {
        this.mContext = mContext
        this.cameraConfigProvider = cameraConfigProvider
        startBackgroundThread()
    }

    override fun releaseManager() {
        this.mContext = null
        stopBackgroundThread()
    }


    private fun startBackgroundThread() {
        mBackgroundThread = HandlerThread(TAG, Process.THREAD_PRIORITY_BACKGROUND)
        mBackgroundThread?.start()
        mBackgroundHandler = Handler(mBackgroundThread?.looper)
    }

    private fun stopBackgroundThread() {
        if (Build.VERSION.SDK_INT > 17) {
            mBackgroundThread?.quitSafely()
        } else
            mBackgroundThread?.quit()

        try {
            mBackgroundThread?.join()
        } catch (e: InterruptedException) {
            Log.e(TAG, "stopBackgroundThread: ", e)
        } finally {
            mBackgroundThread = null
            mBackgroundHandler = null
        }
    }

}