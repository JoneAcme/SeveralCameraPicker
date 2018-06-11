package com.jone.several.listener

import android.view.View
import com.jone.several.utils.Size

/**
 * @fileName CameraView
 * Created by YiangJone on 2018/6/4.
 * @describe
 */

interface CameraView {
     fun updateCameraPreview(previewSize : Size, cameraPreview: View)
     fun releaseCameraPreview()

     fun onPhotoTaken(outPath:String)
     fun onPhotoTakeFail()
}