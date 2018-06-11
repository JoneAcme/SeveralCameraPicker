package com.jone.sevral.manager.impl

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.ImageFormat
import android.graphics.Point
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.hardware.camera2.params.StreamConfigurationMap
import android.media.ImageReader
import android.os.Build
import android.support.annotation.IntDef
import android.support.annotation.RequiresApi
import android.text.TextUtils
import android.util.Log
import android.view.MotionEvent
import android.view.Surface
import android.view.TextureView
import android.view.WindowManager
import com.jone.sevral.config.CameraConfig
import com.jone.sevral.config.CameraConfigProvider
import com.jone.sevral.listener.CameraCloseListener
import com.jone.sevral.listener.CameraOpenListener
import com.jone.sevral.listener.CameraPictureListener
import com.jone.sevral.manager.BaseCameraManager
import com.jone.sevral.utils.CameraUtils
import com.jone.sevral.utils.ImageSaver
import com.jone.sevral.utils.ImageUtils
import com.jone.sevral.utils.Size
import java.io.File
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.util.*
import kotlin.properties.Delegates

/**
 * @fileName Camera2Manager
 * Created by YiangJone on 2018/6/4.
 * @describe
 */

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class Camera2Manager : BaseCameraManager<String, TextureView.SurfaceTextureListener>(), TextureView.SurfaceTextureListener,
ImageReader.OnImageAvailableListener{
    private val TAG = "Camera2Manager"

    companion object {
        private const val STATE_PREVIEW = 0L
        private const val STATE_WAITING_LOCK = 1L
        private const val STATE_WAITING_PRE_CAPTURE = 2L
        private const val STATE_WAITING_NON_PRE_CAPTURE = 3L
        private const val STATE_PICTURE_TAKEN = 4L
    }

    private var mWindowSize:Size by Delegates.notNull()

    private lateinit var mCameraManager: CameraManager
    private var openListener: CameraOpenListener<String, TextureView.SurfaceTextureListener>? = null
    private var mCameraPictureListener: CameraPictureListener? = null

    private var mSurfaceTexture: SurfaceTexture? = null
    private var mWorkingSurface: Surface? = null
    private var mImageReader: ImageReader? = null
    @CameraPreviewState
    private var mPreviewState = STATE_PREVIEW

    private var mCameraDevice: CameraDevice? = null
    private var mPreviewRequest: CaptureRequest? = null
    private var mPreviewRequestBuilder: CaptureRequest.Builder? = null
    private var mCaptureSession: CameraCaptureSession? = null
    private var mFrontCameraCharacteristics: CameraCharacteristics? = null
    private var mBackCameraCharacteristics: CameraCharacteristics? = null
    private var mFrontCameraStreamConfigurationMap: StreamConfigurationMap? = null
    private var mBackCameraStreamConfigurationMap: StreamConfigurationMap? = null

    private lateinit var mOutputPath:File

    override fun initManager(mContext: Context, cameraConfigProvider: CameraConfigProvider) {
        super.initManager(mContext, cameraConfigProvider)
        val windowManager = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        mWindowSize = Size(size.x, size.y)

        mCameraManager = mContext.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            val ids = mCameraManager.cameraIdList
            mNumberOfCameras = ids.size
            for (id in ids) {
                val characteristics = mCameraManager.getCameraCharacteristics(id)

                val orientation = characteristics.get(CameraCharacteristics.LENS_FACING)!!
                if (orientation == CameraCharacteristics.LENS_FACING_FRONT) {
                    mFaceFrontCameraId = id
                    mFaceFrontCameraOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)
                    mFrontCameraCharacteristics = characteristics
                } else {
                    mFaceBackCameraId = id
                    mFaceBackCameraOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)
                    mBackCameraCharacteristics = characteristics
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error during camera initialize")
        }

    }

    @SuppressLint("MissingPermission")
    override fun openCamera(mCameraId: String, openListener: CameraOpenListener<String, TextureView.SurfaceTextureListener>) {
        this.mCameraId = mCameraId
        this.openListener = openListener
        mBackgroundHandler?.post {
            if (mContext == null || cameraConfigProvider == null) {
                Log.e(TAG, "openCamera: ")
                openListener.onCameraOpenError()
                return@post
            }
            prepareCameraOutputs()
            try {
                mCameraManager.openCamera(mCameraId, mStateCallback, mBackgroundHandler)
            } catch (e: Exception) {
                Log.e(TAG, "openCamera: ", e)
                mUiiHandler.post { openListener.onCameraOpenError() }
            }
        }
    }

    private val mStateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(cameraDevice: CameraDevice) {
            this@Camera2Manager.mCameraDevice = cameraDevice
            mUiiHandler.post {
                if (!TextUtils.isEmpty(mCameraId))
                    openListener?.onCameraOpened(mCameraId ?: "", mPreviewSize,this@Camera2Manager)
            }
        }

        override fun onDisconnected(cameraDevice: CameraDevice) {
            cameraDevice.close()
            this@Camera2Manager.mCameraDevice = null
            mUiiHandler.post { openListener?.onCameraOpenError() }
        }


        override fun onError(cameraDevice: CameraDevice, error: Int) {
            cameraDevice.close()
            this@Camera2Manager.mCameraDevice = null
            mUiiHandler.post { openListener?.onCameraOpenError() }
        }
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
        surface?.let { startPreview(it) }
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean = true

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
        surface?.let { startPreview(it) }
    }


    private fun startPreview(texture: SurfaceTexture?) {
        try {
            if (texture == null) return

            this.mSurfaceTexture = texture

            texture.setDefaultBufferSize(mPreviewSize.width, mPreviewSize.height)

            mWorkingSurface = Surface(texture)

            mPreviewRequestBuilder = mCameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            mPreviewRequestBuilder?.addTarget(mWorkingSurface)

            mCameraDevice?.createCaptureSession(Arrays.asList(mWorkingSurface, mImageReader?.surface),
                    object : CameraCaptureSession.StateCallback() {
                        override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                            updatePreview(cameraCaptureSession)
                        }

                        override fun onConfigureFailed(
                                cameraCaptureSession: CameraCaptureSession) {
                            Log.d(TAG, "Fail while starting preview: ")
                        }
                    }, null)
        } catch (e: Exception) {
            Log.e(TAG, "Error while preparing surface for preview: ", e)
        }

    }

    override fun onImageAvailable(imageReader: ImageReader) {
        val outputFile = mOutputPath
        mBackgroundHandler?.post(ImageSaver(imageReader.acquireNextImage(), outputFile, object : ImageSaver.ImageSaverCallback {
            override fun onSuccessFinish(bytes: ByteArray) {
                Log.d(TAG, "onPhotoSuccessFinish: ")

                ImageUtils.amendRotatePhoto(mOutputPath.absolutePath)

                if (mCameraPictureListener != null) {
                    mUiiHandler.post {
                        mCameraPictureListener?.onPictureTaken( mOutputPath.absolutePath)
                    }
                }
                unlockFocus()
            }

           override fun onError() {
                Log.d(TAG, "onPhotoError: ")
                mUiiHandler.post { mCameraPictureListener?.onPictureTakeError() }
            }
        }))

    }

    private fun unlockFocus() {
        try {
            mPreviewRequestBuilder?.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL)
            mCaptureSession?.capture(mPreviewRequestBuilder?.build(), captureCallback, mBackgroundHandler)
            mPreviewState = STATE_PREVIEW
            mCaptureSession?.setRepeatingRequest(mPreviewRequest, captureCallback, mBackgroundHandler)
        } catch (e: Exception) {
            Log.e(TAG, "Error during focus unlocking")
        }

    }

    private fun updatePreview(cameraCaptureSession: CameraCaptureSession) {
        if (null == mCameraDevice) {
            return
        }
        mCaptureSession = cameraCaptureSession

        setFlashModeAndBuildPreviewRequest(cameraConfigProvider.flashMode)
    }

    private fun setFlashModeAndBuildPreviewRequest(@CameraConfig.FlashMode flashMode: Int) {
        try {

            when (flashMode) {
                CameraConfig.FLASH_MODE_AUTO -> {
                    mPreviewRequestBuilder?.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH)
                    mPreviewRequestBuilder?.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_SINGLE)
                }
                CameraConfig.FLASH_MODE_ON -> {
                    mPreviewRequestBuilder?.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON)
                    mPreviewRequestBuilder?.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_SINGLE)
                }
                CameraConfig.FLASH_MODE_OFF -> {
                    mPreviewRequestBuilder?.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON)
                    mPreviewRequestBuilder?.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_OFF)
                }
                else -> {
                    mPreviewRequestBuilder?.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH)
                    mPreviewRequestBuilder?.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_SINGLE)
                }
            }

            mPreviewRequest = mPreviewRequestBuilder?.build()

            try {
                mCaptureSession?.setRepeatingRequest(mPreviewRequest, captureCallback, mBackgroundHandler)
            } catch (e: Exception) {
                Log.e(TAG, "Error updating preview: ", e)
            }

        } catch (ignore: Exception) {
            Log.e(TAG, "Error setting flash: ", ignore)
        }

    }

    override fun prepareCameraOutputs() {
        try {
            val characteristics = if (mCameraId.equals(mFaceBackCameraId)) mBackCameraCharacteristics else mFrontCameraCharacteristics

            if (mCameraId.equals(mFaceFrontCameraId) && mFrontCameraStreamConfigurationMap == null)
                mFrontCameraStreamConfigurationMap = characteristics?.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            else if (mCameraId.equals(mFaceBackCameraId) && mBackCameraStreamConfigurationMap == null)
                mBackCameraStreamConfigurationMap = characteristics?.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)

            val map = if (mCameraId.equals(mFaceBackCameraId)) mBackCameraStreamConfigurationMap else mFrontCameraStreamConfigurationMap

            mPhotoSize = CameraUtils.getPictureSize(Size.fromArray2(map?.getOutputSizes(ImageFormat.JPEG)),
                    if (cameraConfigProvider.mediaQuality === CameraConfig.MEDIA_QUALITY_AUTO)
                        CameraConfig.MEDIA_QUALITY_HIGHEST
                    else
                        cameraConfigProvider.mediaQuality)

            if (mWindowSize.height * mWindowSize.width > mPhotoSize.width * mPhotoSize.height) {
                mPreviewSize = CameraUtils.getOptimalPreviewSize(Size.fromArray2(map?.getOutputSizes(SurfaceTexture::class.java)), mPhotoSize.width, mPhotoSize.height)
            } else {
                mPreviewSize = CameraUtils.getOptimalPreviewSize(Size.fromArray2(map?.getOutputSizes(SurfaceTexture::class.java)), mWindowSize.getWidth(), mWindowSize.getHeight())
            }


            mImageReader = ImageReader.newInstance(mPhotoSize.width, mPhotoSize.height,
                    ImageFormat.JPEG, 2)
            mImageReader?.setOnImageAvailableListener(this, mBackgroundHandler)

            if (mPreviewSize == null)
                mPreviewSize = CameraUtils.chooseOptimalSize(Size.fromArray2(map?.getOutputSizes(SurfaceTexture::class.java)), mWindowSize.getWidth(), mWindowSize.getHeight(), mPhotoSize)


        } catch (e: Exception) {
        Log.e("openCamera",e.message)
            e.printStackTrace()
        }
    }

    private val captureCallback = object : CameraCaptureSession.CaptureCallback() {

        override fun onCaptureProgressed(session: CameraCaptureSession,
                                         request: CaptureRequest,
                                         partialResult: CaptureResult) {
            processCaptureResult(partialResult)
        }

        override fun onCaptureCompleted(session: CameraCaptureSession,
                                        request: CaptureRequest,
                                        result: TotalCaptureResult) {
            processCaptureResult(result)
        }

    }

    private fun processCaptureResult(result: CaptureResult) {
        when (mPreviewState) {
            STATE_PREVIEW -> {
            }
            STATE_WAITING_LOCK -> {
                val afState = result.get(CaptureResult.CONTROL_AF_STATE)
                if (afState == null) {
                    captureStillPicture()
                } else if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState
                        || CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState
                        || CaptureResult.CONTROL_AF_STATE_INACTIVE == afState
                        || CaptureResult.CONTROL_AF_STATE_PASSIVE_SCAN == afState) {
                    val aeState = result.get(CaptureResult.CONTROL_AE_STATE)
                    if (aeState == null || aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
                        mPreviewState = STATE_PICTURE_TAKEN
                        captureStillPicture()
                    } else {
                        runPreCaptureSequence()
                    }
                }
            }
            STATE_WAITING_PRE_CAPTURE -> {
                val aeState = result.get(CaptureResult.CONTROL_AE_STATE)
                if (aeState == null ||
                        aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
                        aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED) {
                    mPreviewState = STATE_WAITING_NON_PRE_CAPTURE
                }
            }
            STATE_WAITING_NON_PRE_CAPTURE -> {
                val aeState = result.get(CaptureResult.CONTROL_AE_STATE)
                if (aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                    mPreviewState = STATE_PICTURE_TAKEN
                    captureStillPicture()
                }
            }
            STATE_PICTURE_TAKEN -> {
            }
        }
    }

    private fun captureStillPicture() {
        try {
            if (null == mCameraDevice) {
                return
            }
            val captureBuilder = mCameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            captureBuilder?.addTarget(mImageReader?.surface)

            captureBuilder?.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
            captureBuilder?.set(CaptureRequest.JPEG_ORIENTATION, getPhotoOrientation(cameraConfigProvider.sensorPosition))

            val CaptureCallback = object : CameraCaptureSession.CaptureCallback() {
                override fun onCaptureCompleted(session: CameraCaptureSession,
                                                request: CaptureRequest,
                                                result: TotalCaptureResult) {
                    Log.d(TAG, "onCaptureCompleted: ")
                }
            }

            mCaptureSession?.stopRepeating()
            mCaptureSession?.capture(captureBuilder?.build(), CaptureCallback, null)

        } catch (e: CameraAccessException) {
            Log.e(TAG, "Error during capturing picture")
        }

    }

    private fun runPreCaptureSequence() {
        try {
            mPreviewRequestBuilder?.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER, CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START)
            mPreviewState = STATE_WAITING_PRE_CAPTURE
            mCaptureSession?.capture(mPreviewRequestBuilder?.build(), captureCallback, mBackgroundHandler)
        } catch (e: CameraAccessException) {
        }

    }


    override fun setFlashMode(flashMode: Int) {
        setFlashModeAndBuildPreviewRequest(flashMode)
    }


    override fun closeCamera(closeListener: CameraCloseListener<String>?) {
        mBackgroundHandler?.post {
            closeCamera()
            mUiiHandler.post { closeListener?.onCameraClosed(mCameraId ?: "") }
        }
    }

    private fun closeCamera() {
        closePreviewSession()
        releaseTexture()
        closeCameraDevice()
        closeImageReader()
    }

    private fun closePreviewSession() {
        mCaptureSession?.let {
            it.close()
            try {
                it.abortCaptures()
            } catch (ignore: Exception) {
            } finally {
                mCaptureSession = null
            }
        }
    }

    private fun releaseTexture() {
        if (null != mSurfaceTexture) {
            mSurfaceTexture?.release()
            mSurfaceTexture = null
        }
    }

    private fun closeImageReader() {
        if (null != mImageReader) {
            mImageReader?.close()
            mImageReader = null
        }
    }

    private fun closeCameraDevice() {
        if (null != mCameraDevice) {
            mCameraDevice?.close()
            mCameraDevice = null
        }
    }

    private fun lockFocus() {
        try {
            mPreviewRequestBuilder?.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START)

            mPreviewState = STATE_WAITING_LOCK
            mCaptureSession?.capture(mPreviewRequestBuilder?.build(), captureCallback, mBackgroundHandler)
        } catch (ignore: Exception) {
        }

    }

    override fun cameraPictureTaken(outFile: File, listener: CameraPictureListener) {
        this.mCameraPictureListener = listener
        mOutputPath = outFile
        mBackgroundHandler?.post { lockFocus() }
    }

    override fun getPhotoOrientation(@CameraConfig.SensorPosition sensorPosition: Int): Int {
        val degrees: Int
        when (sensorPosition) {
            CameraConfig.SENSOR_POSITION_UP -> degrees = 0
            CameraConfig.SENSOR_POSITION_LEFT -> degrees = 90
            CameraConfig.SENSOR_POSITION_UP_SIDE_DOWN -> degrees = 180
            CameraConfig.SENSOR_POSITION_RIGHT -> degrees = 270
            CameraConfig.SENSOR_POSITION_UNSPECIFIED -> degrees = 0
            else -> degrees = 0
        }// Natural orientation
        // Landscape left
        // Upside down
        // Landscape right

        val rotate: Int
        if (mCameraId == mFaceFrontCameraId) {
            rotate = (360 + mFaceFrontCameraOrientation + degrees) % 360
        } else {
            rotate = (360 + mFaceBackCameraOrientation - degrees) % 360
        }
        return rotate
    }



    @IntDef(STATE_PREVIEW, STATE_WAITING_LOCK, STATE_WAITING_PRE_CAPTURE, STATE_WAITING_NON_PRE_CAPTURE, STATE_PICTURE_TAKEN)
    @Retention(RetentionPolicy.SOURCE)
    internal annotation class CameraPreviewState


}