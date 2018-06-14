package com.jone.several.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaActionSound
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.annotation.RequiresApi
import android.util.Log
import android.view.View
import com.jone.several.R
import com.jone.several.config.*
import com.jone.several.lifecycle.BaseLifecycle
import com.jone.several.lifecycle.Camera1Lifecycle
import com.jone.several.lifecycle.Camera2Lifecycle
import com.jone.several.listener.CameraView
import com.jone.several.model.EventEntity
import com.jone.several.model.MediaEntity
import com.jone.several.model.rxbus.RxBus
import com.jone.several.model.rxbus.Subscribe
import com.jone.several.model.rxbus.ThreadMode
import com.jone.several.utils.*
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.picker_activity_camera.*
import java.util.ArrayList
import kotlin.properties.Delegates

class CameraActivityPicker : PickerBaseActivity(), CameraView {
    private val MIN_VERSION_ICECREAM = Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1

    private val DIRECTORY_NAME = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath + "/Camera"

    private lateinit var mCameraLifecycle: BaseLifecycle
    private var mSensorManager: SensorManager by Delegates.notNull()
    private var mCameraConfigProvider: CameraConfigProvider by Delegates.notNull()
    private val mCameraMediaList = ArrayList<MediaEntity>()
    private var pickedMediaList = ArrayList<MediaEntity>()

    @SuppressLint("StringFormatMatches")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        if (!RxBus.default.isRegistered(this))
            RxBus.default.register(this)
        mSensorManager = getSystemService(Activity.SENSOR_SERVICE) as SensorManager
        this.mCameraConfigProvider = CameraConfigProviderImpl()
        this.mCameraConfigProvider.setCameraConfig(CameraConfig.Builder().build())

