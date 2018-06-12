package com.jone.several.lifecycle

import android.content.Context
import android.view.SurfaceHolder
import com.jone.several.config.CameraConfig
import com.jone.several.config.CameraConfigProvider
import com.jone.several.listener.CameraOpenListener
import com.jone.several.listener.CameraPictureListener
import com.jone.several.listener.CameraView
import com.jone.several.manager.impl.Camera1Manager
import com.jone.several.utils.CameraUtils
import com.jone.several.utils.Size
import com.jone.several.ui.widget.AutoFitSurfaceView

/**
 * @fileName Camera1Lifecycle
 * Created by YiangJone on 2018/6/4.
 * @describe
 */

class Camera1Lifecycle(var mContext: Context, var cameraConfigProvider: CameraConfigProvider, var mCameraView: CameraView) : BaseLifecycle(), CameraOpenListener<Int, SurfaceHolder.Callback>, CameraPictureListener {
    private var mCameraId: Int = 0
    private var mCameraManager: Camera1Manager

    init {
        mCameraManager = Camera1Manager()
        mCameraManager.initManager(mContext, cameraConfigProvider)
        mCameraId = mCameraManager.mFaceBackCameraId?:0
    }

    override fun onResume() {
        mCameraManager.openCamera(mCameraId, this)
    }

    override fun onCameraOpened(openedCameraId: Int, previewSize : Size, surfaceListener: SurfaceHolder.Callback) {
        mCameraView.updateCameraPreview(previewSize,AutoFitSurfaceView(mContext, surfaceListener))
    }

    override fun onCameraOpenError() {
    }


    override fun cameraPictureTaken(outPath: String) {
        val outputMediaFile = CameraUtils.getOutputMediaFile(mContext, CameraConfig.MEDIA_ACTION_PHOTO, outPath, "IMG_PICKER1_" + System.currentTimeMillis())
        mCameraManager.cameraPictureTaken(outputMediaFile,this)
    }

    override fun onPictureTaken(path: String) {
        mCameraView.onPhotoTaken(path)
    }

    override fun onPictureTakeError() {
        mCameraView.onPhotoTakeFail()
    }


    override fun onPause() {
        mCameraManager.closeCamera(null)
    }

    override fun onDestroy() {
        mCameraManager.closeCamera(null)
        mCameraManager.releaseManager()
    }

    private fun setmCameraId(cameraId: Int) {
        this.mCameraId = cameraId
        mCameraManager.mCameraId = cameraId
    }

   override fun setFlashMode(@CameraConfig.FlashMode flashMode: Int) {
        mCameraManager.setFlashMode(flashMode)
    }

}