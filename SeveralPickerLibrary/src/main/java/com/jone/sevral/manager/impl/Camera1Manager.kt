package com.jone.sevral.manager.impl

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Rect
import android.hardware.Camera
import android.media.ExifInterface
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.MotionEvent
import android.view.Surface
import android.view.SurfaceHolder
import android.view.WindowManager
import com.jone.sevral.config.CameraConfig
import com.jone.sevral.config.CameraConfigProvider
import com.jone.sevral.listener.CameraCloseListener
import com.jone.sevral.listener.CameraOpenListener
import com.jone.sevral.listener.CameraPictureListener
import com.jone.sevral.manager.BaseCameraManager
import com.jone.sevral.utils.CameraUtils
import com.jone.sevral.utils.ImageUtils
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import com.jone.sevral.utils.Size

/**
 * @fileName Camera1Manager
 * Created by YiangJone on 2018/6/4.
 * @describe
 */

class Camera1Manager : BaseCameraManager<Int, SurfaceHolder.Callback>() {


    private val TAG = "Camera1Manager"
    private var camera: Camera? = null
    private var surface: Surface? = null
    private var futurFlashMode: Int = CameraConfig.FLASH_MODE_AUTO

    override fun initManager(mContext: Context, cameraConfigProvider: CameraConfigProvider) {
        super.initManager(mContext, cameraConfigProvider)
        mNumberOfCameras = Camera.getNumberOfCameras()

        for (i in 0 until mNumberOfCameras) {
            val cameraInfo = Camera.CameraInfo()
            Camera.getCameraInfo(i, cameraInfo)
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                mFaceBackCameraId = i
                mFaceBackCameraOrientation = cameraInfo.orientation
            } else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                mFaceFrontCameraId = i
                mFaceFrontCameraOrientation = cameraInfo.orientation
            }
        }
    }

    override fun openCamera(mCameraId: Int, openListener: CameraOpenListener<Int, SurfaceHolder.Callback>) {
        this.mCameraId = mCameraId
        mBackgroundHandler?.post({
            try {
                camera = Camera.open(mCameraId)
                camera?.lock()
                prepareCameraOutputs()
                setFlashMode(futurFlashMode)
                mUiiHandler.post {
                    openListener.onCameraOpened(mCameraId, mPreviewSize,object : SurfaceHolder.Callback {
                        override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
                            if (surfaceHolder.surface == null) {
                                return
                            }
                            surface = surfaceHolder.surface

                            try {
                                camera?.stopPreview()
                            } catch (ignore: Exception) {
                            }

                            startPreview(surfaceHolder)
                        }

                        override fun surfaceChanged(surfaceHolder: SurfaceHolder, format: Int, width: Int, height: Int) {
                            if (surfaceHolder.surface == null) {
                                return
                            }

                            surface = surfaceHolder.surface

                            try {
                                camera?.stopPreview()
                            } catch (ignore: Exception) {
                            }

                            startPreview(surfaceHolder)
                        }

                        override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {

                        }
                    })
                }
            } catch (error: Exception) {
                Log.d(TAG, "Can't open camera: " + error.message)
                mUiiHandler.post { openListener.onCameraOpenError() }
            }
        })
    }

    override fun prepareCameraOutputs() {
        try {
            val previewSizes = Size.fromList(camera?.parameters?.supportedPreviewSizes)
            val pictureSizes = Size.fromList(camera?.parameters?.supportedPictureSizes)

            mPhotoSize = CameraUtils.getPictureSize(
                    if (pictureSizes == null || pictureSizes.isEmpty()) previewSizes else pictureSizes,
                    if (cameraConfigProvider.mediaQuality === CameraConfig.MEDIA_QUALITY_AUTO)
                        CameraConfig.MEDIA_QUALITY_HIGHEST
                    else
                        cameraConfigProvider.mediaQuality)

            mPreviewSize = CameraUtils.getSizeWithClosestRatio(previewSizes, mPhotoSize.width, mPhotoSize.height)
        } catch (e: Exception) {
            Log.e(TAG, "Error while setup camera sizes.")
        }
    }

    private fun startPreview(surfaceHolder: SurfaceHolder) {
        camera?.let {
            val cameraInfo = Camera.CameraInfo()
            Camera.getCameraInfo(mCameraId ?: 0, cameraInfo)
            val cameraRotationOffset = cameraInfo.orientation

            val parameters = it.parameters
            setAutoFocus(it, parameters)
            setFlashMode(cameraConfigProvider.flashMode)


            if (cameraConfigProvider.mediaAction === CameraConfig.MEDIA_ACTION_PHOTO || cameraConfigProvider.mediaAction === CameraConfig.MEDIA_ACTION_UNSPECIFIED)
                turnPhotoCameraFeaturesOn(it, parameters)
            else if (cameraConfigProvider.mediaAction === CameraConfig.MEDIA_ACTION_PHOTO)
                turnVideoCameraFeaturesOn(it, parameters)

            /**
             *  调整摄像头预览的角度
             */
            val rotation = (mContext?.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.rotation
            var degrees = 0
            when (rotation) {
                Surface.ROTATION_0 -> degrees = 0
                Surface.ROTATION_90 -> degrees = 90
                Surface.ROTATION_180 -> degrees = 180
                Surface.ROTATION_270 -> degrees = 270
            }

            var displayRotation = 0
            //前置摄像头
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                displayRotation = (cameraRotationOffset + degrees) % 360
                displayRotation = (360 - displayRotation) % 360 // compensate
            } else {//后置摄像头
                displayRotation = (cameraRotationOffset - degrees + 360) % 360
            }

            it.setDisplayOrientation(displayRotation)

            /**
             *  开启录制功能
             */
            if (Build.VERSION.SDK_INT > 13 && (cameraConfigProvider.mediaAction === CameraConfig.MEDIA_ACTION_VIDEO || cameraConfigProvider.mediaAction === CameraConfig.MEDIA_ACTION_UNSPECIFIED)) {
                //                parameters.setRecordingHint(true);
            }

            if (Build.VERSION.SDK_INT > 14
                    && parameters.isVideoStabilizationSupported
                    && (cameraConfigProvider.mediaAction === CameraConfig.MEDIA_ACTION_VIDEO || cameraConfigProvider.mediaAction === CameraConfig.MEDIA_ACTION_UNSPECIFIED)) {
                parameters.videoStabilization = true
            }

            //设置预览以及图片宽高
            parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height)
            parameters.setPictureSize(mPhotoSize.width, mPhotoSize.height)

            it.parameters = parameters
            it.setPreviewDisplay(surfaceHolder)
            it.startPreview()
        }
    }

    private fun setAutoFocus(camera: Camera, parameters: Camera.Parameters) {
        try {
            if (parameters.supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                parameters.focusMode = Camera.Parameters.FOCUS_MODE_AUTO
                camera.parameters = parameters
            }
        } catch (ignore: Exception) {
        }

    }

    override fun setFlashMode(@CameraConfig.FlashMode flashMode: Int) {
        camera?.let { setFlashMode(camera!!, camera?.parameters!!, flashMode) }
        futurFlashMode = flashMode
    }


    private fun setFlashMode(camera: Camera, parameters: Camera.Parameters, @CameraConfig.FlashMode flashMode: Int) {
        try {
            when (flashMode) {
                CameraConfig.FLASH_MODE_AUTO -> parameters.flashMode = Camera.Parameters.FLASH_MODE_AUTO
                CameraConfig.FLASH_MODE_ON -> parameters.flashMode = Camera.Parameters.FLASH_MODE_ON
                CameraConfig.FLASH_MODE_OFF -> parameters.flashMode = Camera.Parameters.FLASH_MODE_OFF
                else -> parameters.flashMode = Camera.Parameters.FLASH_MODE_AUTO
            }
            camera.parameters = parameters
        } catch (ignore: Exception) {
        }

    }

    private fun turnPhotoCameraFeaturesOn(camera: Camera, parameters: Camera.Parameters) {
        if (parameters.supportedFocusModes.contains(
                Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
        }
        camera.parameters = parameters
    }

    private fun turnVideoCameraFeaturesOn(camera: Camera, parameters: Camera.Parameters) {
        if (parameters.supportedFocusModes.contains(
                Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            parameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO
        }
        camera.parameters = parameters
    }


    override fun cameraPictureTaken(outFile: File, listener: CameraPictureListener) {
        mBackgroundHandler?.post {
            camera?.let {
                setCameraPhotoQuality(it)
                it.takePicture(null, null, Camera.PictureCallback { bytes, camera -> onPictureTaken(bytes, outFile, camera, listener) })

            }
        }
    }

    private fun setCameraPhotoQuality(camera: Camera) {
        val parameters = camera.parameters

        parameters.pictureFormat = PixelFormat.JPEG

        parameters.jpegQuality = when (cameraConfigProvider.mediaQuality) {
            CameraConfig.MEDIA_QUALITY_LOW -> 50
            CameraConfig.MEDIA_QUALITY_MEDIUM -> 75
            CameraConfig.MEDIA_QUALITY_HIGH -> 100
            CameraConfig.MEDIA_QUALITY_HIGHEST -> 100
            else -> 100
        }

        parameters.setPictureSize(mPhotoSize.width, mPhotoSize.height)

        camera.parameters = parameters
    }


    protected fun onPictureTaken(bytes: ByteArray, outFile: File, camera: Camera, callback: CameraPictureListener) {
        try {
            val fileOutputStream = FileOutputStream(outFile)
            fileOutputStream.write(bytes)
            fileOutputStream.close()
        } catch (error: FileNotFoundException) {
            Log.e(TAG, "File not found: " + error.message)
        } catch (error: IOException) {
            Log.e(TAG, "Error accessing file: " + error.message)
        } catch (error: Throwable) {
            Log.e(TAG, "Error saving file: " + error.message)
        }

        try {
            val exif = ExifInterface(outFile.absolutePath)
            exif.setAttribute(ExifInterface.TAG_ORIENTATION, "" + getPhotoOrientation(cameraConfigProvider.sensorPosition))
            exif.saveAttributes()

            ImageUtils.amendRotatePhoto(outFile.absolutePath)
            mUiiHandler.post {

                callback.onPictureTaken(outFile.absolutePath) }
            camera.startPreview()
        } catch (error: Throwable) {
            callback.onPictureTakeError()
            Log.e(TAG, "Can't save exif info: " + error.message)
        }

    }


    override fun getPhotoOrientation(@CameraConfig.SensorPosition sensorPosition:Int): Int {
        /**
         *  ??????????????
         */
        var rotate = 0
        var orientation = 0
        if (mFaceFrontCameraId == mCameraId) {
            rotate = (360 + mFaceFrontCameraOrientation + cameraConfigProvider.degrees) % 360
        } else {
            rotate = (360 + mFaceBackCameraOrientation - cameraConfigProvider.degrees) % 360
        }

        if (rotate == 0) {
            orientation = ExifInterface.ORIENTATION_NORMAL
        } else if (rotate == 90) {
            orientation = ExifInterface.ORIENTATION_ROTATE_90
        } else if (rotate == 180) {
            orientation = ExifInterface.ORIENTATION_ROTATE_180
        } else if (rotate == 270) {
            orientation = ExifInterface.ORIENTATION_ROTATE_270
        }
        return orientation
    }


    override fun closeCamera(closeListener: CameraCloseListener<Int>?) {
        mBackgroundHandler?.post {
            camera?.let {
                it.release()
                mUiiHandler.post { closeListener?.onCameraClosed(mCameraId ?: 0) }
            }
            camera = null
        }
    }

    override fun releaseManager() {
        super.releaseManager()
    }

}