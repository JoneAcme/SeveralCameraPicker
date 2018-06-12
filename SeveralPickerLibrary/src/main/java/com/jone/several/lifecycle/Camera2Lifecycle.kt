package com.jone.several.lifecycle

import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.view.TextureView
import com.jone.several.config.CameraConfig
import com.jone.several.config.CameraConfigProvider
import com.jone.several.listener.CameraCloseListener
import com.jone.several.listener.CameraOpenListener
import com.jone.several.listener.CameraPictureListener
import com.jone.several.listener.CameraView
import com.jone.several.manager.impl.Camera2Manager
import com.jone.several.ui.widget.AutoFitTextureView
import com.jone.several.utils.CameraUtils
import com.jone.several.utils.Size

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
/**
 * @fileName Camera2Lifecycle
 * Created by YiangJone on 2018/6/4.
 * @describe
 */
class Camera2Lifecycle(private var mContext: Context, private var cameraConfigProvider: CameraConfigProvider, private var mCameraView: CameraView) :
        BaseLifecycle(), CameraOpenListener<String, TextureView.SurfaceTextureListener>, CameraCloseListener<String>, CameraPictureListener {


    override fun onCameraClosed(closedCameraId: String) {
//        mCameraView.releaseCameraPreview()
    }

    override fun onCameraOpened(openedCameraId: String, previewSize : Size, surfaceListener: TextureView.SurfaceTextureListener) {
        mCameraView.updateCameraPreview(previewSize, AutoFitTextureView(mContext,surfaceListener))
    }

    override fun onCameraOpenError() {
    }

    private var mCameraId: String? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private var mCamera2Manager = Camera2Manager()

    init {
        mCamera2Manager.initManager(mContext, cameraConfigProvider)
        setCameraId(mCamera2Manager.mFaceBackCameraId ?: "")
    }

    private fun setCameraId(currentCameraId: String) {
        this.mCameraId = currentCameraId
        mCamera2Manager.mFaceBackCameraId = currentCameraId
    }

    override fun onResume() {
        mCamera2Manager.openCamera(mCameraId ?: "", this)
    }

    override fun onPause() {
        mCamera2Manager.closeCamera(this)
        mCameraView.releaseCameraPreview()
    }

    override fun onDestroy() {
        mCamera2Manager.releaseManager()
    }

    override fun cameraPictureTaken(outPath: String) {
        val outputMediaFile = CameraUtils.getOutputMediaFile(mContext, CameraConfig.MEDIA_ACTION_PHOTO, outPath, "IMG_PICKER2_" + System.currentTimeMillis())
        mCamera2Manager.cameraPictureTaken(outputMediaFile,this)
    }

    override fun onPictureTaken(path: String) {
        mCameraView.onPhotoTaken(path)
    }

    override fun onPictureTakeError() {
        mCameraView.onPhotoTakeFail()
    }



    override fun setFlashMode(@CameraConfig.FlashMode flashMode: Int) {
        mCamera2Manager.setFlashMode(flashMode)
    }



}