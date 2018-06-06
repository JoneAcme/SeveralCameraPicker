package com.jone.sevral.ui

import android.Manifest
import android.app.Activity
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.annotation.RequiresApi
import android.util.Log
import android.view.View
import android.widget.Toast
import com.jone.sevral.R
import com.jone.sevral.config.CameraConfig
import com.jone.sevral.config.CameraConfigProvider
import com.jone.sevral.config.CameraConfigProviderImpl
import com.jone.sevral.config.PickerOption
import com.jone.sevral.lifecycle.BaseLifecycle
import com.jone.sevral.lifecycle.Camera1Lifecycle
import com.jone.sevral.lifecycle.Camera2Lifecycle
import com.jone.sevral.listener.CameraView
import com.jone.sevral.model.MediaEntity
import com.jone.sevral.utils.*
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList
import kotlin.properties.Delegates

class CameraActivity : AppCompatActivity(), CameraView {
    private val DIRECTORY_NAME = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath + "/Camera"

    private lateinit var mCameraLifecycle: BaseLifecycle
    var mSensorManager: SensorManager by Delegates.notNull()
    var mCameraConfigProvider: CameraConfigProvider by Delegates.notNull()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mSensorManager = getSystemService(Activity.SENSOR_SERVICE) as SensorManager
        this.mCameraConfigProvider = CameraConfigProviderImpl()
        this.mCameraConfigProvider.setCameraConfig(CameraConfig.Builder().build())


        RxPermissions(this).request(Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe {
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
            mCameraLifecycle.cameraPictureTaken(DIRECTORY_NAME)
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
        flCamera.setOnTouchListener { v, event ->
            mCameraLifecycle.focusOnTouch(flCamera.width,flCamera.height,event)
             true
        }

    }

    override fun releaseCameraPreview() {
    }

    override fun onPhotoTaken(outPath: String) {
        val mediaList = ArrayList<MediaEntity>()
        val mediaEntity = MediaEntity.newBuilder()
                .localPath(outPath)
                .build()
        mediaList.add(mediaEntity)
        Navigator.showPreviewView(this, PickerOption(), mediaList, mediaList, 0)
    }

    override fun onPhotoTakeFail() {
        Toast.makeText(this, "onPhotoTakeFail", Toast.LENGTH_SHORT).show()
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

}
