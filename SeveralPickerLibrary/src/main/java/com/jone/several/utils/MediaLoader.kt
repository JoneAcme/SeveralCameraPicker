package com.jone.several.utils

import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.FragmentActivity
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.text.TextUtils
import com.jone.several.R
import com.jone.several.model.MediaEntity
import com.jone.several.model.MediaFolder
import java.io.File
import java.util.*

/**
 * @fileName MediaLoader
 * Created by YiangJone on 2018/6/5.
 * @describe
 */

object MediaLoader {
    var type = 1000

    val AUDIO = "audio"
    val IMAGE = "image"
    val VIDEO = "video"
    val DURATION = "duration"

    private val LATITUDE = "latitude"
    private val LONGITUDE = "longitude"
    private val IMAGE_PROJECTION = arrayOf(MediaStore.Images.Media._ID,
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.DATE_ADDED,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.WIDTH,
            MediaStore.MediaColumns.HEIGHT,
            LATITUDE,
            LONGITUDE)

    /**
     * 图片 - SELECTION
     */
    private val IMAGE_SELECTION = (
            MediaStore.Images.Media.MIME_TYPE + "=? or " +
                    MediaStore.Images.Media.MIME_TYPE + "=?"
                    + " or "
                    + MediaStore.Images.Media.MIME_TYPE + "=?"
                    + " AND "
                    + MediaStore.MediaColumns.WIDTH +
                    ">0"
            )

    /**
     * 图片 - SELECTION_ARGS
     */
    private val IMAGE_SELECTION_ARGS = arrayOf("image/jpeg", "image/png", "image/webp")

    /**
     * 全部媒体数据 - PROJECTION
     */
    private val ALL_PROJECTION = arrayOf(MediaStore.Images.Media._ID,
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.DATE_ADDED,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.WIDTH,
            MediaStore.MediaColumns.HEIGHT,
            LATITUDE,
            LONGITUDE,
            DURATION)

    interface LocalMediaLoadListener {
        fun loadComplete(folders: MutableList<MediaFolder>)
    }


    fun loadAllMedia(mActivity: FragmentActivity,listener:LocalMediaLoadListener) {
        mActivity.supportLoaderManager.initLoader(type, null, object : LoaderManager.LoaderCallbacks<Cursor> {
            override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
                if (data == null) {
                    return
                }

                try {
                    val imageFolders = ArrayList<MediaFolder>()
//                            val allImageFolder = MediaFolder()
                    val allImageFolder = MediaFolder("", "", "", 0, 0, true, ArrayList())
                    val latelyImages = ArrayList<MediaEntity>()
                    val count = data.count
                    if (count > 0) {
                        data.moveToFirst()
                        do {
                            val path = data.getString(data.getColumnIndexOrThrow(ALL_PROJECTION[1]))
                            // 如原图路径不存在或者路径存在但文件不存在,就结束当前循环
                            if (TextUtils.isEmpty(path) || !File(path).exists()) {
                                continue
                            }
                            val mimeType = data.getString(data.getColumnIndexOrThrow(ALL_PROJECTION[4]))
                            var fileType = 0
                            var duration = 0L
                            fileType = 1

                            val size = data.getLong(data.getColumnIndexOrThrow(ALL_PROJECTION[5]))
                            val width = data.getInt(data.getColumnIndexOrThrow(ALL_PROJECTION[6]))
                            val height = data.getInt(data.getColumnIndexOrThrow(ALL_PROJECTION[7]))
                            val latitude = data.getDouble(data.getColumnIndexOrThrow(ALL_PROJECTION[8]))
                            val longitude = data.getDouble(data.getColumnIndexOrThrow(ALL_PROJECTION[9]))
                            val image = MediaEntity.newBuilder()
                                    .localPath(path)
                                    .duration(duration)
                                    .fileType(fileType)
                                    .mimeType(mimeType)
                                    .size(size)
                                    .width(width)
                                    .height(height)
                                    .latitude(latitude)
                                    .longitude(longitude)
                                    .build()

                            val folder = getImageFolder(path, imageFolders)
                            val images = folder.images
                            images.add(image)
                            folder.imageNumber = folder.imageNumber + 1
                            latelyImages.add(image)
                            val imageNum = allImageFolder.imageNumber
                            allImageFolder.imageNumber = imageNum + 1
                        } while (data.moveToNext())

                        if (latelyImages.size > 0) {
                            sortFolder(imageFolders)
                            imageFolders.add(0, allImageFolder)
                            allImageFolder.firstImagePath = latelyImages[0].localPath
//                            val title = if (type == MimeType.ofAudio())
//                                activity.getString(R.string.picture_all_audio)
//                            else
                            val title = mActivity.getString(R.string.picture_camera_roll)
                            allImageFolder.name = title
                            allImageFolder.images = latelyImages
                        }
                        listener.loadComplete(imageFolders)
                    } else {
                        // 如果没有相册
                        listener.loadComplete(imageFolders)
                    }
                } catch (e: Exception) {

                }
            }

            override fun onLoaderReset(loader: Loader<Cursor>) {
            }

            override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
                return CursorLoader(
                        mActivity,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        IMAGE_PROJECTION,
                        IMAGE_SELECTION,
                        IMAGE_SELECTION_ARGS,
                        MediaStore.Files.FileColumns.DATE_ADDED + " DESC")
            }


        })
    }

    private fun getImageFolder(path: String, imageFolders: MutableList<MediaFolder>): MediaFolder {
        val imageFile = File(path)
        val folderFile = imageFile.parentFile

        for (folder in imageFolders) {
            if (folder.name == folderFile.name) {
                return folder
            }
        }
        val newFolder = MediaFolder(folderFile.name, folderFile.absolutePath, path, 0, 0, true, ArrayList())
//        newFolder.name =folderFile.name
//        newFolder.path =folderFile.absolutePath
//        newFolder.firstImagePath =path
        imageFolders.add(newFolder)
        return newFolder
    }

    private fun sortFolder(imageFolders: List<MediaFolder>) {
        // 文件夹按图片数量排序
        Collections.sort(imageFolders, Comparator<MediaFolder> { lhs, rhs ->
            if (lhs.images == null || rhs.images == null) {
                return@Comparator 0
            }
            val lsize = lhs.imageNumber
            val rsize = rhs.imageNumber
            if (lsize == rsize) 0 else if (lsize < rsize) 1 else -1
        })
    }
}
