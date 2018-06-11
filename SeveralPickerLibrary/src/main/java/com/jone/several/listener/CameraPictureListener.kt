package com.jone.several.listener

/**
 * @fileName CameraPictureListener
 * Created by YiangJone on 2018/6/4.
 * @describe
 */

interface CameraPictureListener{
    fun onPictureTaken(path: String)
    fun onPictureTakeError()
}