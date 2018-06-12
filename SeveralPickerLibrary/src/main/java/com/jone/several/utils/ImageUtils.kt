package com.jone.several.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.IOException

/**
 * @fileName ImageUtils
 * Created by YiangJone on 2018/6/11.
 * @describe
 */


object ImageUtils {


    /**
     * 处理旋转后的图片
     * @param originpath 原图路径
     * @param context 上下文
     * @return 返回修复完毕后的图片路径
     */
    fun amendRotatePhoto(originpath: String): String {
        try {
            // 取得图片旋转角度
            val angle = readPictureDegree(originpath)

            // 把原图压缩后得到Bitmap对象
//        val bmp = BitmapFactory.decodeFile(originpath)

            var bmp = BitmapFactory.decodeFile(originpath)
            // 修复图片被旋转的角度
            val bitmap = rotaingImageView(angle, bmp)

            // 保存修复后的图片并返回保存后的图片路径
            writeBitmap(originpath, bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return originpath
    }

    private fun writeBitmap(originpath: String, bitmap: Bitmap) {
        try {
            if (null == bitmap) return
            val outStream = FileOutputStream(originpath)
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
            outStream.write(bos.toByteArray())

            bitmap.recycle()
        } catch (e: Exception) {
            e.printStackTrace()
            bitmap?.recycle()
        }
    }


    /**
     * 读取照片旋转角度
     *
     * @param path 照片路径
     * @return 角度
     */
    fun readPictureDegree(path: String): Int {
        var degree = 0
        try {
            val exifInterface = ExifInterface(path)
            val orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return degree
    }

    /**
     * 旋转图片
     * @param angle 被旋转角度
     * @param bitmap 图片对象
     * @return 旋转后的图片
     */
    fun rotaingImageView(angle: Int, bitmap: Bitmap): Bitmap {
        var returnBm: Bitmap? = null
        // 根据旋转角度，生成旋转矩阵
        val matrix = Matrix()
        matrix.postRotate(angle.toFloat())
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } catch (e: OutOfMemoryError) {
        }

        if (returnBm == null) {
            returnBm = bitmap
        }
        if (bitmap != returnBm) {
            bitmap.recycle()
        }
        return returnBm
    }
}