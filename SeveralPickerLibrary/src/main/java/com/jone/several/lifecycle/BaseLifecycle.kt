package com.jone.several.lifecycle

import com.jone.several.config.CameraConfig

/**
 * @fileName BaseLifecycle
 * Created by YiangJone on 2018/6/4.
 * @describe
 */

abstract class BaseLifecycle {
    abstract fun onResume()
    abstract fun onPause()

    abstract fun onDestroy()

    abstract fun cameraPictureTaken(outPath: String)

    abstract fun setFlashMode(@CameraConfig.FlashMode flashMode: Int)

}