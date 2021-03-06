package com.jone.several.ui.adapter

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.ImageView
import com.jone.several.R
import com.jone.several.SeveralImagePicker
import com.jone.several.config.MimeType
import com.jone.several.config.PickerConstant
import com.jone.several.model.MediaEntity
import com.jone.several.utils.*
import kotlinx.android.synthetic.main.item_several_camera.view.*
import kotlinx.android.synthetic.main.item_several_grid_media.view.*
import java.util.ArrayList

/**
 * @fileName PickerAdapter
 * Created by YiangJone on 2018/6/5.
 * @describe
 */


class PickerAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var enableCamera = false
    private var onPickChangedListener: OnPickChangedListener? = null
    private val maxSelectNum: Int
    private val allMediaList: MutableList<MediaEntity> = ArrayList()
    private val pickMediaList: MutableList<MediaEntity> = ArrayList()
    private val enablePreview: Boolean
    private val enableVoice: Boolean
    private val overrideWidth: Int
    private val overrideHeight: Int
    private val animation: Animation by lazy { AnimationLoader.loadAnimation(context, R.anim.picker_anim_window_in) }
    private val zoomAnim: Boolean
    private val numPick: Boolean
    var isExceedMax: Boolean = false

    init {
        val option = SeveralImagePicker.pickerOption
        this.enableCamera = option.enableCamera
        this.maxSelectNum = option.maxPickNumber
        this.enablePreview = option.enablePreview
        this.overrideWidth = option.thumbnailWidth
        this.overrideHeight = option.thumbnailHeight
        this.enableVoice = option.enableClickSound
        this.zoomAnim = option.enableAnimation
        this.numPick = option.enableNumPick

    }

    fun setAllMediaList(medias: MutableList<MediaEntity>) {
        allMediaList.clear()
        allMediaList.addAll(medias)
        notifyDataSetChanged()
    }

    fun getAllMediaList(): MutableList<MediaEntity> {
        return allMediaList
    }

    fun setPickMediaList(medias: MutableList<MediaEntity>) {
        pickMediaList.clear()
        pickMediaList.addAll(medias)
        subSelectPosition()
        onPickChangedListener?.onChange(pickMediaList)
    }

    fun getPickMediaList(): MutableList<MediaEntity> {
        return pickMediaList
    }

    override fun getItemViewType(position: Int): Int {
        if (enableCamera && position == 0) {
            return PickerConstant.TYPE_CAMERA
        } else {
            return PickerConstant.TYPE_PICTURE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == PickerConstant.TYPE_CAMERA) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_several_camera, parent, false)
            return HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_several_grid_media, parent, false)
            return ContentViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == PickerConstant.TYPE_CAMERA) {
            val headerHolder = holder as HeaderViewHolder
            headerHolder.itemView.camera.setOnClickListener {
                onPickChangedListener?.onTakePhoto()
            }
        } else {
            val contentHolder = holder as ContentViewHolder
            val image = allMediaList[if (enableCamera) position - 1 else position]
            image.position = contentHolder.adapterPosition
            val path = image.finalPath
            val pictureType = image.mimeType
            if (numPick) {
                notifyCheckChanged(contentHolder, image)
            }
            selectImage(contentHolder, isSelected(image), false)

            val gif = MimeType.isGif(pictureType)
            contentHolder.itemView.tv_isGif.visibility = if (gif) View.VISIBLE else View.GONE
            val width = image.width
            val height = image.height
            val h = width * 5
            contentHolder.itemView.tv_long_chart.visibility = if (height > h) View.VISIBLE else View.GONE
            context.loadImage(path, contentHolder.itemView.iv_picture)
            if (enablePreview) {
                contentHolder.itemView.ll_check.setOnClickListener { changeCheckboxState(contentHolder, image) }

            }
            contentHolder.itemView.setOnClickListener {
                if (enablePreview) {
                    val index = if (enableCamera) position - 1 else position
                    onPickChangedListener!!.onPictureClick(image, index)
                } else {
                    changeCheckboxState(contentHolder, image)
                }
            }
        }
    }


    override fun getItemCount(): Int {
        return if (enableCamera) allMediaList.size + 1 else allMediaList.size
    }

    inner class HeaderViewHolder(headerView: View) : RecyclerView.ViewHolder(headerView) {
        init {
            val params = headerView.srl_camera.layoutParams
            params.width = overrideWidth
            params.height = overrideHeight
            headerView.srl_camera.layoutParams = params
        }
    }


    inner class ContentViewHolder(contentView: View) : RecyclerView.ViewHolder(contentView) {
        init {
            val params = contentView.iv_picture.layoutParams
            params.width = overrideWidth
            params.height = overrideHeight
            contentView.iv_picture.layoutParams = params
        }
    }

    fun isSelected(image: MediaEntity): Boolean {
        for (mediaEntity in pickMediaList) {
            if (TextUtils.isEmpty(mediaEntity.localPath) || TextUtils.isEmpty(image.localPath)) {
                return false
            }
            if (mediaEntity.localPath == image.localPath) {
                return true
            }
        }
        return false
    }

    /**
     * 选择按钮更新
     */
    private fun notifyCheckChanged(contentViewHolder: ContentViewHolder, imageBean: MediaEntity) {
        contentViewHolder.itemView.tv_check.text = ""
        for (mediaEntity in pickMediaList) {
            if (mediaEntity.localPath == imageBean.localPath) {
                imageBean.number = mediaEntity.number
                mediaEntity.setPosition(imageBean.getPosition())
                contentViewHolder.itemView.tv_check.text = imageBean.number.toString()
            }
        }
    }

    /**
     * 改变图片选中状态
     * @param contentHolderContent contentHolderContent
     * *
     * @param image         image
     */
    @SuppressLint("StringFormatMatches")
    private fun changeCheckboxState(contentHolderContent: ContentViewHolder, image: MediaEntity) {
        val isChecked = contentHolderContent.itemView.tv_check.isSelected
        if (isChecked) {
            for (mediaEntity in pickMediaList) {
                if (mediaEntity.localPath == image.localPath) {
                    pickMediaList.remove(mediaEntity)
                    subSelectPosition()
                    disZoom(contentHolderContent.itemView.iv_picture)
                    break
                }
            }
        } else {
            if (isExceedMax) {
                notifyDataSetChanged()
                context.showToast(context.getString(R.string.message_max_number, maxSelectNum))
                return
            }

            pickMediaList.add(image)
            image.number = pickMediaList!!.size
            zoom(contentHolderContent.itemView.iv_picture)
        }

        //通知点击项发生了改变
        isExceedMax = pickMediaList.size >= maxSelectNum && maxSelectNum != 0
        if (isExceedMax || pickMediaList.size == maxSelectNum - 1) {
            notifyDataSetChanged()
        } else {
            notifyItemChanged(contentHolderContent.adapterPosition)
            selectImage(contentHolderContent, !isChecked, false)
        }
        if (onPickChangedListener != null) {
            onPickChangedListener!!.onChange(pickMediaList)
        }
    }

    /**
     * 更新选择的顺序
     */
    private fun subSelectPosition() {
        if (numPick) {
            val size = pickMediaList.size
            var index = 0
            val length = size
            while (index < length) {
                val mediaEntity = pickMediaList[index]
                mediaEntity.number = index + 1
                notifyItemChanged(mediaEntity.position)
                index++
            }
        }
    }

    private fun selectImage(contentViewHolder: ContentViewHolder, isChecked: Boolean, isAnim: Boolean) {
        contentViewHolder.itemView.tv_check.isSelected = isChecked
        if (isChecked) {
            if (isAnim) {
                contentViewHolder.itemView.tv_check.startAnimation(animation)
            }
            contentViewHolder.itemView.iv_picture.setColorFilter(ContextCompat.getColor(context, R.color.color_black_4), PorterDuff.Mode.SRC_ATOP)
        } else {
            if (isExceedMax) {
                contentViewHolder.itemView.iv_picture.setColorFilter(ContextCompat.getColor(context, R.color.picker_transparent_white), PorterDuff.Mode.SRC_ATOP)
            } else {
                contentViewHolder.itemView.iv_picture.setColorFilter(ContextCompat.getColor(context, R.color.color_black_5), PorterDuff.Mode.SRC_ATOP)
            }
        }
    }

    interface OnPickChangedListener {
        fun onTakePhoto()

        fun onChange(selectImages: List<MediaEntity>)

        fun onPictureClick(mediaEntity: MediaEntity, position: Int)
    }

    fun setOnPickChangedListener(onPickChangedListener: OnPickChangedListener) {
        this.onPickChangedListener = onPickChangedListener
    }

    private fun zoom(iv_img: ImageView) {
        if (zoomAnim) {
            val set = AnimatorSet()
            set.playTogether(
                    ObjectAnimator.ofFloat(iv_img, "scaleX", 1f, 1.12f),
                    ObjectAnimator.ofFloat(iv_img, "scaleY", 1f, 1.12f)
            )
            set.duration = DURATION.toLong()
            set.start()
        }
    }

    private fun disZoom(iv_img: ImageView) {
        if (zoomAnim) {
            val set = AnimatorSet()
            set.playTogether(
                    ObjectAnimator.ofFloat(iv_img, "scaleX", 1.12f, 1f),
                    ObjectAnimator.ofFloat(iv_img, "scaleY", 1.12f, 1f)
            )
            set.duration = DURATION.toLong()
            set.start()
        }
    }

    companion object {
        private val DURATION = 450
    }
}
