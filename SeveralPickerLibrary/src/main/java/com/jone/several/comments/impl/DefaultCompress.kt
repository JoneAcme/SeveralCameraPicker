package com.jone.several.comments.impl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.jone.several.comments.inter.CompressInterface
import com.jone.several.config.PickerOption
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream

/**
 * @fileName DefaultCompress
 * Created by YiangJone on 2018/6/8.
 * @describe
 */

class DefaultCompress : CompressInterface {
    override fun compress(mContext: Context, imgPath: String, pickerOption: PickerOption): String {
        var bitmap: Bitmap? = null
        try {
            bitmap = BitmapFactory.decodeFile(imgPath)

            if (null == bitmap) return imgPath

            var compressPath = pickerOption.compresssPath
            if (compressPath.isNullOrEmpty())
                compressPath = mContext.cacheDir.absolutePath

            var name = "${System.currentTimeMillis()}.jpg"
            compressPath = "$compressPath/$name"
            val outStream = FileOutputStream(compressPath)
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, pickerOption.compressQuality, bos)
            outStream.write(bos.toByteArray())

            bitmap.recycle()
            return compressPath
        } catch (e: Exception) {
            e.printStackTrace()
            bitmap?.recycle()
            return imgPath
        }
    }
}