        RxPermissions(this).request(Manifest.permission.CAMERA,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).subscribe {
            setContentView(R.layout.picker_activity_camera)
            val hasCamera2 = CameraUtils.hasCamera2(this)
            Log.e("Activity", "has Camera2:$hasCamera2")
            if (hasCamera2) {
                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                mCameraLifecycle = Camera2Lifecycle(this, mCameraConfigProvider, this)
            } else {
                mCameraLifecycle = Camera1Lifecycle(this, mCameraConfigProvider, this)
            }
        }
        btn.setOnClickListener {

            if (Build.VERSION.SDK_INT > MIN_VERSION_ICECREAM && pickerOption.enableClickSound) {
                MediaActionSound().play(MediaActionSound.SHUTTER_CLICK)
            }
            mCameraLifecycle.cameraPictureTaken(DIRECTORY_NAME)
        }
        ivPreview.setOnClickListener {
            startPreview()
        }
        ivCameraClose.setOnClickListener {
            finish()
        }
        initConfig()
    }

    private fun initConfig() {

        val defaultOrientation = DeviceUtils.getDeviceDefaultOrientation(this)
        when (defaultOrientation) {
            android.content.res.Configuration.ORIENTATION_LANDSCAPE -> mCameraConfigProvider.deviceDefaultOrientation = CameraConfig.ORIENTATION_LANDSCAPE
            else -> mCameraConfigProvider.deviceDefaultOrientation = CameraConfig.ORIENTATION_PORTRAIT
        }

        when (mCameraConfigProvider.flashMode) {
            CameraConfig.FLASH_MODE_AUTO -> setFlashMode(Flash.FLASH_AUTO)
            CameraConfig.FLASH_MODE_ON -> setFlashMode(Flash.FLASH_ON)
            CameraConfig.FLASH_MODE_OFF -> setFlashMode(Flash.FLASH_OFF)
        }
    }

    fun setFlashMode(@Flash.FlashMode mode: Int) {
        mCameraConfigProvider.flashMode = CameraConfig.FLASH_MODE_AUTO
        this.mCameraLifecycle.setFlashMode(CameraConfig.FLASH_MODE_AUTO)
    }

    override fun updateCameraPreview(previewSize: Size, cameraPreview: View) {
        flCamera?.let {
            it.removeAllViews()
            it.addView(cameraPreview)
            it.setAspectRatio((previewSize.height).toDouble() / (previewSize.width).toDouble())
        }

    }

    override fun releaseCameraPreview() {
    }

    override fun onPhotoTaken(outPath: String) {
//        if (Build.VERSION.SDK_INT > MIN_VERSION_ICECREAM && pickerOption.enableClickSound) {
//            MediaActionSound().play(MediaActionSound.SHUTTER_CLICK)
//        }
        if (pickedMediaList.isNotEmpty()&&pickedMediaList.size >= pickerOption.maxPickNumber) {
            pickedMediaList.removeAt(0)
        }
        loadCameraPreviewImage(outPath, ivPreview)
        val mediaEntity = MediaEntity.newBuilder()
                .localPath(outPath)
                .build()
        mCameraMediaList.add(mediaEntity)
        pickedMediaList.add(mediaEntity)
        /**刷新系统MediaStore*/
        try {
            MediaScannerConnection.scanFile(this, arrayOf(outPath), null
            ) { path, uri -> }
        } catch (ignore: Exception) {
        }
        if (pickedMediaList.size >= pickerOption.maxPickNumber){
            startPreview(mCameraMediaList.size - 1)
        }
        updatePickerActivity()
    }

    private fun startPreview(position: Int = 0) {
        if (mCameraMediaList.isEmpty()) return
        Navigator.showPreviewView(this, pickerOption, mCameraMediaList, pickedMediaList, position)
    }

    private fun updatePickerActivity() {
        val obj = EventEntity(PickerConstant.FLAG_PREVIEW_UPDATE_SELECT, pickedMediaList, pickedMediaList.size)
        RxBus.default.post(obj)
    }

    override fun onPhotoTakeFail() {
        showToast("拍照失败！")
    }

    override fun onResume() {
        super.onResume()
        mCameraLifecycle.onResume()
        mSensorManager.registerListener(mSensorEventListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)

    }

    private val mSensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(sensorEvent: SensorEvent) {
            synchronized(this) {
                if (sensorEvent.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    if (sensorEvent.values[0] < 4 && sensorEvent.values[0] > -4) {
                        if (sensorEvent.values[1] > 0) {
                            // UP
                            mCameraConfigProvider.sensorPosition = (CameraConfig.SENSOR_POSITION_UP)
                            mCameraConfigProvider.degrees = (if (mCameraConfigProvider.deviceDefaultOrientation === CameraConfig.ORIENTATION_PORTRAIT) 0 else 90)
                        } else if (sensorEvent.values[1] < 0) {
                            // UP SIDE DOWN
                            mCameraConfigProvider.sensorPosition = (CameraConfig.SENSOR_POSITION_UP_SIDE_DOWN)
                            mCameraConfigProvider.degrees = (if (mCameraConfigProvider.deviceDefaultOrientation === CameraConfig.ORIENTATION_PORTRAIT) 180 else 270)
                        }
                    } else if (sensorEvent.values[1] < 4 && sensorEvent.values[1] > -4) {
                        if (sensorEvent.values[0] > 0) {
                            // LEFT
                            mCameraConfigProvider.sensorPosition = (CameraConfig.SENSOR_POSITION_LEFT)
                            mCameraConfigProvider.degrees = (if (mCameraConfigProvider.deviceDefaultOrientation === CameraConfig.ORIENTATION_PORTRAIT) 90 else 180)
                        } else if (sensorEvent.values[0] < 0) {
                            // RIGHT
                            mCameraConfigProvider.sensorPosition = (CameraConfig.SENSOR_POSITION_RIGHT)
                            mCameraConfigProvider.degrees = (if (mCameraConfigProvider.deviceDefaultOrientation === CameraConfig.ORIENTATION_PORTRAIT) 270 else 0)
                        }
                    }
                    onScreenRotation(mCameraConfigProvider.degrees)
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, i: Int) {

        }
    }

    override fun onPause() {
        super.onPause()
        mCameraLifecycle.onPause()
        mSensorManager.unregisterListener(mSensorEventListener)
    }

    protected fun onScreenRotation(degrees: Int) {
//        if (mCameraStateListener != null) {
//            mCameraStateListener.shouldRotateControls(degrees)
//        }
//        rotateSettingsDialog(degrees)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (RxBus.default.isRegistered(this))
            RxBus.default.unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun eventBus(obj: EventEntity) {
        when (obj.what) {
            PickerConstant.FLAG_PREVIEW_COMPLETE -> {
                mCameraLifecycle.onDestroy()
                finish()
            }
            PickerConstant.FLAG_PREVIEW_UPDATE_SELECT -> {
                pickedMediaList = obj.mediaEntities as ArrayList<MediaEntity>
            }
        }
    }

}
