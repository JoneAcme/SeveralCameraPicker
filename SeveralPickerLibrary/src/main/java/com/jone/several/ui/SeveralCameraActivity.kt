package com.jone.several.ui

import android.Manifest
import android.os.Bundle
import android.util.Log
import com.google.android.cameraview.CameraView
import com.google.android.cameraview.compress.inter.CompressListener
import com.google.android.cameraview.configs.CameraViewOptions
import com.jone.several.R
import com.jone.several.config.PickerConstant
import com.jone.several.model.EventEntity
import com.jone.several.model.MediaEntity
import com.jone.several.model.rxbus.RxBus
import com.jone.several.model.rxbus.Subscribe
import com.jone.several.model.rxbus.ThreadMode
import com.jone.several.utils.Jumper
import com.jone.several.utils.loadCameraPreviewImage
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_several_camera.*
import java.util.ArrayList

class SeveralCameraActivity : PickerBaseActivity(), CompressListener {
    private val TAG = "SeveralCameraActivity"

    private val mCameraMediaList = ArrayList<MediaEntity>()
    private var pickedMediaList = ArrayList<MediaEntity>()

    private val FLASH_ICONS = intArrayOf(R.drawable.ic_flash_auto, R.drawable.ic_flash_off, R.drawable.ic_flash_on)

    private val FLASH_OPTIONS = intArrayOf(CameraView.FLASH_AUTO, CameraView.FLASH_OFF, CameraView.FLASH_ON)

    private var currentFlash = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_several_camera)

        if (!RxBus.default.isRegistered(this))
            RxBus.default.register(this)
        initCameraView()
        initClick()
    }

    private fun initCameraView() {
        val viewOptions = CameraViewOptions.Builder(this).setCompressListener(this).create()
        mCameraView.setCameraOption(viewOptions)
    }

    private fun initClick() {
        ivFlash.setOnClickListener {
            currentFlash++
            ivFlash.setImageResource(FLASH_ICONS[currentFlash % 3])
            mCameraView.flash = FLASH_OPTIONS[currentFlash % 3]
        }

        btnCameraPic.setOnClickListener {
            if (pickedMediaList.size >= option.maxPickNumber){
                pickedMediaList.removeAt(0)
            }
            mCameraView.takePicture()
        }

        btnChooice.setOnClickListener {
            mCameraView.swithCamera()
        }

        ivCameraClose.setOnClickListener {
            mCameraView.stopAndReleaseCamera()
            finish()
        }

        ivPreview.setOnClickListener {
            startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        startCamera()
    }

    private fun startCamera() {
        RxPermissions(this).request(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        ).subscribe { t: Boolean ->
            if (t) {
                mCameraView.openCamera()
            } else {
                finish()
            }
        }

    }

    override fun onPause() {
        try {
            stopCamera()
        } catch (e: Exception) {
            Log.e(TAG, "stopCamera camera fail", e)
        }
        super.onPause()
    }

    private fun stopCamera() {
        try {
            mCameraView.stopCamera()
        } catch (e: Exception) {
            Log.e(TAG, "stopCamera camera fail", e)
        }
    }


    override fun onStartCompress() {
        Log.e(TAG, "onStartCompress")
    }

    override fun onCompressFail() {
        Log.e(TAG, "onCompressFail")
    }

    override fun onCompressSuccess(action: Int, localPath: String, compressPath: String) {
        loadCameraPreviewImage(compressPath, ivPreview)
        val mediaEntity = MediaEntity.newBuilder()
                .localPath(localPath)
                .build()
        mCameraMediaList.add(mediaEntity)
        pickedMediaList.add(mediaEntity)
        updatePickerActivity()

        if (pickedMediaList.size >= option.maxPickNumber){
            startPreview(mCameraMediaList.size - 1)
        }
    }


    private fun updatePickerActivity() {
        val obj = EventEntity(PickerConstant.FLAG_PREVIEW_UPDATE_SELECT, pickedMediaList, pickedMediaList.size)
        RxBus.default.post(obj)
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
                mCameraView.stopAndReleaseCamera()
                finish()
            }
            PickerConstant.FLAG_PREVIEW_UPDATE_SELECT -> {
                pickedMediaList = obj.mediaEntities as ArrayList<MediaEntity>
            }
        }
    }

    private fun startPreview(position: Int = 0) {
        if (mCameraMediaList.isEmpty()) return
        Jumper.showPreviewView(this, mCameraMediaList, pickedMediaList, position)
    }

}
